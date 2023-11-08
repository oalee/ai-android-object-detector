package com.example.delftaiobjectdetector.ui.analysis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.delftaiobjectdetector.R;
import com.example.delftaiobjectdetector.databinding.ItemDetectedBinding;

public class DetectedItemsAdapter extends RecyclerView.Adapter<DetectedItemsAdapter.ViewHolder>{

    public DetectedItemsAdapter() {
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

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {
        ItemDetectedBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemDetectedBinding.bind(itemView);
        }


    }
}
