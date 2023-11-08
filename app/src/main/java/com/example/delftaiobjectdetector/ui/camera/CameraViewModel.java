package com.example.delftaiobjectdetector.ui.camera;

import android.media.Image;
import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.example.delftaiobjectdetector.core.camera.CameraManager;
import com.example.delftaiobjectdetector.core.ml.MLUtils;
import com.google.mlkit.vision.common.InputImage;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CameraViewModel extends ViewModel {

    private CameraManager cameraManager;

    private MLUtils mlUtils;

    @Inject
    public CameraViewModel(CameraManager cameraManager, MLUtils mlUtils) {

        this.cameraManager = cameraManager;

        this.mlUtils = mlUtils;

    }

    public MLUtils getMlUtils() {
        return mlUtils;
    }

    public void detectObjects(Uri imageUri, MLUtils.MLTaskListener listener) {
        mlUtils.detectObjects(imageUri, listener);
    }

    public void detectObjects(InputImage image, MLUtils.MLTaskListener listener) {
        mlUtils.detectObjects(image, listener);
    }



    public boolean isCameraAvailable() {
        return cameraManager.isCameraAvailable();
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}