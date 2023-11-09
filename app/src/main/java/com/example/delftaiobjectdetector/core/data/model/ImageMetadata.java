package com.example.delftaiobjectdetector.core.data.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image_metadata")
public class ImageMetadata {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "image_name")
    String imageName;

    @ColumnInfo(name = "width")
    int width;

    @ColumnInfo(name = "height")
    int height;

    @ColumnInfo(name = "rotation")
    int rotation;

//    scaleX and y
    @ColumnInfo(name = "scale_factor")
    float scaleFactor;


    @ColumnInfo(name = "offset_x")
    int offsetX;

    @ColumnInfo(name = "offset_y")
    int offsetY;

    public ImageMetadata(String imageName, int width, int height, int rotation, float scaleFactor, int offsetX, int offsetY) {
        this.imageName = imageName;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.scaleFactor = scaleFactor;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }


    public float getScaleFactor() {
        return scaleFactor;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }
}
