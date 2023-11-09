package com.example.delftaiobjectdetector.core.camera;

import android.content.Context;
import android.util.Size;

import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.CameraController;
import androidx.camera.view.LifecycleCameraController;

import com.google.common.util.concurrent.ListenableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class CameraManager {

    private LifecycleCameraController cameraController;

    @Inject
    public CameraManager(@ApplicationContext Context context) {

        initCameraController(context);
    }

    public LifecycleCameraController getCameraController() {
        return cameraController;
    }

    private void initCameraController(Context context){
        cameraController = new LifecycleCameraController(context);

        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);

        cameraController.setImageAnalysisTargetSize(
                new CameraController.OutputSize(
                        new Size(
                                480,
                                640
                        )
                )
        );

        cameraController.setImageCaptureTargetSize(
                new CameraController.OutputSize(
                        new Size(
                                480,
                                640
                        )
                )
        );

    }


}
