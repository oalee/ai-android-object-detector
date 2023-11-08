package com.example.delftaiobjectdetector.core.data.source.local.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import androidx.room.TypeConverters;

import com.example.delftaiobjectdetector.core.data.model.DetectionResult;

@Database(entities = {DetectionResult.class}, version = 1)
@TypeConverters({TypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDao appDao();
}
