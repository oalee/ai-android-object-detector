package com.example.delftaiobjectdetector.core.ml;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.delftaiobjectdetector.ml.EfficientdetLite2Detection;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

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

//    detect objects in image
    public void detectObjects(Uri imageUri) {



    //    read Image, downscale to 448x448, convert to RGB, convert to bytebuffer


        InputStream imageStream = null;
        try {
            imageStream = this.mContext.getContentResolver().openInputStream(imageUri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);


        // Calculate the new dimensions
        int newWidth = 448;
        int newHeight = 448;

        // Downscale the image maintaining the aspect ratio
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
        // Ensure the bitmap is in the correct format (ARGB_8888)
        Bitmap rgbBitmap = scaledBitmap.copy(Bitmap.Config.ARGB_8888, true);

        // Prepare the ByteBuffer
        int bufferSize = newWidth * newHeight * 3; // 3 bytes per pixel for RGB
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bufferSize);

        // Extract RGB values and put them into the ByteBuffer
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int pixel = rgbBitmap.getPixel(x, y);
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
                byteBuffer.put((byte) (pixel & 0xFF));         // Blue
            }
        }
        byteBuffer.rewind(); // Rewind the buffer to the beginning before loading into TensorBuffer

        TensorBuffer inputFeature = TensorBuffer.createFixedSize(new int[]{1, newWidth, newHeight, 3}, DataType.UINT8);
        inputFeature.loadBuffer(byteBuffer);

        // Runs model inference and gets result.
        EfficientdetLite2Detection.Outputs outputs = model.process(inputFeature);



        TensorBuffer outputBoxes = outputs.getOutputFeature0AsTensorBuffer();
        TensorBuffer outputClasses = outputs.getOutputFeature1AsTensorBuffer();
        TensorBuffer outputScores = outputs.getOutputFeature2AsTensorBuffer();
        TensorBuffer outputNumDetections = outputs.getOutputFeature3AsTensorBuffer();

// Typically, these buffers are in float format
        float[] boxes = outputBoxes.getFloatArray();
        float[] classes = outputClasses.getFloatArray();
        float[] scores = outputScores.getFloatArray();
        float[] numDetections = outputNumDetections.getFloatArray();

// The exact number of detections: you might need to cast from float to int
        int numberOfDetections = (int) numDetections[0];

// Iterate over the detections
        for (int i = 0; i < numberOfDetections; i++) {
            // Each box is typically represented by 4 values: [ymin, xmin, ymax, xmax]
            float ymin = boxes[4 * i];
            float xmin = boxes[4 * i + 1];
            float ymax = boxes[4 * i + 2];
            float xmax = boxes[4 * i + 3];

            // The class index for the detected object
            int classIndex = (int) classes[i];

            // The confidence score for the detection
            float score = scores[i];

            // Log or process the detection information
            Log.d("MLUtils", "Detection " + i + ": Class " + classIndex + ", Score " + score + ", Box [" + ymin + ", " + xmin + ", " + ymax + ", " + xmax + "]");
        }
//        this.model.close();
    }

    public boolean isModelLoaded() {
        return model != null;
    }


    public interface MLTaskListener {
        void onMLTaskCompleted();
        void onMLTaskFailed();
    }
}
