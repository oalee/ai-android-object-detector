package com.example.delftaiobjectdetector.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.data.model.ImageMetadata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class ImageManager {


    private final SizeManager sizeManager;
    private final Context context;

    @Inject
    public ImageManager(
            SizeManager sizeManager, @ApplicationContext Context context
    ) {
        this.sizeManager = sizeManager;
        this.context = context;
    }

    public Bitmap getCroppedImage(Uri source, RectF box){
        Bitmap bitmap = getBitmapFromUri(source);
        return getCroppedImage(bitmap, box);
    }

    private Bitmap getCroppedImage(Bitmap bitmap, RectF cropRect) {

        Log.d("ImageManager", "getCroppedImage: " + cropRect);

        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap,
                (int) ( cropRect.left * 1080.f/480),
                (int) (cropRect.top * 1440.f/640),
                (int) Math.abs  ((int) cropRect.width()  * 1080.f/480 ),
                (int) (Math.abs  ( (int) cropRect.height())  * 1440.f/640)) ;
        return croppedBitmap;

    }

    private Bitmap getBitmapFromUri(Uri source) {

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(source);
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadImage(String imageName, RectF boundingBox, ImageView detectedItemImageView) {

        Uri imageUri = Uri.parse("file://" + context.getFilesDir() + "/" + imageName);
        Log.d("ImageManager", "loadImage: " + imageUri);
        Bitmap bitmap = getBitmapFromUri(imageUri);

//        rotate bitmap
        try {
            ExifInterface exif = new ExifInterface(imageUri.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("ImageManager", "loadImage: orientation " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap == null) {

            Log.d("ImageManager", "loadImage: bitmap is null");
            return;
        }

        Bitmap croppedBitmap = getCroppedImage(bitmap, boundingBox);
        detectedItemImageView.setImageBitmap(croppedBitmap);
    }

    public void loadImage(String imageName, DetectionResult detectionResult, ImageMetadata imageMetadata, ImageView detectedItemImageView) {


    }
}
