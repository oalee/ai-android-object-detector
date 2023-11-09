package com.example.delftaiobjectdetector.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.data.source.AppRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class GalleryViewModel extends ViewModel {


    private final AppRepository appRepository;

//    live data
    private final MutableLiveData<List<List<DetectionResult>>> detectionResults = new MutableLiveData<>();



    @Inject
    public GalleryViewModel(
            AppRepository appRepository
    ) {
        this.appRepository = appRepository;
        setUpData();
    }

    private void setUpData() {
//        run on background executor
        Executors.newSingleThreadExecutor().execute(
                () -> {
                    List<DetectionResult> allData = appRepository.getAll();

//        group by image name, and create a list of list of detection results
//        for each image
                    List<List<DetectionResult>> groupedData = new ArrayList<>(allData.stream()
                            .collect(Collectors.groupingBy(DetectionResult::getImageName))
                            .values());

                    detectionResults.postValue(groupedData);
                }
        );

    }
    public LiveData<List<List<DetectionResult>>> getDetectionResults() {
        return detectionResults;
    }
}