package com.example.delftaiobjectdetector.core.data.source;

import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.data.model.ImageMetadata;
import com.example.delftaiobjectdetector.core.data.source.local.LocalDataSource;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AppRepository {

    private final
    LocalDataSource localDataSource;

    @Inject
    public AppRepository(LocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }


    public List<ImageMetadata> getAllImageMetaData() {
        return localDataSource.getAllImageMetaData();
    }

    public ImageMetadata getByImageName(String imageName) {
        return localDataSource.getByImageName(imageName);
    }

    public List<String> getImages() {
        return localDataSource.getImages();
    }

    public List<DetectionResult> getByImagePath(String imagePath) {

        return localDataSource.getByImagePath(imagePath);
    }

    public DetectionResult getById(int id) {
        return localDataSource.getById(id);
    }


    public void insertResults(List<DetectionResult> detectionResults, String imagePath, ImageMetadata imageMetadata) {



//        run on background thread

            localDataSource.insertAll(detectionResults, imagePath);
            localDataSource.insertImageMetadata(imageMetadata);

//        localDataSource.insertAll(detectionResults, imagePath);
    }

    public void deleteByImagePath(String imagePath) {
        localDataSource.deleteByImagePath(imagePath);
    }

    public List<DetectionResult> getAll() {
        return localDataSource.getAll();
    }

}
