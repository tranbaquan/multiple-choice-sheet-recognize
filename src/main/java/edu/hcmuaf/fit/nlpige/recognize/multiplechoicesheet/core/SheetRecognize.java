package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.FileNotFoundException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.RecognizeException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.imageviewer.ImageViewer;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;

public class SheetRecognize {

    private Mat input, canny;
    private Rect boundingRect;
    private int questionNum;
    private ImageViewer imageViewer = new ImageViewer();

    public int getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
    }

    public void readFile(String file) {
        this.input = Imgcodecs.imread(file);
        if (input.dataAddr() == 0) {
            throw new FileNotFoundException("Could not read file");
        }
    }

    public Mat imageProc() {
        Mat gray = new Mat(input.rows(), input.cols(), CvType.CV_8UC3);
        Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGR2GRAY);

        Mat blurred = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC3);
        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);

        Mat edged = new Mat(blurred.rows(), blurred.cols(), CvType.CV_8UC3);
        Imgproc.Canny(blurred, edged, 100, 255);
        this.canny = edged;

        Mat dilated = new Mat(edged.rows(), edged.cols(), CvType.CV_8UC3);
        Imgproc.dilate(edged, dilated, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(5, 5)));

        Mat thresh = new Mat(dilated.rows(), dilated.cols(), CvType.CV_8UC3);
        Imgproc.threshold(dilated, thresh, 150, 255, Imgproc.THRESH_BINARY);

        return thresh;
    }

    public void detectBoundingBox(Mat processed) {
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(processed.clone(), contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        HashMap<Integer, MatOfPoint> boundingBox = new HashMap<>();
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.005;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, false);

            MatOfPoint points = new MatOfPoint(approxCurve.toArray());
            if (points.toArray().length == 4) {
                boundingBox.put(i, contours.get(i));
            }
        }

        List<Rect> outerRects = new ArrayList<>();
        for (Map.Entry<Integer, MatOfPoint> box : boundingBox.entrySet()) {
            int index = box.getKey();
            double[] boxHierarchy = hierarchy.get(0, index);
            if (boxHierarchy[3] == -1) {
                Rect roi = Imgproc.boundingRect(contours.get(index));
                outerRects.add(roi);
            }
        }

        Rect roi = outerRects.stream().max(Comparator.comparing(rect -> rect.height)).get();

        roi.x += 2;
        roi.y += 2;
        roi.width -= 4;
        roi.height -= 4;

//        Imgproc.rectangle(input, new Point(roi.x, roi.y),
//                new Point(roi.x + roi.width, roi.y + roi.height), new Scalar(255, 0, 0), 3);
//
//        imageViewer.show(input);
        boundingRect = roi;
    }

    public List<Rect> detectRows() {
        Mat boundingMat = canny.submat(boundingRect);
        Mat boundingMat1 = input.submat(boundingRect);
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(boundingMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Rect> rows = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);

            int width = rect.width;
            int height = rect.height;

            int ratio = Math.max(width, height) / Math.min(width, height);

            if (ratio > 10 && ratio < 20) {
                rows.add(rect);
                Imgproc.rectangle(boundingMat1, new Point(rect.x, rect.y),
                new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0), 3);
            }
        }

        imageViewer.show(boundingMat1);
        rows.sort(Comparator.comparing(rect -> rect.y));
        rows.remove(0);

        if (rows.size() != (questionNum)) {
            throw new RecognizeException("Can not recognize");
        }

        return rows;
    }

    public List<List<Rect>> detectBubbles(List<Rect> records) {
        Mat bounding = canny.submat(boundingRect);

        ImageViewer imageViewer = new ImageViewer();
        imageViewer.show(bounding);

        List<List<Rect>> allOfChoices = new ArrayList<>();

        for (Rect rect : records) {
            rect.x += rect.width / 2;
            rect.width /= 2;
            Mat mat = bounding.submat(rect);

            ArrayList<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            List<Rect> choices = new ArrayList<>();

            for (int i = 0; i < contours.size(); i++) {
                Rect r = Imgproc.boundingRect(contours.get(i));
                double[] hierarchyBox = hierarchy.get(0, i);

                int w = r.width;
                int h = r.height;
                double ratio = (double) Math.max(w, h) / Math.min(w, h);

                if (ratio >= 0.9 && ratio <= 1.1 && hierarchyBox[3] == -1) {
                    choices.add(r);
                }
            }

            choices.sort(Comparator.comparing(r -> r.x));
            allOfChoices.add(choices);
        }

        return allOfChoices;
    }

    public List<Integer> recognizeAnswer(List<List<Rect>> allOfChoices, List<Rect> records) {
        List<Integer> answer = new ArrayList<>();
        Mat boundingMat = canny.submat(boundingRect);
        for (int i = 0; i < records.size(); i++) {
            Mat recordMat = boundingMat.submat(records.get(i));
            List<Rect> choiceOfRecord = allOfChoices.get(i);
            for (int j = 0; j < choiceOfRecord.size() ; j++) {
                Rect r2 = choiceOfRecord.get(j);
                Imgproc.rectangle(recordMat, new Point(r2.x, r2.y), new Point(r2.x +r2.width, r2.y + r2.height), new Scalar(255,0,0));
                Mat choice = recordMat.submat(choiceOfRecord.get(j));
                System.out.println(choice.type());
            }
            imageViewer.show(recordMat);
        }

        return answer;
    }
}
