package com.example.delftaiobjectdetector.core.ml;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.util.Log;

import com.example.delftaiobjectdetector.core.camera.YuvToRgbConverter;
import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.ml.EfficientdetLite2Detection;
import com.google.mlkit.vision.common.InputImage;

import org.tensorflow.lite.support.image.TensorImage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class MLUtils {
    Context mContext;

    EfficientdetLite2Detection model;

    @Inject
    public MLUtils(@ApplicationContext Context context) {
        mContext = context;

        try {
            this.model = EfficientdetLite2Detection.newInstance(context);
        } catch (IOException e) {
//            failed loading model
        }


    }

    public EfficientdetLite2Detection getModel() {
        return model;
    }

    private Bitmap padBitmapToSize(Bitmap bitmap, int width, int height) {
        Bitmap paddedBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(paddedBitmap);
        canvas.drawColor(Color.BLACK); // Use a neutral color for padding
        canvas.drawBitmap(bitmap, (width - bitmap.getWidth()) / 2f, (height - bitmap.getHeight()) / 2f, null);
        return paddedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    //    detect objects in image
    public void detectObjects(Uri imageUri, MLTaskListener listener) {


        //    read Image, downscale to 448x448, convert to RGB, convert to bytebuffer

        int orientation = -1;

        InputStream imageStream = null;
        try {
            imageStream = this.mContext.getContentResolver().openInputStream(imageUri);

            ExifInterface ei = new ExifInterface(imageUri.getPath());
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);

        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                originalBitmap = rotateImage(originalBitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                originalBitmap = rotateImage(originalBitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                originalBitmap = rotateImage(originalBitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                originalBitmap = originalBitmap;
        }


        // Store the original dimensions
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();

//        log this
        Log.d("MLUtils", "detectObjects: Original Image Size " + originalWidth + " " + originalHeight);

        // Calculate the new dimensions
        int newWidth = 448;
        int newHeight = 448;

        // Calculate the scaling factor to maintain the aspect ratio
        float scaleFactor = Math.max((float) originalWidth / newWidth, (float) originalHeight / newHeight);
        int scaledWidth = (int) (originalWidth / scaleFactor);
        int scaledHeight = (int) (originalHeight / scaleFactor);

// Downscale the image maintaining the aspect ratio
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true);

// If necessary, pad the image to the required input size (448x448)
        Bitmap paddedBitmap = padBitmapToSize(scaledBitmap, 448, 448);

// Use the padded bitmap for detection
        TensorImage image = TensorImage.fromBitmap(paddedBitmap);

        // Runs model inference and gets result.
        EfficientdetLite2Detection.Outputs outputs = model.process(
                image
        );


        List<EfficientdetLite2Detection.DetectionResult> results = outputs.getDetectionResultList();

//        for (EfficientdetLite2Detection.DetectionResult result : results) {
//            Log.d("MLUtils", "detectObjects: " + result.getCategoryAsString() + " " + result.getScoreAsFloat());
//        }

        List<DetectionResult> scaledResults = new ArrayList<>();
        float offsetX = (448 - scaledWidth) / 2f;
        float offsetY = (448 - scaledHeight) / 2f;

        Log.d(
                "MLUtils",
                String.format(
                        "Scaled Image Size: %d x %d, Offset: %f x %f",
                        scaledWidth,
                        scaledHeight,
                        offsetX,
                        offsetY
                )
        );

        for (EfficientdetLite2Detection.DetectionResult result : results) {
            RectF boundingBox = result.getLocationAsRectF();

            // Remove padding and rescale bounding box
            boundingBox.left = (boundingBox.left - offsetX) * scaleFactor;
            boundingBox.top = (boundingBox.top - offsetY) * scaleFactor;
            boundingBox.right = (boundingBox.right - offsetX) * scaleFactor;
            boundingBox.bottom = (boundingBox.bottom - offsetY) * scaleFactor;

            // Ensure the bounding box does not go beyond the image boundaries
            boundingBox.left = Math.max(boundingBox.left, 0);
            boundingBox.top = Math.max(boundingBox.top, 0);
            boundingBox.right = Math.min(boundingBox.right, originalWidth);
            boundingBox.bottom = Math.min(boundingBox.bottom, originalHeight);

            // Log the category, score, and bounding box
            Log.d("MLUtils", String.format("Category: %s, Score: %.2f, Box: [%f, %f, %f, %f]",
                    result.getCategoryAsString(),
                    result.getScoreAsFloat(),
                    boundingBox.left,
                    boundingBox.top,
                    boundingBox.right,
                    boundingBox.bottom));

            // You might need to create a new result object if the DetectionResult class is immutable
            // or otherwise update the bounding box in the existing result object.
            scaledResults.add(new DetectionResult(
                    result.getCategoryAsString(),
                    boundingBox,
                    result.getScoreAsFloat()
            ));

        }
        if (listener != null) {
            listener.onMLTaskCompleted(scaledResults);
        }

//        this.model.close();
    }

    public Bitmap ToBitmap(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
    }

    public void detectObjects(InputImage srcImage, MLTaskListener listener) {


        YuvToRgbConverter converter = new YuvToRgbConverter(mContext);
        Bitmap originalBitmap = Bitmap.createBitmap(
                srcImage.getWidth(),
                srcImage.getHeight(),
                Bitmap.Config.ARGB_8888
        );

        converter.yuvToRgb(srcImage.getMediaImage(), originalBitmap);

//           rotate image
        int rotation = srcImage.getRotationDegrees();

        originalBitmap = rotateImage(originalBitmap, rotation);


        // Store the original dimensions
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();

//        log this
        Log.d("MLUtils", "detectObjects: Original Image Size " + originalWidth + " " + originalHeight);

        // Calculate the new dimensions
        int newWidth = 448;
        int newHeight = 448;

        // Calculate the scaling factor to maintain the aspect ratio
        float scaleFactor = Math.max((float) originalWidth / newWidth, (float) originalHeight / newHeight);
        int scaledWidth = (int) (originalWidth / scaleFactor);
        int scaledHeight = (int) (originalHeight / scaleFactor);

// Downscale the image maintaining the aspect ratio
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true);

// If necessary, pad the image to the required input size (448x448)
        Bitmap paddedBitmap = padBitmapToSize(scaledBitmap, 448, 448);

// Use the padded bitmap for detection
        TensorImage image = TensorImage.fromBitmap(paddedBitmap);

        // Runs model inference and gets result.
        EfficientdetLite2Detection.Outputs outputs = model.process(
                image
        );


        List<EfficientdetLite2Detection.DetectionResult> results = outputs.getDetectionResultList();

//        for (EfficientdetLite2Detection.DetectionResult result : results) {
//            Log.d("MLUtils", "detectObjects: " + result.getCategoryAsString() + " " + result.getScoreAsFloat());
//        }

        List<DetectionResult> scaledResults = new ArrayList<>();
        float offsetX = (448 - scaledWidth) / 2f;
        float offsetY = (448 - scaledHeight) / 2f;

        Log.d(
                "MLUtils",
                String.format(
                        "Scaled Image Size: %d x %d, Offset: %f x %f",
                        scaledWidth,
                        scaledHeight,
                        offsetX,
                        offsetY
                )
        );

        for (EfficientdetLite2Detection.DetectionResult result : results) {
            RectF boundingBox = result.getLocationAsRectF();

            // Remove padding and rescale bounding box
            boundingBox.left = (boundingBox.left - offsetX) * scaleFactor;
            boundingBox.top = (boundingBox.top - offsetY) * scaleFactor;
            boundingBox.right = (boundingBox.right - offsetX) * scaleFactor;
            boundingBox.bottom = (boundingBox.bottom - offsetY) * scaleFactor;

            // Ensure the bounding box does not go beyond the image boundaries
            boundingBox.left = Math.max(boundingBox.left, 0);
            boundingBox.top = Math.max(boundingBox.top, 0);
            boundingBox.right = Math.min(boundingBox.right, originalWidth);
            boundingBox.bottom = Math.min(boundingBox.bottom, originalHeight);

            // Log the category, score, and bounding box
            Log.d("MLUtils", String.format("Category: %s, Score: %.2f, Box: [%f, %f, %f, %f]",
                    result.getCategoryAsString(),
                    result.getScoreAsFloat(),
                    boundingBox.left,
                    boundingBox.top,
                    boundingBox.right,
                    boundingBox.bottom));

            // You might need to create a new result object if the DetectionResult class is immutable
            // or otherwise update the bounding box in the existing result object.
            scaledResults.add(new DetectionResult(
                    result.getCategoryAsString(),
                    boundingBox,
                    result.getScoreAsFloat()
            ));

        }
        if (listener != null) {
            listener.onMLTaskCompleted(scaledResults);
        }

//        this.model.close();
    }

    public boolean isModelLoaded() {
        return model != null;
    }


    public interface MLTaskListener {
        void onMLTaskCompleted(List<DetectionResult> results);

        void onMLTaskFailed();
    }
}
