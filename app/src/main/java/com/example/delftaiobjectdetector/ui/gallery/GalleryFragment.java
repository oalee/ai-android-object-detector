package com.example.delftaiobjectdetector.ui.gallery;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.delftaiobjectdetector.R;
import com.example.delftaiobjectdetector.databinding.FragmentGalleryBinding;
import com.example.delftaiobjectdetector.ui.gallery.components.GalleryAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GalleryFragment extends Fragment {

    private GalleryViewModel mViewModel;

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        FragmentGalleryBinding binding = FragmentGalleryBinding.inflate(inflater, container, false);

        mViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        mViewModel.getDetectionResults().observe(getViewLifecycleOwner(), detectionResults -> {
            binding.galleryRecyclerView.setAdapter(new GalleryAdapter(detectionResults));
//            set staggered grid layout manager
            binding.galleryRecyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(
                            2, StaggeredGridLayoutManager.VERTICAL
                    )
            );
            binding.textView.setVisibility(detectionResults.isEmpty() ? View.VISIBLE : View.GONE);
        });

        return binding.getRoot();
    }



}