package com.example.delftaiobjectdetector.core.data.source.local;

import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.data.model.ImageMetadata;
import com.example.delftaiobjectdetector.core.data.source.local.db.AppDatabase;
import com.example.delftaiobjectdetector.core.di.DatabaseModule;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LocalDataSource {

    private final
    AppDatabase appDatabase;

    @Inject
    public LocalDataSource(AppDatabase database) {
        this.appDatabase = database;
    }

    public List<String> getImages() {
        return appDatabase.appDao().getImages();
    }

    public List<DetectionResult> getByImagePath(String imagePath) {
        return appDatabase.appDao().getByImagePath(imagePath);
    }

    public DetectionResult getById(int id) {
        return appDatabase.appDao().getById(id);
    }

    public void insert(DetectionResult detectionResult) {
        appDatabase.appDao().insert(detectionResult);
    }


    public void deleteByImagePath(String imagePath) {
        appDatabase.appDao().deleteByImagePath(imagePath);
    }

    public void deleteById(int id) {
        appDatabase.appDao().deleteById(id);
    }

    public void insertAll(List<DetectionResult> detectionResults, String imagePath) {

        for (DetectionResult detectionResult : detectionResults) {
            detectionResult.setImageName(imagePath);
        }
        appDatabase.appDao().insertAll(detectionResults);
    }


    public List<DetectionResult> getAll() {
        return appDatabase.appDao().getAll();
    }

    public void insertImageMetadata(ImageMetadata imageMetadata) {
        appDatabase.appDao().insert(imageMetadata);
    }

    public List<ImageMetadata> getAllImageMetaData() {
        return appDatabase.appDao().getAllImageMetaData();
    }

    public ImageMetadata getByImageName(String imageName) {
        return appDatabase.appDao().getByImageName(imageName);
    }
}
