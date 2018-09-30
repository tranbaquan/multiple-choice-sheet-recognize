package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.ErrorCode;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.FileNotFoundException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.NLPigeException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.RecognizeException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.imageviewer.ImageViewer;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;

import static edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.ErrorCode.RECOGNIZE_EXCEPTION;

public class SheetRecognize implements SheetRecognizable{

    private Mat input, edged, gray, blurred, thresh;
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
            throw new FileNotFoundException();
        }
    }

    @Override
    public void imageProc() {
        gray = new Mat(input.rows(), input.cols(), CvType.CV_8UC3);
        Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGR2GRAY);

        blurred = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC3);
        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);

        edged = new Mat(blurred.rows(), blurred.cols(), CvType.CV_8UC3);
        Imgproc.Canny(blurred, edged, 100, 255);

        Mat dilated = new Mat(edged.rows(), edged.cols(), CvType.CV_8UC3);
        Imgproc.dilate(edged, dilated, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(3, 3)));

        thresh = new Mat(dilated.rows(), dilated.cols(), CvType.CV_8UC3);
        Imgproc.threshold(dilated, thresh, 150, 255, Imgproc.THRESH_BINARY);
    }

    @Override
    public void detectBoundingBox() {
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresh.clone(), contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        HashMap<Integer, MatOfPoint> edgeRects = new HashMap<>();
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        for (int i = 0; i < contours.size(); i++) {

            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.005;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, false);

            MatOfPoint points = new MatOfPoint(approxCurve.toArray());
            if (points.toArray().length == 4) {
                edgeRects.put(i, contours.get(i));
            }
        }

        List<Rect> outerRects = new ArrayList<>();
        for (Map.Entry<Integer, MatOfPoint> edgeRect : edgeRects.entrySet()) {
            int index = edgeRect.getKey();
            double[] boxHierarchy = hierarchy.get(0, index);
            if (boxHierarchy[3] == -1) {
                Rect roi = Imgproc.boundingRect(contours.get(index));
                outerRects.add(roi);
            }
        }

        if (outerRects.size() == 0) {
           throw new RecognizeException();
        }

        Rect roi = outerRects.stream().max(Comparator.comparing(rect -> rect.height)).get();

        roi.x += 2;
        roi.y += 2;
        roi.width -= 4;
        roi.height -= 4;

//        Imgproc.rectangle(input, new Point(roi.x, roi.y),
//                new Point(roi.x + roi.width, roi.y + roi.height), new Scalar(255, 0, 0), 1);

        imageViewer.show(input);
        boundingRect = roi;
    }

    @Override
    public List<Rect> detectRows() {
        Mat boundingMat = thresh.submat(boundingRect);
        Mat boundingMat1 = input.submat(boundingRect);
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(boundingMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Rect> rows = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);

            int ratio = rect.width / rect.height;

            if (ratio > 10 && ratio < 15) {
                rows.add(rect);
                Imgproc.rectangle(boundingMat1, new Point(rect.x, rect.y),
                        new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)), 1);
            }
        }

        imageViewer.show(boundingMat1);
        rows.sort(Comparator.comparing(rect -> rect.y));
        rows.remove(0);

        if (rows.size() != questionNum) {
            throw new RecognizeException(RECOGNIZE_EXCEPTION);
        }

        return rows;
    }

    @Override
    public List<List<Rect>> detectBubbles(List<Rect> records) {
        Mat bounding = edged.submat(boundingRect);
        ImageViewer imageViewer = new ImageViewer();
        imageViewer.show(bounding);

        List<List<Rect>> recordsChoices = new ArrayList<>();

        for (Rect recordRect : records) {
            recordRect.x += recordRect.width / 2;
            recordRect.width /= 2;

            Mat mat = bounding.submat(recordRect);
            ArrayList<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            List<Rect> choices = new ArrayList<>();

            for (int i = 0; i < contours.size(); i++) {
                Rect choiceRect = Imgproc.boundingRect(contours.get(i));
                double[] choiceHierarchy = hierarchy.get(0, i);

                int w = choiceRect.width;
                int h = choiceRect.height;
                double ratio = (double) w / h;

                if (ratio >= 0.9 && ratio <= 1.1 && choiceHierarchy[3] == -1) {
                    choices.add(choiceRect);
                }
            }

            choices.sort(Comparator.comparing(r -> r.x));
            recordsChoices.add(choices);
        }

        return recordsChoices;
    }

    @Override
    public Object recognize() {
        return null;
    }


    public List<List<Integer>> recognizeAnswer(List<List<Rect>> recordsChoices, List<Rect> records) {
        List<List<Integer>> answer = new ArrayList<>();
        Mat boundingMat = edged.submat(boundingRect);
        for (int i = 0; i < records.size(); i++) {
            Mat recordMat = boundingMat.submat(records.get(i));
            List<Rect> choiceOfRecord = recordsChoices.get(i);
            List<Integer> a = new ArrayList<>();
            for (int j = 0; j < choiceOfRecord.size(); j++) {
                Rect r = choiceOfRecord.get(j);
                Mat choiceMat = recordMat.submat(r);

                int nonZero = Core.countNonZero(choiceMat);
                double ratio = (double) nonZero / (Math.pow(r.width / 2, 2) * 3.14);
                if (ratio < 0.3) {
                    a.add(j + 1);
                }
            }
            answer.add(a);
        }

        return answer;
    }
}
