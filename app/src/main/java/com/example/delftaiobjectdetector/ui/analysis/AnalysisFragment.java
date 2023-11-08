package com.example.delftaiobjectdetector.ui.analysis;

import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.delftaiobjectdetector.R;
import com.example.delftaiobjectdetector.databinding.FragmentAnalysisBinding;
import com.example.delftaiobjectdetector.ui.camera.components.BoundingBoxOverlay;

import java.io.File;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AnalysisFragment extends Fragment {

    private AnalysisViewModel mViewModel;
    private FragmentAnalysisBinding binding;

    private String imagePath;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
         binding = FragmentAnalysisBinding.inflate(inflater, container, false);

//        get arguments from bundle
        AnalysisFragmentArgs args = AnalysisFragmentArgs.fromBundle(getArguments());
        imagePath = args.getImageUri();

        File newFile = new File(requireContext().getFilesDir(), imagePath);

        binding.capturedImageView.setImageURI(Uri.fromFile(newFile));



        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(AnalysisViewModel.class);

        mViewModel.getDetectionResults(imagePath).observe(getViewLifecycleOwner(), detectionResults -> {
            if (detectionResults == null) {
                return;
            }
            binding.analysisRecyclerView.setAdapter(new DetectedItemsAdapter(detectionResults));

            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            binding.analysisRecyclerView.setLayoutManager(layoutManager);

//            overlay
            BoundingBoxOverlay overlay = new BoundingBoxOverlay(binding.overlay, detectionResults);
            binding.overlay.add(overlay);
        });
    }
}