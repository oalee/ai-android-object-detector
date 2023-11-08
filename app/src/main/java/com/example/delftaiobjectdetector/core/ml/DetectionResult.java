package com.example.delftaiobjectdetector.core.ml;

import android.graphics.RectF;

import androidx.annotation.NonNull;

public class DetectionResult {


    String categoryAsString;

    RectF boundingBox;

    float scoreAsFloat;



    public DetectionResult(@NonNull String categoryAsString, RectF boundingBox, @NonNull float scoreAsFloat) {
        this.categoryAsString = categoryAsString;
        this.boundingBox = boundingBox;
        this.scoreAsFloat = scoreAsFloat;
    }

    public String getCategoryAsString() {
        return categoryAsString;
    }

    public void setCategoryAsString(String categoryAsString) {
        this.categoryAsString = categoryAsString;
    }

    public float getScoreAsFloat() {
        return scoreAsFloat;
    }

    public RectF getBoundingBox() {
        return boundingBox;
    }
}
