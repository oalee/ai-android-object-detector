package com.example.delftaiobjectdetector.ui.gallery.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
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
    private final SizeManager sizeManager;
    private List<List<DetectionResult>> detectionResults;

    public GalleryAdapter(List<List<DetectionResult>> detectionResults, OnClickToNavigateToAnalysisFragment onClickToNavigateToAnalysisFragment, SizeManager sizeManager) {

        this.detectionResults = detectionResults;
        this.onClickToNavigateToAnalysisFragment = onClickToNavigateToAnalysisFragment;
        this.sizeManager = sizeManager;
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

            Glide.with(binding.imageView)
                    .load(newFile)
                    .override((int) (sizeManager.getWidth() * 0.4f)) // use the actual width and height in pixels, or calculate the size
                    .format(DecodeFormat.PREFER_RGB_565)
                    .into(binding.imageView);



            binding.imageView.setTransitionName(imageName);


            binding.getRoot().setOnClickListener(v -> {
                onClickToNavigateToAnalysisFragment.onClickToNavigateToAnalysisFragment(imageName, binding.imageView);
            });
//            binding.graphicOverlay.setOnClickListener(v -> {
//                onClickToNavigateToAnalysisFragment.onClickToNavigateToAnalysisFragment(imageName);
//            });
            binding.imageView.setOnClickListener(v -> {
                onClickToNavigateToAnalysisFragment.onClickToNavigateToAnalysisFragment(imageName, binding.imageView);
            });
            binding.moreImageView.setOnClickListener(v -> {
                onClickToNavigateToAnalysisFragment.onClickToNavigateToAnalysisFragment(imageName, binding.imageView);
            });

        }


    }
   public interface OnClickToNavigateToAnalysisFragment {
        void onClickToNavigateToAnalysisFragment(String imageName, View view);
    }

}
