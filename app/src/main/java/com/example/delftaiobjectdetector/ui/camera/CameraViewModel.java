package com.example.delftaiobjectdetector.ui.camera;

import android.net.Uri;

import androidx.camera.core.ImageProxy;
import androidx.camera.view.LifecycleCameraController;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.delftaiobjectdetector.core.camera.CameraManager;
import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.data.source.AppRepository;
import com.example.delftaiobjectdetector.core.ml.MLUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CameraViewModel extends ViewModel {

    private MutableLiveData<List<DetectionResult>> mDetectionResults = new MutableLiveData<>();
    public
    LiveData<List<DetectionResult>> detectionResults = mDetectionResults;

    private CameraManager cameraManager;

    private MLUtils mlUtils;

    private AppRepository appRepository;

    @Inject
    public CameraViewModel(CameraManager cameraManager, MLUtils mlUtils, AppRepository appRepository) {

        this.cameraManager = cameraManager;

        this.appRepository = appRepository;
        this.mlUtils = mlUtils;

    }

    public LifecycleCameraController getCameraController() {
        return cameraManager.getCameraController();
    }

    public void insertResults(DetectionResult[] detectionResults, String imagePath) {
        appRepository.insertResults(detectionResults, imagePath);
    }

    public MLUtils getMlUtils() {
        return mlUtils;
    }

    public void detectObjects(Uri imageUri, MLUtils.MLTaskListener listener) {
        mlUtils.detectObjects(imageUri, listener);
    }

    public void detectObjects( ImageProxy imageProxy, MLUtils.MLTaskListener listener) {
        mlUtils.detectObjects(imageProxy, new MLUtils.MLTaskListener() {
            @Override
            public void onMLTaskCompleted(List<DetectionResult> results) {

                mDetectionResults.postValue(results);
                listener.onMLTaskCompleted(results);
            }

            @Override
            public void onMLTaskFailed() {

            }

        });
    }




    @Override
    protected void onCleared() {
        super.onCleared();
    }
}