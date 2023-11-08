package com.example.delftaiobjectdetector.core.camera;

import android.content.Context;

import androidx.camera.lifecycle.ProcessCameraProvider;

import com.google.common.util.concurrent.ListenableFuture;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class CameraManager {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @Inject
    public CameraManager(@ApplicationContext Context context) {

//        log
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);

    }

    public ListenableFuture<ProcessCameraProvider> getCameraProviderFuture() {
        return cameraProviderFuture;
    }

    public boolean isCameraAvailable() {
        try {
            ProcessCameraProvider camera = cameraProviderFuture.get();
            return camera != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
