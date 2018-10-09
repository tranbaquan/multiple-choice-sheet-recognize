package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.FileNotFoundException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.RecognizeException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.logging.Logger;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.MatType;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.PaperType;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.RationConst;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.utils.MatConverter;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.imageviewer.ImageViewer;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;
import java.util.stream.Collectors;

public class SheetRecognize extends Logger implements SheetRecognizable {

    private final ImageViewer imageViewer = new ImageViewer();
    private Mat input, edged, thresh;
    private Rect boundingRect;
    private int questionNum;
    private PaperType paperType;
    private double avgRatio;

    public SheetRecognize() {
        this.paperType = PaperType.A4;
        initRatio();
    }

    public SheetRecognize(int questionNum, PaperType paperType) {
        this.questionNum = questionNum;
        this.paperType = paperType;
        initRatio();
    }

    private void initRatio() {
        switch (paperType){
            case A4:
                avgRatio = RationConst.A4_RATION_DEFAULT;
                break;
            case A5:
                avgRatio = RationConst.A5_RATION_DEFAULT;
                break;
        }
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

        Mat gray = MatConverter.convertMat(input, MatType.GRAY, paperType);

        Mat blurred = MatConverter.convertMat(gray, MatType.GAUSSIANBLUR, paperType);

        edged = MatConverter.convertMat(blurred, MatType.CANNY, paperType);

        Mat dilated = MatConverter.convertMat(edged, MatType.DILATE, paperType);

        thresh = MatConverter.convertMat(dilated, MatType.THRESHOLD, paperType);

        imageViewer.show(thresh);
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
        for (Map.Entry<Integer, MatOfPoint> edgeRect : quadrilaterals.entrySet()) {
            int index = edgeRect.getKey();

            double[] boxHierarchy = hierarchy.get(0, index);
            if (boxHierarchy[3] == -1) {
                Rect roi = Imgproc.boundingRect(contours.get(index));
                outerQuads.add(roi);
            }
        }

        if (outerQuads.size() == 0) {
            throw new RecognizeException();
        }

        Rect roi = outerQuads.stream().max(Comparator.comparing(rect -> rect.height)).get();

        roi.x += 2;
        roi.y += 2;
        roi.width -= 4;
        roi.height -= 4;

        Imgproc.rectangle(input, new Point(roi.x, roi.y),
                new Point(roi.x + roi.width, roi.y + roi.height), new Scalar(255, 0, 0), 3);

//        imageViewer.show(input);
        boundingRect = roi;
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
            if (ratio > 10 && ratio < 15) {
                rows.add(rect);
                Imgproc.rectangle(input, new Point(rect.x, rect.y),
                        new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)), 2);
            }
        }

        rows.sort(Comparator.comparing(rect -> rect.y));
        rows.remove(0);
//        imageViewer.show(input);

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
            recordRect.x += recordRect.width / 2;
            recordRect.width /= 2;

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
            for (int j = 0; j < choiceOfRecord.size(); j++) {
                Rect r = choiceOfRecord.get(j);
                Mat choiceMat = recordMat.submat(r);
                int nonZero = Core.countNonZero(choiceMat);
                double ratio = (double) nonZero / (Math.pow(r.width / 2, 2) * 3.14);
                if (ratio < avgRatio) {
                    recordAnswers.add(j + 1);
                }
            }
            answer.add(recordAnswers);
        }
        log.info("Done!");
        return answer;
    }

}
