package edu.hcmuaf.fit.nlpige.recognize.multiplechoicesheet.core;

import org.opencv.core.Rect;

import java.util.List;

public interface SheetRecognizable extends Recognizable{
    List<Rect> detectRows();

    List<List<Rect>> detectBubbles(List<Rect> records);
}
