package com.example.delftaiobjectdetector.core.data.source.local.db;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.delftaiobjectdetector.core.data.model.DetectionResult;
import com.example.delftaiobjectdetector.core.data.model.ImageMetadata;

import java.util.List;

@Dao
public interface AppDao {

//    write queries for ImageMetadata
//    get all

    @Query("SELECT * FROM image_metadata")
    List<ImageMetadata> getAllImageMetaData();

    @Query("SELECT * FROM image_metadata WHERE image_name = :imageName")
    ImageMetadata getByImageName(String imageName);

    @Insert
    long insert(ImageMetadata imageMetadata);


    @Query("SELECT * FROM detections")
    List<DetectionResult>  getAll();

    @Query("SELECT * FROM detections WHERE id = :id")
    DetectionResult  getById(int id);

    @Query("SELECT * FROM detections WHERE image_path = :imagePath")
    List<DetectionResult> getByImagePath(String imagePath);

//    unique image paths

    @Query("SELECT DISTINCT image_path FROM detections")
    List<String> getImages();

//    insert
    @Insert
    void insert(DetectionResult detectionResult);

//    insert all, array
    @Insert
    void insertAll(List<DetectionResult> detectionResults);

    @Query("DELETE FROM detections WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM detections WHERE image_path = :imagePath")
    void deleteByImagePath(String imagePath);

}
