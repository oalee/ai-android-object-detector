package com.example.delftaiobjectdetector.core.ml;

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

import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.data.model.ImageMetadata;
import com.example.delftaiobjectdetector.core.utils.BitmapUtils;
import com.example.delftaiobjectdetector.ml.EfficientdetLite2Detection;

import org.tensorflow.lite.support.image.TensorImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import timber.log.Timber;

@Singleton
public class MLUtils implements DefaultLifecycleObserver {
    Context mContext;

    EfficientdetLite2Detection model;

    @Inject
    public MLUtils(@ApplicationContext Context context) {
        mContext = context;

        initModel();


    }

    public boolean ioError() {
        return model == null;
    }

    private void initModel() {
        try {
            this.model = EfficientdetLite2Detection.newInstance(mContext);
        } catch (IOException e) {
            this.model = null; // set model to null to indicate error
        }
    }

    public EfficientdetLite2Detection getModel() {

        if (model == null) {
            initModel();
        }

        return model;
    }

    // Implement the DefaultLifecycleObserver interface methods
    @Override
    public void onDestroy(LifecycleOwner owner) {
        // This will be called when the LifecycleOwner (Activity or Fragment) is destroyed
        if (model != null) {
            // Release resources or cleanup here
            model.close(); // assuming there is a close method to release resources
            model = null;
        }
    }

    // Make sure to initialize and release MLUtils with the lifecycle of the Activity or Fragment
    public void bindToLifecycle(LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(this);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);

        int rotation = 0;

        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                originalBitmap = rotateImage(originalBitmap, 90);
                rotation = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                originalBitmap = rotateImage(originalBitmap, 180);
                rotation = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                originalBitmap = rotateImage(originalBitmap, 270);
                rotation = 270;
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                originalBitmap = originalBitmap;
        }


        // Store the original dimensions
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();

//        log this
        Timber.d("detectObjects: Original Image Size " + originalWidth + " " + originalHeight);

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


        List<DetectionResult> scaledResults = new ArrayList<>();
        float offsetX = (448 - scaledWidth) / 2f;
        float offsetY = (448 - scaledHeight) / 2f;


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


            // You might need to create a new result object if the DetectionResult class is immutable
            // or otherwise update the bounding box in the existing result object.
            scaledResults.add(new DetectionResult(
                    result.getCategoryAsString(),
                    boundingBox,
                    result.getLocationAsRectF(),
                    result.getScoreAsFloat()
            ));

        }

        ImageMetadata imageMetadata = new ImageMetadata(imageUri.getLastPathSegment(), originalWidth, originalHeight, rotation, scaleFactor, (int) offsetX, (int) offsetY);

        if (listener != null) {
            listener.onMLTaskCompleted(scaledResults, imageMetadata);
        }

    }


    @OptIn(markerClass = ExperimentalGetImage.class)
    public void detectObjects(ImageProxy srcImage, MLTaskListener listener) {

        Bitmap originalBitmap = BitmapUtils.getBitmap(srcImage);

//           rotate image
        int rotation = srcImage.getImageInfo().getRotationDegrees();

        // Store the original dimensions
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();

//        dependeing on orientation, rotate image, that is, if lanscape, swap width and height

        if (rotation == 0 || rotation == 180) {
            originalWidth = originalBitmap.getHeight();
            originalHeight = originalBitmap.getWidth();
        }

        // Model input size
        int modelInputImageWidth = 448;
        int modelInputImageHeight = 448;

        // Calculate the scaling factor to maintain the aspect ratio
        float scaleFactor = Math.max((float) originalWidth / modelInputImageWidth, (float) originalHeight / modelInputImageHeight);
        int scaledWidth = (int) (originalWidth / scaleFactor);
        int scaledHeight = (int) (originalHeight / scaleFactor);

// Downscale the image maintaining the aspect ratio
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true);

// If necessary, pad the image to the required input size (448x448)
        Bitmap paddedBitmap = padBitmapToSize(scaledBitmap, 448, 448);

// Use the padded bitmap for detection
        TensorImage image = TensorImage.fromBitmap(paddedBitmap);

        // Runs model inference and gets result.
        EfficientdetLite2Detection.Outputs outputs = getModel().process(
                image
        );


        List<EfficientdetLite2Detection.DetectionResult> results = outputs.getDetectionResultList();


        List<DetectionResult> scaledResults = new ArrayList<>();
        float offsetX = (448 - scaledWidth) / 2f;
        float offsetY = (448 - scaledHeight) / 2f;


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


            // You might need to create a new result object if the DetectionResult class is immutable
            // or otherwise update the bounding box in the existing result object.
            scaledResults.add(new DetectionResult(
                    result.getCategoryAsString(),
                    boundingBox,
                    result.getLocationAsRectF(),
                    result.getScoreAsFloat()
            ));

        }

        ImageMetadata imageMetadata = new ImageMetadata("image_name", originalWidth, originalHeight, rotation, scaleFactor, (int) offsetX, (int) offsetY);

        if (listener != null) {
            listener.onMLTaskCompleted(scaledResults, imageMetadata);
        }

    }


    public interface MLTaskListener {
        void onMLTaskCompleted(List<DetectionResult> results, ImageMetadata imageMetadata);

        void onMLTaskFailed();
    }
}
