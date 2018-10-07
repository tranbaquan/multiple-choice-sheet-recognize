package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.utils;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.MatType;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.PaperType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MatConverter {

    public static Mat convertMat(Mat input, MatType outputType, PaperType paperType){
        Mat output = new Mat(input.size(), input.type());
        switch (outputType){
            case GRAY:
                Imgproc.cvtColor(input, output, Imgproc.COLOR_BGR2GRAY);
                break;
            case CANNY:
                Imgproc.Canny(input, output, 100, 255);
                break;
            case DILATE:
                Imgproc.dilate(input, output, Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(3, 3)));
                break;
            case THRESHOLD:
                Imgproc.threshold(input, output, 150, 255, Imgproc.THRESH_BINARY);
                break;
            case GAUSSIANBLUR:
                blur(input, output, paperType);
                break;
        }
        return output;
    }

    public static void scaleImage(Mat input, PaperType paperType){
        int width = paperType.getWidth();
        int height = width*input.rows()/input.cols();
        Imgproc.resize(input, input, new Size(width, height));
    }

    private static void blur(Mat input, Mat output, PaperType paperType) {
        switch (paperType) {
            case A4:
                Imgproc.GaussianBlur(input, output, new Size(5, 5), 0);
                break;
            case A5:
                Imgproc.GaussianBlur(input, output, new Size(3, 3), 0);
                break;
        }
    }
}