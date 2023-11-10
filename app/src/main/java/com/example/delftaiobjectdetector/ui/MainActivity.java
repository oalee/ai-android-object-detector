package com.example.delftaiobjectdetector.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.example.delftaiobjectdetector.R;
import com.example.delftaiobjectdetector.core.ml.MLUtils;
import com.example.delftaiobjectdetector.core.utils.SharedPrefUtil;
import com.example.delftaiobjectdetector.ui.home.HomeViewModel;

import java.util.logging.Logger;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {



    private MainViewModel viewModel;

    private ActivityResultLauncher<String> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashScreen.installSplashScreen(this);


        viewModel  = new ViewModelProvider(this).get(MainViewModel.class);


        setContentView(R.layout.activity_main);

        initActivityResultLauncher();


        viewModel.bindToLifecycle(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        handlePermissions();
    }

    private void initActivityResultLauncher() {
        if (activityResultLauncher == null) activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {

                    if (!result) {
                        viewModel.increasePermissionRequestCount();
                        showPermissionExplanationDialog();
                    }
                }
        );
    }
    //
    private void handlePermissions() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//        // Permissions already granted
            return;
        }


        activityResultLauncher.launch(

                android.Manifest.permission.CAMERA

        );


    }


    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage("This app requires camera permission to function. Please grant the permission.")
                .setPositiveButton("OK", (dialog, which) -> requestPermissions())
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .create().show();


    }


    private void requestPermissions() {

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return;
        }

//        only for android 11 and above
        if (viewModel.getPermissionRequestCount() >= 2 && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {

//            go directly to settings
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);

            Toast.makeText(this, "Please grant camera permission in settings", Toast.LENGTH_SHORT).show();
            startActivity(intent);

            return;
        }
        activityResultLauncher.launch(

                android.Manifest.permission.CAMERA

        );

    }

}