package com.example.delftaiobjectdetector.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.data.source.AppRepository;
import com.example.delftaiobjectdetector.core.utils.SizeManager;

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
    private final SizeManager sizeManager;


    @Inject
    public GalleryViewModel(
            AppRepository appRepository,
            SizeManager sizeManager
    ) {
        this.sizeManager = sizeManager;
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

//                    sort list by parsed date( detectionResult.parseDate) in descending order (whole list not each sublist)
                    groupedData.sort((o1, o2) -> {
                        DetectionResult detectionResult1 = o1.get(0);
                        DetectionResult detectionResult2 = o2.get(0);
                        long firstDate = detectionResult1.parseImageCreationTime();
                        long secondDate = detectionResult2.parseImageCreationTime();

                        return Long.compare(secondDate, firstDate);
                    });

                    detectionResults.postValue(groupedData);
                }
        );

    }
    public LiveData<List<List<DetectionResult>>> getDetectionResults() {
        return detectionResults;
    }

    public SizeManager getSizeManager() {
        return sizeManager;
    }
}