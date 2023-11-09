package com.example.delftaiobjectdetector.ui.camera;

public enum CameraState {
    STREAMING,
    CAPTURING,
    CAPTURED,
    RESTARTING,
    SAVING_IMAGE_RESULT,
    ERROR_SAVE_IMAGE,
    SAVE_IMAGE_SUCCESS,
    ERROR_ML,
    ERROR_PERMISSIONS,
    ERROR_CAMERA_CAPTURE,
}
