package com.example.delftaiobjectdetector.core.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.util.LruCache;
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
    private final LruCache<String, Bitmap> bitmapCache;
    @Inject
    public ImageManager(
            SizeManager sizeManager, @ApplicationContext Context context
    ) {
        this.sizeManager = sizeManager;
        this.context = context;

        // Initialize LRU cache
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8; // Use 1/8th of the available memory for this memory cache.

        bitmapCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    // Method to add a bitmap to the cache
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            bitmapCache.put(key, bitmap);
        }
    }

    // Method to retrieve a bitmap from the cache
    public Bitmap getBitmapFromMemCache(String key) {
        return bitmapCache.get(key);
    }

    // Method to clear the cache
    public void clearCache() {
        bitmapCache.evictAll();
    }
    public Bitmap getCroppedImage(Uri source, RectF box){
        Bitmap bitmap = getBitmapFromUri(source);
        return getCroppedImage(bitmap, box);
    }

    public Bitmap getCroppedImage(Bitmap originalBitmap, RectF boundingBox) {

        // Make sure the bounding box does not go outside the image dimensions
        int x = Math.max((int) boundingBox.left, 0);
        x = Math.min(x, originalBitmap.getWidth() - 1);

        boundingBox.left = x;

        int y = Math.max((int) boundingBox.top, 0);

        y = Math.min(y, originalBitmap.getHeight() - 1);

        boundingBox.top = y;

        // Ensure width and height are within the bitmap bounds and do not extend past the edges
        int width = (int) boundingBox.width();
        int height = (int) boundingBox.height();

        if (x + width > originalBitmap.getWidth()) {
            width = originalBitmap.getWidth() - x -1; // Reduce width to fit within the bitmap
        }

        if (y + height > originalBitmap.getHeight()) {
            height = originalBitmap.getHeight() - y -1; // Reduce height to fit within the bitmap
        }

        width = Math.abs(width);
        height = Math.abs(height);


        // Crop the bitmap to the bounding box dimensions
        return Bitmap.createBitmap(originalBitmap, x, y, width, height);
    }


    private Bitmap getBitmapFromUri(Uri source) {

        String key = source.toString();

        Bitmap bitmap = getBitmapFromMemCache(key);
        if (bitmap != null) {
            return bitmap;
        }

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(source);
            Bitmap value = BitmapFactory.decodeStream(inputStream);
            addBitmapToMemoryCache(key, value);
            return value;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadImage(String imageName, DetectionResult detectionResult, ImageMetadata imageMetadata, ImageView detectedItemImageView) {

        String key = detectionResult.id + " " + imageMetadata.id;

        Bitmap cachedBitmap = getBitmapFromMemCache(key);
        if (cachedBitmap != null) {
            detectedItemImageView.setImageBitmap(cachedBitmap);
            return;
        }

        Uri imageUri = Uri.parse("file://" + context.getFilesDir() + "/" + imageName);
        Log.d("ImageManager", "loadImage: " + imageUri);
        Bitmap bitmap = getBitmapFromUri(imageUri);
        // No need to scale down or pad the bitmap, as we're cropping from the original

//        rotate bitmap by imageMetadata.rotation
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

        RectF scaledBoundingBox = detectionResult.getScaledBoundingBox();

        Bitmap croppedBitmap = getCroppedImage(bitmap, scaledBoundingBox);


        addBitmapToMemoryCache(key, croppedBitmap);

        detectedItemImageView.setImageBitmap(croppedBitmap);



    }

}
