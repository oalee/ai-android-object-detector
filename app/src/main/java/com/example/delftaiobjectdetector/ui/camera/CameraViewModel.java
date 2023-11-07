package com.example.delftaiobjectdetector.ui.camera;

import androidx.lifecycle.ViewModel;

import com.example.delftaiobjectdetector.core.camera.CameraManager;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CameraViewModel extends ViewModel {

    private CameraManager cameraManager;

    @Inject
    public CameraViewModel(CameraManager cameraManager) {

        this.cameraManager = cameraManager;


    }

    public boolean isCameraAvailable() {
        return cameraManager.isCameraAvailable();
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }
}