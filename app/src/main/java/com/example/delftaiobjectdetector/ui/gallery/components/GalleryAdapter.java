package com.example.delftaiobjectdetector.ui.gallery.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.delftaiobjectdetector.R;
import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.utils.SizeManager;
import com.example.delftaiobjectdetector.databinding.ItemDetectedBinding;
import com.example.delftaiobjectdetector.databinding.ItemGalleryBinding;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private final OnClickToNavigateToAnalysisFragment onClickToNavigateToAnalysisFragment;
    private List<List<DetectionResult>> detectionResults;

    public GalleryAdapter(List<List<DetectionResult>> detectionResults, OnClickToNavigateToAnalysisFragment onClickToNavigateToAnalysisFragment) {

        this.detectionResults = detectionResults;
        this.onClickToNavigateToAnalysisFragment = onClickToNavigateToAnalysisFragment;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        List<DetectionResult> detectionResult = detectionResults.get(position);
        holder.setDetectionResult(detectionResult, onClickToNavigateToAnalysisFragment);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gallery, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return detectionResults.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ItemGalleryBinding binding;

        @Inject
        SizeManager sizeManager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemGalleryBinding.bind(itemView);
        }

        public ItemGalleryBinding getBinding() {
            return binding;
        }

        public void setDetectionResult(List<DetectionResult> detectionResult, OnClickToNavigateToAnalysisFragment onClickToNavigateToAnalysisFragment) {

            String imageName = detectionResult.get(0).getImageName();

            File newFile = new File(binding.getRoot().getContext().getFilesDir(), imageName);

            Glide.with(binding.getRoot().getContext())
                    .load(newFile)
                    .into(binding.imageView);


//            sizeManager.setViewWidthAndHeight(
//                    binding.moreImageView, (int) (sizeManager.getWidth() * 0.1f)
//            );


            binding.getRoot().setOnClickListener(v -> {
                onClickToNavigateToAnalysisFragment.onClickToNavigateToAnalysisFragment(imageName);
            });
            binding.graphicOverlay.setOnClickListener(v -> {
                onClickToNavigateToAnalysisFragment.onClickToNavigateToAnalysisFragment(imageName);
            });
            binding.imageView.setOnClickListener(v -> {
                onClickToNavigateToAnalysisFragment.onClickToNavigateToAnalysisFragment(imageName);
            });
            binding.moreImageView.setOnClickListener(v -> {
                onClickToNavigateToAnalysisFragment.onClickToNavigateToAnalysisFragment(imageName);
            });

        }


    }
   public interface OnClickToNavigateToAnalysisFragment {
        void onClickToNavigateToAnalysisFragment(String imageName);
    }

}
