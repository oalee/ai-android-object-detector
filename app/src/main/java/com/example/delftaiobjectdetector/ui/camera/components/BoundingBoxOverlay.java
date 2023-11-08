package com.example.delftaiobjectdetector.ui.camera.components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.Log;

import com.example.delftaiobjectdetector.core.ml.DetectionResult;

import java.util.List;


public class BoundingBoxOverlay extends GraphicOverlay.Graphic {
    private final Paint paint;
    private final Paint textPaint;
    private final List<DetectionResult> detections;



    public BoundingBoxOverlay(GraphicOverlay overlay, List<DetectionResult> detections) {
        super(overlay);

        this.detections = detections;

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
    public void draw(Canvas canvas) {

        for (DetectionResult detection : detections) {
            if (detection.getScoreAsFloat() >= 0.5) {
                // Use a different color for each label
                paint.setColor(getColorForLabel(detection.getCategoryAsString()));

                RectF rect = detection.getBoundingBox();

                int originalWidth = 480 ;
                int originalHeight = 640;



                int canvasWidth = canvas.getWidth();
                int canvasHeight = canvas.getHeight();

//                log
                Log.d("Draw", "canvas: " + canvasWidth + " " + canvasHeight);

//                scale rect to canvas width and height
                float scaleX = (float) canvasWidth / originalWidth;
                float scaleY = (float) canvasHeight / originalHeight;

                RectF scaledRect = new RectF(rect.left * scaleX, rect.top * scaleY, rect.right * scaleX, rect.bottom * scaleY);

//                rotate 90 degrees rect
//                RectF scaledRect = new RectF(
//                        detection.getBoundingBox().top * scaleX,
//                        detection.getBoundingBox().left * scaleY,
//                        detection.getBoundingBox().bottom * scaleX,
//                        detection.getBoundingBox().right * scaleY);

                // Draw the bounding box
                canvas.drawRect(scaledRect, paint);

                // Draw the label at the top
                canvas.drawText(detection.getCategoryAsString() + " " + String.format("%.2f", detection.getScoreAsFloat()),
                        scaledRect.left, scaledRect.top  + 40, textPaint);

                Log.d("Draw", "draw: " + detection.getCategoryAsString() + " " + String.format("%.2f", detection.getScoreAsFloat()));
            }
        }
    }

    // Helper method to assign a color based on the label
    private int getColorForLabel(String label) {
        // This is a simple way to hash a label to a color
        int hash = label.hashCode();
        // Use the hash code to generate a color
        return Color.argb(255, (hash >> 16) & 0xFF, (hash >> 8) & 0xFF, hash & 0xFF);
    }
}