package com.example.delftaiobjectdetector.ui.camera;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import androidx.annotation.OptIn;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.delftaiobjectdetector.databinding.FragmentCameraBinding;
import com.example.delftaiobjectdetector.ui.analysis.AnalysisViewModel;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CameraFragment extends Fragment {

    CameraViewModel mViewModel;

    Uri uri;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(CameraViewModel.class);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentCameraBinding binding = FragmentCameraBinding.inflate(inflater, container, false);

//        setPreview(binding.previewView);
        PreviewView previewView = binding.previewView;
        LifecycleCameraController cameraController = new LifecycleCameraController(requireContext());
        cameraController.bindToLifecycle(this);
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        previewView.setController(cameraController);

        cameraController.setImageCaptureMode(
                ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
        );

        binding.analyzeButton.setOnClickListener(
                v -> {

                    mViewModel.detectObjects(this.uri);
//                    CameraFragmentDirections.ActionCameraFragmentToAnalysisFragment action =
//                            CameraFragmentDirections.actionCameraFragmentToAnalysisFragment(
//                                    this.uri.toString()
//                            );
//                    findNavController(this).navigate(action);
                }
        );

        binding.captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                get time stamp for file name, time millis

                String fileName = System.currentTimeMillis() + ".jpg";

                File file = new File(requireContext().getFilesDir(), fileName);


                ImageCapture.OutputFileOptions outputFileOptions =
                        new ImageCapture.OutputFileOptions.Builder(
                          file
                        ).build();

                cameraController.takePicture(
                        outputFileOptions,
                        Executors.newSingleThreadExecutor(),
                        new ImageCapture.OnImageSavedCallback() {
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                Log.d("CameraFragment", "onImageSaved: " + file.getAbsolutePath());

                              Uri uri=  outputFileResults.getSavedUri();

                                Log.d("CameraFragment", "onImageSaved: " + uri);
                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        CameraFragment.this.uri = uri;

                                        binding.capturedImageView.setImageURI(uri);
                                        binding.capturedImageView.setVisibility(View.VISIBLE);
                                    }
                                });




                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                Log.d("CameraFragment", "onError: " + exception.getMessage());
                            }
                        }
                );
//                cameraController.takePicture(
//
//                );
            }
        });
//
//
//        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build();
//        //imageAnalysis.setAnalyzer(cameraController.getImageAnalysisBackgroundExecutor(), new YourAnalyzer());
//        cameraController.setEnabledUseCases(IMAGE_CAPTURE|IMAGE_ANALYSIS);
//        cameraController.setImageAnalysisAnalyzer(
//                cameraController.getImageAnalysisBackgroundExecutor()
//                , new YourAnalyzer()
//        );
        Log.d("CameraFragment", "analyze: test");

        return binding.getRoot();
    }





}