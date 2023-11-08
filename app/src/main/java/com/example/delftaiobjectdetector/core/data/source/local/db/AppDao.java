package com.example.delftaiobjectdetector.core.data.source.local.db;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.delftaiobjectdetector.core.data.model.DetectionResult;

import java.util.List;

@Dao
public interface AppDao {
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
    void insertAll(DetectionResult... detectionResults);

    @Query("DELETE FROM detections WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM detections WHERE image_path = :imagePath")
    void deleteByImagePath(String imagePath);

}
