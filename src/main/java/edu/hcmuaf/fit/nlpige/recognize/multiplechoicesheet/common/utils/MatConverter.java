package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.utils;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.MatType;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.common.types.PaperType;
import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.viewer.image.ImageViewer;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

public class MatConverter {

    public static Mat convertMat(Mat input, MatType outputType, PaperType paperType){
        if(outputType == null) {
            throw new NullPointerException("Output type is null");
        }

        if (paperType == null) {
            throw new NullPointerException("Paper type is null");
        }

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
            case HSV:
                Imgproc.cvtColor(input, output, Imgproc.COLOR_BGR2HSV);
        }
        return output;
    }

    public static void rotate(Mat input, Mat hsv) {
        boolean isLogo = false;
//        Imgproc.rectangle(hsv, new Point(0,0), new Point(150, 80), new Scalar(0,0,0));
//        new ImageViewer().show(hsv);

        label: for (int i = 0; i < 80; i++){
            for (int j = 0; j < 150; j++) {
                if(hsv.get(i, j)[2] < 90) {
                    isLogo = true;
                    break label;
                }
            }
        }
        if(!isLogo) {
            Core.rotate(input, input, Core.ROTATE_180);
        }
    }

    public static void scaleImage(Mat input, PaperType paperType){
        if (paperType == null) {
            throw new NullPointerException("Paper type is null");
        }

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
