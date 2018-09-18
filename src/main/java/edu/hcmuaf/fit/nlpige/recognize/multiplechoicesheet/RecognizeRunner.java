package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet;

import edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.tool.imageviewer.ImageViewer;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;

public class RecognizeRunner {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String file = "src/main/resources/sheet-1.jpg";
        ImageViewer imageViewer = new ImageViewer();

        Mat img = Imgcodecs.imread(file);
        imageViewer.show(img);

        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        imageViewer.show(gray, "Gray Image");

        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);
        imageViewer.show(blurred, "Blurred");

        Mat edged = new Mat();
        Imgproc.Canny(blurred, edged, 75, 200);
        imageViewer.show(edged, "Edged");

        Mat adaptiveThresh = new Mat();
        Imgproc.adaptiveThreshold(edged, adaptiveThresh, 255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
        imageViewer.show(adaptiveThresh, "adaptiveThresh ");

        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(adaptiveThresh.clone(), contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        HashMap<Double, MatOfPoint> rectangles = new HashMap<Double, MatOfPoint>();
        for(int i = 0; i < contours.size(); i++){
            MatOfPoint2f approxCurve = new MatOfPoint2f( contours.get(i).toArray() );

            if(approxCurve.toArray().length == 4){
                rectangles.put((double) i, contours.get(i));
            }
        }

    }
}
