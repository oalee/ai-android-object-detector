package com.example.delftaiobjectdetector.core.data.model;

import android.graphics.RectF;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.auto.value.AutoValue;

@AutoValue
//@Entity(tableName = "detections")
public abstract class AbsDetectionResult {

    @AutoValue.CopyAnnotations
    @PrimaryKey(autoGenerate = true)
    public abstract long getId();

    public abstract String getCategoryAsString();

    public abstract float getScoreAsFloat();

    public abstract RectF getBoundingBox();

//    public static AbsDetectionResult create(long id, String categoryAsString, float scoreAsFloat, float boundingBoxLeft) {
//        return new AutoValue_AbsDetectionResult(id, categoryAsString, scoreAsFloat, boundingBoxLeft);
//    }
}
