package com.example.delftaiobjectdetector.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class SizeManager {

    private final Context context;
    private int width;
    private int height;

    @Inject
    public SizeManager(@ApplicationContext Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        this.context = context;
    }

    public int getHeightForWidth(int width, float aspectRatio) {
        return (int) (width * aspectRatio);
    }

    public int getCameraHeightPortraitPreview() {
        return getHeightForWidth(width, 4f / 3f);
    }

    public int getCameraHeightLandscapePreview() {
        return getHeightForWidth(width, 3f / 4f);
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

    public Size getImageSize(Uri imageUri) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

// Use the content resolver to open a stream to the image URI
        try (InputStream inputStream = context.getContentResolver().openInputStream(imageUri)) {
            BitmapFactory.decodeStream(inputStream, null, options);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
        }

        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        return new Size(imageWidth, imageHeight);
    }
    public void setCameraHeightBasedOnImageSize(View view, Uri imageUri) {
        Size imageSize = getImageSize(imageUri);

        Log.d("SizeManager", "setCameraHeightBasedOnImageSize: " + imageSize.toString());

        float aspectRatio = (float) imageSize.getWidth() / (float) imageSize.getHeight();
        int height = getHeightForWidth(width, aspectRatio);
        setHeight(height, view);
        view.postInvalidate();
    }

}
