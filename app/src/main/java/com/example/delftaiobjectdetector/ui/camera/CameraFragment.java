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
import com.example.delftaiobjectdetector.ml.LiteModelEfficientdetLite2DetectionDefault1;
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

                    CameraFragmentDirections.ActionCameraFragmentToAnalysisFragment action =
                            CameraFragmentDirections.actionCameraFragmentToAnalysisFragment(
                                    this.uri.toString()
                            );
                    findNavController(this).navigate(action);
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
                              CameraFragment.this.uri = uri;

                                Log.d("CameraFragment", "onImageSaved: " + uri);
                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
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

    private void setPreview(PreviewView previewView) {
        ListenableFuture<ProcessCameraProvider> instance = ProcessCameraProvider.getInstance(requireContext());


        instance.addListener(() -> {

            try {
                ProcessCameraProvider cameraProvider = instance.get();


                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), new YourAnalyzer());

                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                Preview preview = new Preview.Builder().build();

                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner()
                        , cameraSelector, preview, imageAnalysis);


                previewView.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);

                preview.setSurfaceProvider(previewView.getSurfaceProvider());


            } catch (ExecutionException e) {
                e.printStackTrace();
                Log.e("TAG", "Use case binding failed", e);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("TAG", "Use case binding failed", e);
            }

        }, ContextCompat.getMainExecutor(requireContext()));

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(CameraViewModel.class);
    }

    private class YourAnalyzer implements ImageAnalysis.Analyzer {
        // Live detection and tracking

        LocalModel localModel =
                new LocalModel.Builder()
                        .setAssetFilePath("net.tflite")
                        // or .setAbsoluteFilePath(absolute file path to model file)
                        // or .setUri(URI to model file)
                        .build();

        CustomObjectDetectorOptions customObjectDetectorOptions =
                new CustomObjectDetectorOptions.Builder(localModel)
                        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                        .enableMultipleObjects()
                        .build();
        ObjectDetector objectDetector =
                ObjectDetection.getClient(customObjectDetectorOptions);


        private void runModel(ByteBuffer byteBuffer) {
            try {
                LiteModelEfficientdetLite2DetectionDefault1 model = LiteModelEfficientdetLite2DetectionDefault1.newInstance(getContext());


                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 448, 448, 3}, DataType.UINT8);
                inputFeature0.loadBuffer(byteBuffer);

                // Runs model inference and gets result.
                LiteModelEfficientdetLite2DetectionDefault1.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                TensorBuffer outputFeature1 = outputs.getOutputFeature1AsTensorBuffer();
                TensorBuffer outputFeature2 = outputs.getOutputFeature2AsTensorBuffer();
                TensorBuffer outputFeature3 = outputs.getOutputFeature3AsTensorBuffer();

                // Releases model resources if no longer used.
                model.close();
            } catch (IOException e) {


            }

        }

        @OptIn(markerClass = ExperimentalGetImage.class)
        @Override
        public void analyze(ImageProxy imageProxy) {
            Log.d("CameraFragment", "analyze: image");

            Image mediaImage = imageProxy.getImage();
            //Toast.makeText(requireContext(), "Image received", Toast.LENGTH_SHORT).show();
            if (mediaImage != null) {
                InputImage image =
                        InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

                Log.d("CameraFragment", "analyze: image");


                // Pass image to an ML Kit Vision API
                // ...
                objectDetector.process(image)
                        .addOnSuccessListener(detectedObjects -> {
                            // Task completed successfully
                            // ...
                            Logger.getLogger("CameraFragment").info("Detected objects: " + detectedObjects.toString());
                        })
                        .addOnFailureListener(e -> {
                            // Task failed with an exception
                            // ...
                            Logger.getLogger("CameraFragment").info("Detection failed: " + e.getMessage());
                        })
                        .addOnCompleteListener(result -> {
                            // Task completed regardless of success or failure
                            imageProxy.close();
                        });


            }

            imageProxy.close();
        }
    }


}