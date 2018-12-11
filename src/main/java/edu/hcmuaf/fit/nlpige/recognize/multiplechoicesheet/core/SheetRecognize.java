package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.FileNotFoundException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.RecognizeException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.logging.Logger;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.MatType;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.PaperType;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.RationConst;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.utils.MatConverter;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.viewer.image.ImageViewer;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;
import java.util.stream.Collectors;

public class SheetRecognize extends Logger implements SheetRecognizable {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final ImageViewer imageViewer = new ImageViewer();
    private Mat input, edged, thresh;
    private Rect boundingRect;
    private int questionNum;
    private PaperType paperType;

    public SheetRecognize() {
        this.paperType = PaperType.A4;
    }

    public SheetRecognize(int questionNum, PaperType paperType) {
        this.questionNum = questionNum;
        this.paperType = paperType;
    }


    public int getQuestionNum() {
        return this.questionNum;
    }

    public void setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
    }

    public PaperType getPaperType() {
        return paperType;
    }

    public void setPaperType(PaperType paperType) {
        this.paperType = paperType;
    }

    public void readFile(String file) {
        log.info("Reading " + file);
        this.input = Imgcodecs.imread(file);
        if (input.dataAddr() == 0) {
            throw new FileNotFoundException();
        }
    }

    @Override
    public void imageProc() {
        MatConverter.scaleImage(input, paperType);

        Mat hsv = MatConverter.convertMat(input, MatType.HSV, paperType);

        MatConverter.rotate(input, hsv);

        Mat gray = MatConverter.convertMat(input, MatType.GRAY, paperType);

        Mat blurred = MatConverter.convertMat(gray, MatType.GAUSSIANBLUR, paperType);

        edged = MatConverter.convertMat(blurred, MatType.CANNY, paperType);

        Mat dilated = MatConverter.convertMat(edged, MatType.DILATE, paperType);

        thresh = MatConverter.convertMat(dilated, MatType.THRESHOLD, paperType);
    }

    @Override
    public void detectBoundingBox() {
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresh.clone(), contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        HashMap<Integer, MatOfPoint> quadrilaterals = new HashMap<>();
        MatOfPoint2f approxCurve = new MatOfPoint2f();

        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.002;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

            MatOfPoint points = new MatOfPoint(approxCurve.toArray());
            if (points.toArray().length == 4) {
                quadrilaterals.put(i, contours.get(i));
            }
        }

        List<Rect> outerQuads = new ArrayList<>();
//        Xử lí ảnh bị nghiêng
//        List<RotatedRect> rotatedRects = new ArrayList<>();
        for (Map.Entry<Integer, MatOfPoint> edgeRect : quadrilaterals.entrySet()) {
            int index = edgeRect.getKey();

            double[] boxHierarchy = hierarchy.get(0, index);
            if (boxHierarchy[3] == -1) {
                Rect roi = Imgproc.boundingRect(contours.get(index));
                outerQuads.add(roi);

//                Xử lí ảnh bị nghiêng
//                RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(edgeRect.getValue().toArray()));
//                rotatedRects.add(rotatedRect);
            }
        }

        if (outerQuads.size() == 0) {
            throw new RecognizeException();
        }

//        Xử lí ảnh bị nghiêng
//        RotatedRect rotatedRect = rotatedRects.stream().max(Comparator.comparing(rect -> rect.size.width)).get();
//        Mat m = Imgproc.getRotationMatrix2D(rotatedRect.center, rotatedRect.angle, 1.0);
//        Imgproc.warpAffine(input, input, m, input.size());
//        Imgproc.warpAffine(edged, edged, m, input.size());
//        Imgproc.warpAffine(thresh, thresh, m, input.size());

        Rect roi = outerQuads.stream().max(Comparator.comparing(rect -> rect.height)).get();

        roi.x += 2;
        roi.y += 2;
        roi.width -= 4;
        roi.height -= 4;
        boundingRect = roi;

//        Imgproc.rectangle(input, new Point(roi.x, roi.y), new Point(roi.x + roi.width, roi.y + roi.height), new Scalar(0, 255, 0), 2);
//        imageViewer.show(input);
    }

    @Override
    public List<Rect> detectRows() {
        thresh = thresh.submat(boundingRect);
        input = input.submat(boundingRect);

        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Rect> rows = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            int ratio = rect.width / rect.height;
            if (ratio > 10 && ratio < 20) {
                rows.add(rect);
//                Imgproc.rectangle(input, new Point(rect.x, rect.y),
//                        new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);
            }
        }
//        imageViewer.show(input);
        rows.sort(Comparator.comparing(rect -> rect.y));
        rows.remove(0);

//        System.out.println(rows.size());

        if (rows.size() != questionNum) {
            throw new RecognizeException();
        }
        return rows;
    }


    @Override
    public List<List<Rect>> detectBubbles(List<Rect> records) {
        edged = edged.submat(boundingRect);
        List<List<Rect>> recordsChoices = new ArrayList<>();
        for (Rect recordRect : records) {
            recordRect.x += recordRect.width * 1 / 3;
            recordRect.width = recordRect.width * 2 / 3;

            Mat mat = edged.submat(recordRect);
            ArrayList<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

            List<Rect> choices = new ArrayList<>();
            for (int i = 0; i < contours.size(); i++) {
                double[] choiceHierarchy = hierarchy.get(0, i);

                Rect choiceRect = Imgproc.boundingRect(contours.get(i));
                int w = choiceRect.width;
                int h = choiceRect.height;
                double ratio = (double) w / h;

                if (ratio >= 0.8 && ratio <= 1.2 && choiceHierarchy[3] == -1) {
                    choices.add(choiceRect);
                }
            }

            choices = choices.stream()
                    .filter(r -> r.height > 5 && r.width > 5)
                    .sorted(Comparator.comparing(r -> r.x))
                    .collect(Collectors.toList());

            if (choices.size() != questionNum) {
                throw new RecognizeException();
            }

            recordsChoices.add(choices);
        }

        return recordsChoices;
    }

    @Override
    public Object recognize() {
        log.info("Processing...");
        imageProc();
        detectBoundingBox();
        List<Rect> records = detectRows();
        List<List<Rect>> allChoices = detectBubbles(records);
        return recognizeAnswer(allChoices, records);
    }


    private List<List<Integer>> recognizeAnswer(List<List<Rect>> recordsChoices, List<Rect> records) {
        List<List<Integer>> answer = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            Mat recordMat = edged.submat(records.get(i));
            List<Rect> choiceOfRecord = recordsChoices.get(i);
            List<Integer> recordAnswers = new ArrayList<>();
            List<Integer> counters = new ArrayList<>();
            for (int j = 0; j < choiceOfRecord.size(); j++) {
                Rect r = choiceOfRecord.get(j);
                Mat choiceMat = recordMat.submat(r);
                int nonZero = Core.countNonZero(choiceMat);
                counters.add(nonZero);
            }
            int lowest = counters.stream().min(Comparator.comparing(value -> value)).get();
            for (int j = 0; j < counters.size(); j++) {
                if (counters.get(j) <= lowest + 15) {
                    recordAnswers.add(j + 1);
                }
            }
            answer.add(recordAnswers);
        }
        log.info("Done!");
        return answer;
    }
}
