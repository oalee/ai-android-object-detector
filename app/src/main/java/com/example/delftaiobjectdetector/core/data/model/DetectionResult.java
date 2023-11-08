package com.example.delftaiobjectdetector.core.data.model;

import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.auto.value.AutoValue;

@Entity(tableName = "detections")
public class DetectionResult  {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "category")
    String categoryAsString;

    @ColumnInfo(name = "bounding_box")
    RectF boundingBox;

    @ColumnInfo(name = "score")
    float scoreAsFloat;

    @ColumnInfo(name = "image_path")
    String imageName;

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

    public void setBoundingBox(RectF boundingBox) {
        this.boundingBox = boundingBox;
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
}
