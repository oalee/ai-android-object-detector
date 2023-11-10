package com.example.delftaiobjectdetector.core.data.model;

import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "detections")
public class DetectionResult {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "category")
    String categoryAsString;

    @ColumnInfo(name = "scaled_bounding_box")
    public RectF scaledBoundingBox;

    @ColumnInfo(name = "original_box")
    public RectF originalBox;


    @ColumnInfo(name = "score")
    float scoreAsFloat;

    @ColumnInfo(name = "image_path")
    String imageName;

    public DetectionResult(@NonNull String categoryAsString, RectF scaledBoundingBox, RectF originalBox,   @NonNull float scoreAsFloat) {
        this.categoryAsString = categoryAsString;
        this.scoreAsFloat = scoreAsFloat;
        this.scaledBoundingBox = scaledBoundingBox;
        this.originalBox = originalBox;

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




    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setScoreAsFloat(float scoreAsFloat) {
        this.scoreAsFloat = scoreAsFloat;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public RectF getOriginalBox() {
        return originalBox;
    }

    public void setOriginalBox(RectF originalBox) {
        this.originalBox = originalBox;
    }

    public void setScaledBoundingBox(RectF scaledBoundingBox) {
        this.scaledBoundingBox = scaledBoundingBox;
    }

    public RectF getScaledBoundingBox() {
        return scaledBoundingBox;
    }
}
