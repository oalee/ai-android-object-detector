package com.example.delftaiobjectdetector.ui.analysis;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
import com.example.delftaiobjectdetector.R;
import com.example.delftaiobjectdetector.databinding.FragmentAnalysisBinding;
import com.example.delftaiobjectdetector.ui.analysis.components.DetectedItemsAdapter;
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

        AnalysisFragmentArgs args = AnalysisFragmentArgs.fromBundle(getArguments());
        imagePath = args.getImageUri();

        binding = FragmentAnalysisBinding.inflate(inflater, container, false);



         mViewModel = new ViewModelProvider(this).get(AnalysisViewModel.class);
//        TransitionInflater sharedElementReturnTransition =  TransitionInflater.from(this.context).inflateTransition(R.transition.change_bounds)

        Transition sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.transition_set);

        sharedElementEnterTransition.setDuration(400);
        setSharedElementEnterTransition(sharedElementEnterTransition);
        postponeEnterTransition();





//        get arguments from bundle
        binding.capturedImageView.setTransitionName(imagePath);


        File newFile = new File(requireContext().getFilesDir(), imagePath);

//        mViewModel.getSizeManager().setCameraHeightBasedOnImageSize(
//                binding.capturedImageView, Uri.fromFile(newFile)
//        );

//        load with glide and and load, then start transition
        Glide.with(this)
                .load(newFile)
                .listener(
                        new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }


                            @Override
                            public boolean onResourceReady(android.graphics.drawable.Drawable resource, java.lang.Object model, com.bumptech.glide.request.target.Target target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {

                                startPostponedEnterTransition();
//                                      binding.scrollView.getViewTreeObserver().addOnPreDrawListener(
//                new ViewTreeObserver.OnPreDrawListener() {
//                    @Override
//                    public boolean onPreDraw() {
//                        binding.scrollView.getViewTreeObserver().removeOnPreDrawListener(this);
//                        startPostponedEnterTransition();
//                        return true;
//                    }
//                }
//        );
                                return false;
                            }
                        }
                )
                .format(com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888)
                .dontTransform()
                .into(binding.capturedImageView);


        mViewModel.loadData(imagePath);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        binding.analysisRecyclerView.setLayoutManager(layoutManager);

        mViewModel.getCombinedImageMetadataMutableLiveData().observe(
                getViewLifecycleOwner(), combinedImageMetadata -> {
                    if (combinedImageMetadata == null) {
                        return;
                    }
                    binding.analysisRecyclerView.setAdapter(new DetectedItemsAdapter(combinedImageMetadata.getDetectionResults(), combinedImageMetadata.getImageMetadata(), mViewModel.getImageManager()));

                    binding.overlay.clear();
                    BoundingBoxOverlay overlay = new BoundingBoxOverlay(binding.overlay, combinedImageMetadata.getDetectionResults(), combinedImageMetadata.getImageMetadata());
                    binding.overlay.add(overlay);
                }
        );




        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
}