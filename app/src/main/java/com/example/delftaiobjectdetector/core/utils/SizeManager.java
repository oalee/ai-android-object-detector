package com.example.delftaiobjectdetector.core.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class SizeManager {

    private int width;
    private int height;

    @Inject
    public SizeManager(@ApplicationContext Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    public int getHeightForWidth(int width, float aspectRatio) {
        return (int) (width * aspectRatio);
    }

    public int getCameraHeightPortraitPreview() {
        return getHeightForWidth(width, 4f / 3f);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width, View view) {
        view.getLayoutParams().width = width;
    }

    public void setHeight(int height, View view) {
        view.getLayoutParams().height = height;
    }

    public void setCameraHeightPortraitPreview(View view) {
        setHeight(getCameraHeightPortraitPreview(), view);
    }

    public void setViewWidthAndHeight(View view, int width, int height) {
        setWidth(width, view);
        setHeight(height, view);
    }

    public void setViewWidthAndHeight(View view, int size) {
        setWidth(size, view);
        setHeight(size, view);
    }

}
