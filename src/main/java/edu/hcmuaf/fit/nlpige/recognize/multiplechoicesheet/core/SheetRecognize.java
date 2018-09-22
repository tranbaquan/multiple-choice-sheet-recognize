package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.FileNotFoundException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.NLPigeException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.RecognizeException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.imageviewer.ImageViewer;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;

public class SheetRecognize {

    private Mat input;
    private Mat canny;
    private int questionNum;

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

        ImageViewer imageViewer = new ImageViewer();
        imageViewer.show(thresh);
        return thresh;
    }

    public Rect detectBoundingBox(Mat processed) {
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

        Rect roi = new Rect();
        for (Map.Entry<Integer, MatOfPoint> box : boundingBox.entrySet()) {
            int index = box.getKey();
            double[] boxHierarchy = hierarchy.get(0, index);
            if (boxHierarchy[3] == -1) {
                roi = Imgproc.boundingRect(contours.get(index));
                break;
            }
        }

        roi.x += 2;
        roi.y += 2;
        roi.width -= 4;
        roi.height -= 4;

        Imgproc.rectangle(input, new Point(roi.x, roi.y),
                new Point(roi.x + roi.width, roi.y + roi.height), new Scalar(255, 0, 0), 1);

        ImageViewer imageViewer = new ImageViewer();
        imageViewer.show(input);

        return roi;
    }

    public List<MatOfPoint> detectRows(Rect roi) {
        Mat cut = canny.submat(roi);
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(cut, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        ArrayList<MatOfPoint> rows = new ArrayList<>();

        Mat m = input.submat(roi);

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);

            int width = rect.width;
            int height = rect.height;

            int ratio = Math.max(width, height) / Math.min(width, height);

            if (ratio > 10 && ratio < 20) {
                rows.add(contour);
                Imgproc.rectangle(m, new Point(rect.x, rect.y),
                        new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)), 1);
            }
        }

        ImageViewer imageViewer = new ImageViewer();
        imageViewer.show(m);

        if (rows.size() != (questionNum + 1)) {
            throw new RecognizeException("Can not recognize");

        }
        return rows;
    }
}
