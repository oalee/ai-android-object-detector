package com.example.delftaiobjectdetector.ui.analysis;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.data.model.ImageMetadata;
import com.example.delftaiobjectdetector.core.data.source.AppRepository;
import com.example.delftaiobjectdetector.core.utils.ImageManager;
import com.example.delftaiobjectdetector.core.utils.SizeManager;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AnalysisViewModel extends ViewModel {

    private final SizeManager sizeManager;
    private final ImageManager imageManager;
    private AppRepository appRepository;

    private final MutableLiveData<CombinedImageMetadata> combinedImageMetadataMutableLiveData = new MutableLiveData<>();

    public LiveData<CombinedImageMetadata> getCombinedImageMetadataMutableLiveData() {
        return combinedImageMetadataMutableLiveData;
    }

    @Inject
    public AnalysisViewModel(AppRepository appRepository, SizeManager sizeManager, ImageManager imageManager) {
        this.appRepository = appRepository;
        this.sizeManager = sizeManager;
        this.imageManager = imageManager;


    }

    public void loadData( String imagePath) {
        Executors.newSingleThreadExecutor().execute(() -> {
            ImageMetadata imageMetadata = appRepository.getByImageName(imagePath);
            List<DetectionResult> detectionResults = getDetectionResult(imagePath);
            CombinedImageMetadata combinedImageMetadata = new CombinedImageMetadata(imageMetadata, detectionResults);
            combinedImageMetadataMutableLiveData.postValue(combinedImageMetadata);
        });
    }

    public ImageManager getImageManager() {
        return imageManager;
    }

    public SizeManager getSizeManager() {
        return sizeManager;
    }

    private List<DetectionResult> getDetectionResult(String imagePath) {
//
        return appRepository.getByImagePath(imagePath);
    }

    public class CombinedImageMetadata {
        private final ImageMetadata imageMetadata;
        private final List<DetectionResult> detectionResults;

        public CombinedImageMetadata(ImageMetadata imageMetadata, List<DetectionResult> detectionResults) {
            this.imageMetadata = imageMetadata;
            this.detectionResults = detectionResults;
        }

        public ImageMetadata getImageMetadata() {
            return imageMetadata;
        }

        public List<DetectionResult> getDetectionResults() {
            return detectionResults;
        }
    }
}