package com.example.delftaiobjectdetector.ui.analysis.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delftaiobjectdetector.R;
import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.databinding.ItemDetectedBinding;

import java.util.List;

public class DetectedItemsAdapter extends RecyclerView.Adapter<DetectedItemsAdapter.ViewHolder> {

    private
    List<DetectionResult> detectionResults;

    public DetectedItemsAdapter(List<DetectionResult> detectionResults) {

        this.detectionResults = detectionResults;

    }

    @NonNull
    @Override
    public DetectedItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detected, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetectedItemsAdapter.ViewHolder holder, int position) {

        DetectionResult detectionResult = detectionResults.get(position);
        holder.setDetectionResult(detectionResult);
    }

    @Override
    public int getItemCount() {
        return  detectionResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemDetectedBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemDetectedBinding.bind(itemView);
        }

        public ItemDetectedBinding getBinding() {
            return binding;
        }


        public void setDetectionResult(DetectionResult detectionResult) {

            String category = detectionResult.getCategoryAsString();
//            capitalize first letter
            category = category.substring(0, 1).toUpperCase() + category.substring(1);

            float confidence = detectionResult.getScoreAsFloat();

//            join category and confidence, e.g. "Person (0.99)"
            String name = category ;

            binding.detectedItemTextView.setText( name);

//            2 decimal places
            binding.confidenceTextView.setText("Confidence:" + String.format("%.2f", confidence));

//            binding.de.setText(String.valueOf(detectionResult.getConfidence()));
        }
    }
}
