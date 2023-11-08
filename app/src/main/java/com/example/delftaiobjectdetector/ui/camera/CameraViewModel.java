package com.example.delftaiobjectdetector.ui.camera;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

import com.example.delftaiobjectdetector.core.camera.CameraManager;
import com.example.delftaiobjectdetector.core.ml.MLUtils;

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

    public void detectObjects(Uri imageUri){
        mlUtils.detectObjects(imageUri);
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