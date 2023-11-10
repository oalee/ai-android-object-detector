package com.example.delftaiobjectdetector.ui.camera;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.CameraController;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.delftaiobjectdetector.core.camera.CameraManager;
import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.data.model.ImageMetadata;
import com.example.delftaiobjectdetector.core.data.source.AppRepository;
import com.example.delftaiobjectdetector.core.ml.MLUtils;
import com.example.delftaiobjectdetector.core.utils.SizeManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import timber.log.Timber;

@HiltViewModel
public class CameraViewModel extends ViewModel implements MLUtils.MLTaskListener {

    private MutableLiveData<List<DetectionResult>> mDetectionResults = new MutableLiveData<>();
    public LiveData<List<DetectionResult>> detectionResults = mDetectionResults;

    private MutableLiveData<CameraState> mCameraState = new MutableLiveData<>(CameraState.STREAMING);

    public LiveData<CameraState> cameraState = mCameraState;

    private MutableLiveData<String> mSavedImageName = new MutableLiveData<>();

    public LiveData<String> savedImageName = mSavedImageName;

    private MutableLiveData<Integer> mRotationDegrees = new MutableLiveData<>();

    public LiveData<Integer> rotationDegrees = mRotationDegrees;

    private MutableLiveData<ImageMetadata> mImageMetadata = new MutableLiveData<>();

    public LiveData<ImageMetadata> imageMetadata = mImageMetadata;

    private MutableLiveData<Boolean> mShouldGalleryBeEnabled = new MutableLiveData<>();
    public LiveData<Boolean> shouldGalleryBeEnabled = mShouldGalleryBeEnabled;


    private CameraManager cameraManager;

    private MLUtils mlUtils;

    private AppRepository appRepository;

    private SizeManager sizeManager;

    private Context context;

    public SizeManager getSizeManager() {
        return sizeManager;
    }


    @Inject
    public CameraViewModel(CameraManager cameraManager, MLUtils mlUtils, AppRepository appRepository, SizeManager sizeManager, @ApplicationContext Context context) {

        this.cameraManager = cameraManager;
        this.sizeManager = sizeManager;
        this.appRepository = appRepository;
        this.mlUtils = mlUtils;
        this.context = context;

//        run on background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            mShouldGalleryBeEnabled.postValue(
                    appRepository.getImages().size() > 0
            );

        });

    }

    public void bindCamera(LifecycleOwner lifecycleOwner, PreviewView previewView) {

        bindCamera(lifecycleOwner);
        LifecycleCameraController cameraController = cameraManager.getCameraController();
        previewView.setController(cameraController);


    }

    public void bindCamera(LifecycleOwner lifecycleOwner) {

        LifecycleCameraController cameraController = cameraManager.getCameraController();
        cameraController.bindToLifecycle(lifecycleOwner);
        cameraController.setImageAnalysisAnalyzer(
                Executors.newSingleThreadExecutor(),
                image -> {
                    detectObjects(image, CameraViewModel.this);
                    Integer rotationDegrees = image.getImageInfo().getRotationDegrees();
                    mRotationDegrees.postValue(rotationDegrees);
                    image.close();
                }
        );
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

    public void restartCapture(LifecycleOwner owner) {
        if (cameraState.getValue() != CameraState.CAPTURED) {
            return;
        }
        mCameraState.postValue(CameraState.RESTARTING);
        bindCamera(owner);


    }

    public void captureImage() {

        if (cameraState.getValue() != CameraState.STREAMING) {
            return;
        }
        Timber.d("captureImage: ");

        unbindML();

//        post value to change camera state
        mCameraState.postValue(CameraState.CAPTURING);

        LifecycleCameraController cameraController = cameraManager.getCameraController();

        String fileName = "temp.jpg";
        File file = new File(this.context.getFilesDir(), fileName);


        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).
                        build();

        cameraController.takePicture(
                outputFileOptions,
                Executors.newSingleThreadExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Timber.d("onImageSaved: " + file.getAbsolutePath());

//                        Uri savedUri = outputFileResults.getSavedUri(); // Get the Uri of the saved file
//                        String filePath = savedUri.getPath(); // Convert Uri to file path if needed
//
//
//
//                        try {
//                            ExifInterface exif = new ExifInterface(filePath);
//
////                            for orientation 0 save 90
//
//                            int degrees = rotationDegrees.getValue();
//                            Timber.d("onImageSaved: " + degrees);
//                            if (degrees == 0) {
//                                exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
//                                exif.saveAttributes();
//
//                            }
//                            if (degrees == 180) {
//                                exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_270));
//                                exif.saveAttributes();
//
//                            }
//                            // Set the orientation tag to rotate 90 degrees
//
//                            Timber.d("EXIF metadata updated with portrait orientation.");
//                        } catch (IOException e) {
//                            Log.e("CameraFragment", "Failed to update EXIF metadata", e);
//                        }
                        mCameraState.postValue(CameraState.CAPTURED);
//                        release camera for now
//                        Integer rotationDegrees =  outputFileResults.getSavedImage().getImageInfo().getRotationDegrees();
//                        mRotationDegrees.postValue(rotationDegrees);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {

                        Timber.d("onError: " + exception.getMessage());

                        mCameraState.postValue(CameraState.ERROR_CAMERA_CAPTURE);
                    }
                }
        );
    }

    public LifecycleCameraController getCameraController() {
        return cameraManager.getCameraController();
    }


    public MLUtils getMlUtils() {
        return mlUtils;
    }

    public void detectObjects(Uri imageUri, MLUtils.MLTaskListener listener) {
        mlUtils.detectObjects(imageUri, listener);
    }

    public void detectObjects(ImageProxy imageProxy, MLUtils.MLTaskListener listener) {
        mlUtils.detectObjects(imageProxy, new MLUtils.MLTaskListener() {
            @Override
            public void onMLTaskCompleted(List<DetectionResult> results, ImageMetadata imageMetadata) {
                mImageMetadata.postValue(imageMetadata);

                mDetectionResults.postValue(results);
                listener.onMLTaskCompleted(results, imageMetadata);
            }

            @Override
            public void onMLTaskFailed() {

            }

        });
    }

    @Override
    public void onMLTaskCompleted(List<DetectionResult> results, ImageMetadata imageMetadata) {
        mImageMetadata.postValue(imageMetadata);
        mDetectionResults.postValue(results);
    }

    @Override
    public void onMLTaskFailed() {

    }

    public void restartCamera(LifecycleOwner owner) {


        mCameraState.postValue(
                CameraState.RESTARTING
        );

        bindCamera(owner);

//        backgroudn thread wait for 1 second
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mCameraState.postValue(
                    CameraState.STREAMING
            );
        });


    }

    public void unbindML() {
        cameraManager.getCameraController().clearImageAnalysisAnalyzer();
    }

    public void bindML() {
        cameraManager.getCameraController().setImageAnalysisAnalyzer(
                Executors.newSingleThreadExecutor(),
                image -> {
                    detectObjects(image, CameraViewModel.this);
                    image.close();
                }
        );
    }


    public void performSave() {

        mCameraState.postValue(CameraState.SAVING_IMAGE_RESULT);

        String fileName = "temp.jpg";
        File file = new File(context.getFilesDir(), fileName);
        String newFileName = System.currentTimeMillis() + ".jpg";
        File newFile = new File(context.getFilesDir(), newFileName);

//        run on a executor background thread
        Executors.newSingleThreadExecutor().execute(() -> {

            boolean success = file.renameTo(newFile);
//            List<DetectionResult> result = detectionResults.getValue();

            if (!success) {
                mCameraState.postValue(CameraState.ERROR_SAVE_IMAGE);
                return;
            }
            mSavedImageName.postValue(newFileName);


            mlUtils.detectObjects(Uri.fromFile(
                            newFile
                    ),
                    new MLUtils.MLTaskListener() {
                        @Override
                        public void onMLTaskCompleted(List<DetectionResult> results, ImageMetadata imageMetadata) {
                            mShouldGalleryBeEnabled.postValue(true);
                            mImageMetadata.postValue(imageMetadata);
                            mDetectionResults.postValue(results);
                            appRepository.insertResults(results, newFileName, imageMetadata);
                            mCameraState.postValue(CameraState.SAVE_IMAGE_SUCCESS);


                        }

                        @Override
                        public void onMLTaskFailed() {

                        }
                    }
            );


        });

    }

    public void onNavigatedToResult() {
        mCameraState.postValue(CameraState.CAPTURED);
    }

    //     enum class camera state

}