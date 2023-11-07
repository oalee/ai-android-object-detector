package com.example.delftaiobjectdetector.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.delftaiobjectdetector.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}