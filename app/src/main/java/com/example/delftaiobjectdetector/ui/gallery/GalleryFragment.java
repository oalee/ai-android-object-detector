package com.example.delftaiobjectdetector.ui.gallery;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.transition.Transition;
import android.transition.TransitionInflater;
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
        Transition sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.transition_set);
//
        setReturnTransition(sharedElementEnterTransition);
//        setEnterTransition(sharedElementEnterTransition);

        mViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        binding.galleryRecyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false)
        );
        mViewModel.getDetectionResults().observe(getViewLifecycleOwner(), detectionResults -> {

            binding.galleryRecyclerView.setAdapter(new GalleryAdapter(detectionResults, (imagePath, imageView) -> {
                GalleryFragmentDirections.ActionGalleryFragmentToAnalysisFragment action =
                        GalleryFragmentDirections.actionGalleryFragmentToAnalysisFragment(imagePath);


                Navigator.Extras extras = new androidx.navigation.fragment.FragmentNavigator.Extras.Builder()
                        .addSharedElement(imageView, imagePath)
                        .build();

//                predraw the view
                imageView.getViewTreeObserver().addOnPreDrawListener(() -> {
                    startPostponedEnterTransition();
                    return true;
                });

                findNavController(this).navigate(action, extras);

            },  mViewModel.getSizeManager()) );
//            set staggered grid layout manager

        });

        postponeEnterTransition();
        binding.galleryRecyclerView.getViewTreeObserver().addOnPreDrawListener(() -> {
            startPostponedEnterTransition();
            return true;
        });



        return binding.getRoot();
    }



}