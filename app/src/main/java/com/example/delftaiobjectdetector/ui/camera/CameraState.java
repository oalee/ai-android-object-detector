package com.example.delftaiobjectdetector.ui.camera;

public enum CameraState {
    STREAMING,
    CAPTURING,
    CAPTURED,
    RESTARTING,
    ERROR_ML,
    ERROR_PERMISSIONS,
    ERROR_CAMERA_CAPTURE,
}
