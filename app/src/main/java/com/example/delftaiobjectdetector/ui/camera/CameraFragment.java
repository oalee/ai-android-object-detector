package com.example.delftaiobjectdetector.ui.camera;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.CameraController;
import androidx.camera.view.LifecycleCameraController;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.ViewModelProvider;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.delftaiobjectdetector.R;
import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.ml.MLUtils;
import com.example.delftaiobjectdetector.databinding.FragmentCameraBinding;
import com.example.delftaiobjectdetector.ui.camera.components.BoundingBoxOverlay;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CameraFragment extends Fragment implements MLUtils.MLTaskListener {

    CameraViewModel mViewModel;

    FragmentCameraBinding binding;
    Uri uri;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(CameraViewModel.class);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);

//        setPreview(binding.previewView);
        PreviewView previewView = binding.previewView;
        LifecycleCameraController cameraController = new LifecycleCameraController(requireContext());
        cameraController.bindToLifecycle(this);
        cameraController.setCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA);
        previewView.setController(cameraController);
//
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




        cameraController.setImageAnalysisAnalyzer(
                Executors.newSingleThreadExecutor(),
                new ImageAnalysis.Analyzer() {
                    @OptIn(markerClass = ExperimentalGetImage.class) @Override
                    public void analyze(@NonNull ImageProxy image) {
                        Log.d("CameraFragment", "analyze: test");
                        Image mediaImage = image.getImage();
                        if (mediaImage != null) {
                            InputImage inputImage =
                                    InputImage.fromMediaImage(mediaImage, image.getImageInfo().getRotationDegrees());
//                            image is not rotated for some reason
//                            manually rotate

                            mViewModel.detectObjects(inputImage, CameraFragment.this);
                        }
                        image.close();
                    }
                }
        );

        binding.saveButton.setOnClickListener(
                v -> {
                    String fileName = "temp.jpg";

                    File file = new File(requireContext().getFilesDir(), fileName);

                    String newFileName = System.currentTimeMillis() + ".jpg";

                    File newFile = new File(requireContext().getFilesDir(), newFileName);

                    boolean success = file.renameTo(newFile);

                   List<DetectionResult> result =  mViewModel.detectionResults.getValue();

                    if (result != null) {

                        CameraFragmentDirections.ActionCameraFragmentToAnalysisFragment action = CameraFragmentDirections.actionCameraFragmentToAnalysisFragment(newFileName);

                        mViewModel.insertResults(result.toArray(new DetectionResult[0]), newFileName);

                        Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show();

                        findNavController(this).navigate(action);
                    }

                }
        );

        binding.captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                get time stamp for file name, time millis
                if (CameraFragment.this.uri != null) {

//                    animate previewView to appear
                    binding.previewView.setVisibility(View.VISIBLE);
                    binding.previewView.animate()
                            .alpha(1)
                            .setDuration(500) // duration of the fade-out effect
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    // Set the view to GONE after the fade-out

                                }
                            })
                            .start();


                    binding.captureButton.animate()

                            .rotationBy(360)
                            .setInterpolator(new LinearInterpolator())
                            .setDuration(500) // duration of the fade-out effect
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    // Change the image resource after the fade-out
                                    binding.captureButton.setImageResource(R.drawable.baseline_camera_24);
                                    // Start fade-in effect after the image resource has been changed
                                    binding.captureButton.animate()
                                            .alpha(1f)
                                            .setDuration(200)
                                            .start();
                                }
                            })
                            .start();

                   // binding.capturedImageView.setVisibility(View.GONE);
                    cameraController.bindToLifecycle(CameraFragment.this);
                    CameraFragment.this.uri = null;
                    binding.capturedImageView.animate()
                            .alpha(0f)

                            .setDuration(200) // duration of the fade-out effect
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    // Set the view to GONE after the fade-out
                                    binding.capturedImageView.setVisibility(View.GONE);
                                }
                            })
                            .start();
                    binding.saveButton.animate()
                            .alpha(0f)
                            .setDuration(200) // duration of the fade-out effect
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    // Set the view to GONE after the fade-out
                                    binding.saveButton.setVisibility(View.GONE);
                                }
                            })
                            .start();


                    return;
                }

                String fileName = "temp.jpg";

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

//                                stop camera
                                        cameraController.unbind();

                                        CameraFragment.this.uri = uri;

                                        // Fade out the captureButton
                                        binding.captureButton.animate()
                                                .rotationBy(360)
                                                .setInterpolator(new LinearInterpolator())
                                                .setDuration(500) // duration of the fade-out effect
                                                .withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // Change the image resource after the fade-out
                                                        binding.captureButton.setImageResource(R.drawable.baseline_autorenew_24);
                                                        // Fade in the captureButton with the new image
                                                        binding.captureButton.animate()
                                                                .alpha(1f)
                                                                .setDuration(200)
                                                                .start();
                                                    }
                                                })
                                                .start();
                                        binding.capturedImageView.setImageURI(uri);
                                        binding.capturedImageView.setVisibility(View.VISIBLE);
                                        // Set the saveButton to be invisible and fully transparent
                                        binding.saveButton.setAlpha(0f);
                                        binding.saveButton.setVisibility(View.VISIBLE);

// Fade in the saveButton
                                        binding.saveButton.animate()
                                                .alpha(1f)
                                                .setDuration(500) // duration of the fade-in effect
                                                .start();

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


    @Override
    public void onMLTaskCompleted(List<DetectionResult> results) {

//        check if there is one result with a score higher than 0.5
        boolean hasResult = false;
        for (DetectionResult result : results) {
            if (result.getScoreAsFloat() >= 0.5) {
                hasResult = true;
                break;
            }
        }

        if (!hasResult) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(requireContext(), "No objects detected", Toast.LENGTH_SHORT).show();
                    binding.overlay.clear();
                    binding.overlay.setVisibility(View.GONE);
                }
            });
            return;
        }

        BoundingBoxOverlay overlay = new BoundingBoxOverlay(binding.overlay, results);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.overlay.clear();
                binding.overlay.setVisibility(View.VISIBLE);
                binding.overlay.add(overlay);
            }
        });
    }

    @Override
    public void onMLTaskFailed() {

    }
}