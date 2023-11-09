package com.example.delftaiobjectdetector.ui.camera.components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GraphicOverlay extends View {
    private final Object lock = new Object();
    private final List<Graphic> graphics = new ArrayList<>();

    public float parentRotation = 90;



    // Constructors
    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // Methods to add and clear graphics
    public void add(Graphic graphic) {
        synchronized (lock) {
            graphics.add(graphic);
            graphic.overlay = this;
        }
        postInvalidate();
    }

    public void setParentRotation(float parentRotation) {
        this.parentRotation = parentRotation;
    }

    public float getParentRotation() {
        return parentRotation;
    }

    public void clear() {
        synchronized (lock) {
            graphics.clear();
        }
        postInvalidate();
    }

    // Overridden draw method to draw each graphic
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (lock) {
            for (Graphic graphic : graphics) {
                graphic.draw(canvas);
            }
        }
    }

    // Abstract class for creating graphics (bounding boxes, labels, etc.)
    public abstract static class Graphic {
        public GraphicOverlay overlay;

        public Graphic(GraphicOverlay overlay) {
            this.overlay = overlay;
        }

        public abstract GraphicOverlay getOverlay() ;

        public abstract void draw(Canvas canvas);

        public float scaleX(float horizontal) {
            return horizontal * overlay.getWidth();
        }

        public float scaleY(float vertical) {
            return vertical * overlay.getHeight();
        }

        public Context getContext() {
            return overlay.getContext();
        }
    }
}
