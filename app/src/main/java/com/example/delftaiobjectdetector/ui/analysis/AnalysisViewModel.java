package com.example.delftaiobjectdetector.ui.analysis;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.data.source.AppRepository;
import com.example.delftaiobjectdetector.core.utils.SizeManager;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AnalysisViewModel extends ViewModel {

    private final SizeManager sizeManager;
    private AppRepository appRepository;

    private MutableLiveData<List<DetectionResult>> detectionResults = new MutableLiveData<>();

    public LiveData<List<DetectionResult>> getDetectionResults(String imagePath) {

//        run on background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            detectionResults.postValue(getDetectionResult(imagePath));
        });
        return detectionResults;
    }

    @Inject
    public AnalysisViewModel(AppRepository appRepository, SizeManager sizeManager) {
        this.appRepository = appRepository;
        this.sizeManager = sizeManager;
    }

    public SizeManager getSizeManager() {
        return sizeManager;
    }

    private List<DetectionResult> getDetectionResult(String imagePath) {
//
        return appRepository.getByImagePath(imagePath);
    }
}