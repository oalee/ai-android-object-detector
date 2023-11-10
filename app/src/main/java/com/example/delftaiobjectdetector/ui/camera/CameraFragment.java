package com.example.delftaiobjectdetector.ui.camera;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.example.delftaiobjectdetector.R;
import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.utils.SizeManager;
import com.example.delftaiobjectdetector.databinding.FragmentCameraBinding;
import com.example.delftaiobjectdetector.ui.camera.components.BoundingBoxOverlay;

import java.io.File;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class CameraFragment extends Fragment {

    CameraViewModel mViewModel;

    FragmentCameraBinding binding;

    @Override
    public void onResume() {
        super.onResume();

//        if state is not Captured, bind camera
        CameraState state = mViewModel.cameraState.getValue();
        if (state == CameraState.CAPTURED || state == CameraState.SAVE_IMAGE_SUCCESS) {

          mViewModel.restartCamera(this);
        }
//        log state
        Timber.d( "onResume: " + mViewModel.cameraState.getValue());

        binding.overlay.clear();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater, container, false);
        mViewModel = new ViewModelProvider(this).get(CameraViewModel.class);

        mViewModel.bindCamera(this, binding.previewView);

        SizeManager sizeManager = mViewModel.getSizeManager();
        sizeManager.setCameraHeightPortraitPreview(binding.previewView);
        sizeManager.setCameraHeightPortraitPreview(binding.capturedImageView);
        sizeManager.setViewWidthAndHeight(binding.captureButton, (int) (sizeManager.getWidth() * 0.15f));
        sizeManager.setViewWidthAndHeight(binding.saveButton, (int) (sizeManager.getWidth() * 0.13f));
        sizeManager.setViewWidthAndHeight(binding.galleryButton, (int) (sizeManager.getWidth() * 0.13f));

        mViewModel.cameraState.observe(
                getViewLifecycleOwner(),
                cameraState -> {
                    Timber.d("onCreateView: " + cameraState);
                    if (cameraState == CameraState.STREAMING) {

                        binding.previewView.setVisibility(View.VISIBLE);
                    } else
                    if (cameraState == CameraState.CAPTURING) {
                        animateCapturing();
                    } else if (cameraState == CameraState.RESTARTING) {
                        animateRestarting();
                    }
                    else if (cameraState == CameraState.CAPTURED){
//                        mViewModel.unbindCamera();
                        binding.previewView.setVisibility(View.INVISIBLE);

                        File file = new File(requireContext().getFilesDir(), "temp.jpg");
//                        Glide.with(requireContext()).load(file).into(binding.capturedImageView);
                        binding.capturedImageView.setImageURI(null);
                        binding.capturedImageView.setImageURI(Uri.fromFile(file));
                        binding.capturedImageView.setVisibility(View.VISIBLE);
                        binding.capturedImageView.setAlpha(1f);


                    } else if (cameraState == CameraState.SAVE_IMAGE_SUCCESS) {

                        String newFileName = mViewModel.savedImageName.getValue();
                        CameraFragmentDirections.ActionCameraFragmentToAnalysisFragment action = CameraFragmentDirections.actionCameraFragmentToAnalysisFragment(newFileName);
                        findNavController(this).navigate(action);
                        mViewModel.onNavigatedToResult();
                    }
                }
        );

        mViewModel.detectionResults.observe(
                getViewLifecycleOwner(),
                detectionResults -> {
                    displayOverlay(detectionResults);
                }
        );


        mViewModel.rotationDegrees.observe(
                getViewLifecycleOwner(),
                rotationDegrees -> {
//                    binding.capturedImageView.setRotation(rotationDegrees);

                    Timber.d("onCreateView: " + rotationDegrees);
//                    binding.overlay.setRotation(Math.abs(rotationDegrees-90));
                    binding.overlay.setParentRotation(rotationDegrees);
                }
        );

        binding.saveButton.setOnClickListener(
                v -> {

//                    only save when state is captured
                    if (mViewModel.cameraState.getValue() != CameraState.CAPTURED) {
                        return;
                    }

                    mViewModel.performSave();


                }
        );

        binding.galleryButton.setOnClickListener(
                v -> {

                    findNavController(this).navigate( CameraFragmentDirections.actionCameraFragmentToGalleryFragment());

                }
        );

        binding.captureButton.setOnClickListener(v -> {

//            log state
            Timber.d("onClick: " + mViewModel.cameraState.getValue());
//                only capture when state is streaming
            if (mViewModel.cameraState.getValue() == CameraState.STREAMING) {
//                    initiate capture
                mViewModel.captureImage();
                return;
            }
//            restart camepra if state is captured
            if (mViewModel.cameraState.getValue() == CameraState.CAPTURED) {
                mViewModel.restartCamera(CameraFragment.this);
                return;
            }


        });


        return binding.getRoot();
    }

    public void animateCapturing() {
        // Fade out the captureButton
        binding.captureButton.animate()
                .rotationBy(360)
                .setInterpolator(new LinearInterpolator())
                .setDuration(500) // duration of the fade-out effect
                .withEndAction(() -> {
                    // Change the image resource after the fade-out
                    binding.captureButton.setImageResource(R.drawable.baseline_autorenew_24);
                    // Fade in the captureButton with the new image
                    binding.captureButton.animate()
                            .alpha(1f)
                            .setDuration(200)
                            .start();
                })
                .start();

//        image uri is test.jpg

        // Set the saveButton to be invisible and fully transparent
        binding.saveButton.setAlpha(0.4f);
        binding.saveButton.setVisibility(View.VISIBLE);

// Fade in the saveButton
        binding.saveButton.animate()
                .alpha(1f)
                .setDuration(500) // duration of the fade-in effect
                .start();
    }

    public void animateRestarting() {

// animate previewView to appear
        binding.overlay.clear();
        binding.previewView.setVisibility(View.VISIBLE);
        binding.previewView.animate()
                .alpha(1)
                .setDuration(500) // duration of the fade-out effect
                .withEndAction(() -> {
                    // Set the view to GONE after the fade-out

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
                .alpha(0.4f)
                .setDuration(200) // duration of the fade-out effect
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // Set the view to GONE after the fade-out

                    }
                })
                .start();
    }

    private void displayOverlay(List<DetectionResult> results) {

        if (results == null) {
            binding.overlay.clear();
            binding.overlay.setVisibility(View.GONE);
            return;
        }
        boolean hasResult = false;
        for (DetectionResult result : results) {
            if (result.getScoreAsFloat() >= 0.45) {
                hasResult = true;
                break;
            }
        }
        if (!hasResult) {
            binding.overlay.clear();
            binding.overlay.setVisibility(View.GONE);
            return;
        }


        BoundingBoxOverlay overlay = new BoundingBoxOverlay(binding.overlay, results, mViewModel.imageMetadata.getValue());
        binding.overlay.clear();
        binding.overlay.setVisibility(View.VISIBLE);
        binding.overlay.add(overlay);
    }

}