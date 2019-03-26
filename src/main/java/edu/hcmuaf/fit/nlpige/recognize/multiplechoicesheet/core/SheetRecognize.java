package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.FileNotFoundException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.exception.RecognizeException;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.MatType;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.PaperType;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.utils.MatConverter;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.viewer.image.ImageViewer;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static nu.pattern.OpenCV.loadShared;

public class SheetRecognize implements SheetRecognizable {

    static {
        String osName = System.getProperty("os.name");
        String userDir = System.getProperty("user.dir");
        if (osName.contains("Windows 7") && System.getenv("ProgramFiles(x86)") != null) {
            System.load(userDir + "/src/main/resources/opencv_java341.dll");
        } else {
            loadShared();
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        }
    }

    private final ImageViewer imageViewer = new ImageViewer();
    private Mat input, edged, thresh;
    private Rect boundingRect;
    private int questionNum;
    private PaperType paperType;
    private ArrayList<MatOfPoint>  contours;
    private Mat hierarchy;

    public SheetRecognize() {
        this.paperType = PaperType.A4;
    }

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
        contours = new ArrayList<>();
        hierarchy = new Mat();
        Imgproc.findContours(thresh.clone(), this.contours, this.hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        HashMap<Integer, MatOfPoint> quadrilaterals = new HashMap<>();
        MatOfPoint2f approxCurve = new MatOfPoint2f();

        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.005;
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

//            Rect r = Imgproc.boundingRect(contours.get(index));
//            Imgproc.rectangle(input, new Point(r.x, r.y), new Point(r.x + r.width, r.y + r.height), new Scalar(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)), 2);
            if (boxHierarchy[3] == -1) {
                Rect roi = Imgproc.boundingRect(contours.get(index));
                outerQuads.add(roi);

//                Xử lí ảnh bị nghiêng
//                RotatedRect rotatedRect = Imgproc.minAreaRect(new MatOfPoint2f(edgeRect.getValue().toArray()));
//                rotatedRects.add(rotatedRect);
            }
        }

//        imageViewer.show(input);
//        imageViewer.show(edged);
//        imageViewer.show(thresh);


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

        contours = new ArrayList<>();
        hierarchy = new Mat();
        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        List<Rect> rows = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            int ratio = rect.width / rect.height;
            if (ratio > 10 && ratio < 20 && rect.width > boundingRect.width - 50) {
                rows.add(rect);
//                Imgproc.rectangle(input, new Point(rect.x, rect.y),
//                        new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);
            }
        }

//        imageViewer.show(input);
        rows.sort(Comparator.comparing(rect -> rect.y));
        rows.remove(0);

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
            recordRect.x += recordRect.width / 3;
            recordRect.width = recordRect.width * 2 / 3;

            Mat mat = edged.submat(recordRect);

            hierarchy = new Mat();
            contours = new ArrayList<>();
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
    public int[][] recognize() {
        detectBoundingBox();
        List<Rect> records = detectRows();
        List<List<Rect>> allChoices = detectBubbles(records);
        List<List<Integer>> recognized = recognizeAnswer(allChoices, records);
        int[][] res = new int[recognized.size()][];
        for (int i = 0; i < recognized.size(); i++) {
            res[i] = new int[recognized.get(i).size()];
            for (int j = 0; j < res[i].length; j++) {
                res[i][j] = recognized.get(i).get(j);
            }
        }
        return res;
    }

    public String getQrCode() {
        try {
            Rect rect = new Rect();
            rect.x = 400;
            rect.y = 0;
            rect.width = 196;
            rect.height = 200;
            Mat m = input.submat(rect);
            Mat m1 = MatConverter.convertMat(m, MatType.GRAY, paperType);
            m1 = MatConverter.convertMat(m1, MatType.GAUSSIANBLUR, paperType);
            m1 = MatConverter.convertMat(m1, MatType.CANNY, paperType);
            Imgproc.dilate(m1, m1, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(9, 9)));
            ArrayList<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(m1, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            contours.sort(Comparator.comparing(matOfPoint -> matOfPoint.toArray().length));
            Rect r = Imgproc.boundingRect(contours.get(contours.size() - 1));

            Mat m2 = m.submat(r);
            int bufferSize = m2.channels() * m2.cols() * m2.rows();
            byte[] buffer = new byte[bufferSize];
            m2.get(0, 0, buffer);

            BufferedImage image = new BufferedImage(m2.cols(), m2.rows(), BufferedImage.TYPE_3BYTE_BGR);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "bmp", baos);
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return Base64.getEncoder().encodeToString(imageInByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<List<Integer>> recognizeAnswer(List<List<Rect>> recordsChoices, List<Rect> records) {
        List<List<Integer>> answer = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            Mat recordMat = edged.submat(records.get(i));
            List<Rect> choiceOfRecord = recordsChoices.get(i);
            List<Integer> recordAnswers = new ArrayList<>();
            List<Integer> counters = new ArrayList<>();
            for (Rect r : choiceOfRecord) {
                Mat choiceMat = recordMat.submat(r);
                int nonZero = Core.countNonZero(choiceMat);
                counters.add(nonZero);
            }
            int lowest = counters.stream().min(Comparator.comparing(value -> value)).get();
            for (int j = 0; j < counters.size(); j++) {
                if (counters.get(j) <= lowest + 19) {
                    recordAnswers.add(j + 1);
                }
            }
            answer.add(recordAnswers);
        }
        return answer;
    }
}
