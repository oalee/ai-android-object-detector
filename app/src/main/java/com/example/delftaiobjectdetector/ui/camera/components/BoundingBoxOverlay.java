package com.example.delftaiobjectdetector.ui.camera.components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.Log;

import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.data.model.ImageMetadata;

import java.util.List;


public class BoundingBoxOverlay extends GraphicOverlay.Graphic {
    private final Paint paint;
    private final Paint textPaint;
    private final List<DetectionResult> detections;
    private final ImageMetadata imageMetadata;


    public BoundingBoxOverlay(GraphicOverlay overlay, List<DetectionResult> detections, ImageMetadata imageMetadata) {
        super(overlay);


        this.detections = detections;
        this.imageMetadata = imageMetadata;

        // Paint for the boxes
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8.0f);
        PathEffect dashEffect = new DashPathEffect(new float[]{20, 20}, 0);
        paint.setPathEffect(dashEffect);

        // Paint for the text
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);
    }

    @Override
    public GraphicOverlay getOverlay() {

        return this.overlay;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        float rotation = imageMetadata.getRotation();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        int originalWidth =  imageMetadata.getWidth();
        int originalHeight = imageMetadata.getHeight();

        Log.d("Draw", "draw: " + originalWidth + " " + originalHeight + " " + canvasWidth + " " + canvasHeight);

        Log.d("Draw", "draw: " + rotation) ;

////
//        if (rotation == 0 || rotation == 180){
////            swap og width and height
//            int temp = originalWidth;
//            originalWidth = originalHeight;
//            originalHeight = temp;
//        }
//
//        if (rotation != 90) {
//            canvas.save(); // Save the canvas state before any manipulation
//
//            // Depending on the rotation, adjust the canvas.
//            // For example, if rotation is 180, rotate the canvas around its center
//            if (rotation == 180) {
//                canvas.rotate(270);
//                canvas.translate(-canvasHeight, 0);
//            } else if (rotation == 270) {
//                // for 270 degrees, rotate and translate to adjust the origin
//                canvas.rotate(180);
//                canvas.translate(-canvasWidth, -canvasHeight);
//            } else if (rotation == 0) {
//
//                canvas.rotate(90);
//                canvas.translate(0, -canvasWidth );
//            }
//
//            // Add other conditions if you expect other rotations
//        }

        // If
        for (DetectionResult detection : detections) {
            if (detection.getScoreAsFloat() >= 0.5) {
                // Use a different color for each label
                paint.setColor(getColorForLabel(detection.getCategoryAsString()));

                RectF rect = detection.getScaledBoundingBox();

//                scale rect to canvas width and height
                float scaleX = (float) canvasWidth / originalWidth;
                float scaleY = (float) canvasHeight / originalHeight;

                RectF scaledRect = new RectF(rect.left * scaleX, rect.top * scaleY, rect.right * scaleX, rect.bottom * scaleY);


                    // For 90 degrees, use the original scaling (portrait mode)
                    scaledRect = new RectF(
                            rect.left * scaleX, rect.top * scaleY,
                            rect.right * scaleX, rect.bottom * scaleY);


                // Draw the bounding box
                canvas.drawRect(scaledRect, paint);

                // Draw the label at the top
                canvas.drawText(detection.getCategoryAsString() + " " + String.format("%.2f", detection.getScoreAsFloat()),
                        scaledRect.left, scaledRect.top  + 40, textPaint);

//                Log.d("Draw", "draw: " + detection.getCategoryAsString() + " " + String.format("%.2f", detection.getScoreAsFloat()));
            }


        }

//        if (rotation != 90) {
//            canvas.restore(); // Restore the canvas to its original state if we changed it
//        }
    }

    // Helper method to assign a color based on the label
    private int getColorForLabel(String label) {
        // This is a simple way to hash a label to a color
        int hash = label.hashCode();
        // Use the hash code to generate a color
        return Color.argb(255, (hash >> 16) & 0xFF, (hash >> 8) & 0xFF, hash & 0xFF);
    }
}