package com.example.delftaiobjectdetector.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;

import android.os.Bundle;
import android.widget.Toast;

import com.example.delftaiobjectdetector.R;

import java.util.logging.Logger;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        installSplashScreen();
        SplashScreen.installSplashScreen(this);

        setContentView(R.layout.activity_main);

        handlePermissions();

    }


    private void handlePermissions() {

        ActivityResultLauncher activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {

                    Logger logger = Logger.getLogger("MainActivity");
                    logger.info("Permission request result: " + result.toString());

                    boolean allPermissionsGranted = true;
                    if (result.containsValue(false)) {
                        allPermissionsGranted = false;
                    }
                    if (!allPermissionsGranted) {


//                        Request permissions

//                        ActivityCompat.requestPermissions(this,
//                                new String[]{
//                                        android.Manifest.permission.CAMERA,
//                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                        android.Manifest.permission.READ_EXTERNAL_STORAGE
//                                },
//                                0); // 0 is the request code

                    }
                    else{

                    }
                }
        );

        activityResultLauncher.launch(
                new String[]{
                        android.Manifest.permission.CAMERA,

                }
        );

    }
}