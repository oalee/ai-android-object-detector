package com.example.delftaiobjectdetector.core.di;

import android.content.Context;

import androidx.room.Room;

import com.example.delftaiobjectdetector.core.data.source.local.db.AppDatabase;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    public static AppDatabase provideAppDatabase(
            @ApplicationContext Context context
            ) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                "delftaiobjectdetector.db"
        ).build();
    }
}
