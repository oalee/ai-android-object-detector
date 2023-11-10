package com.example.delftaiobjectdetector.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.example.delftaiobjectdetector.R;

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


        viewModel = new ViewModelProvider(this).get(MainViewModel.class);


        setContentView(R.layout.activity_main);
        handlePermission(); // handle permission
        viewModel.bindToMLLifecycle(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void handlePermission() {
        if (activityResultLauncher == null) activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (!result) {

                        if (viewModel.getPermissionRequestCount() < 1) {
                            showPermissionExplanationDialog();
                            viewModel.increasePermissionRequestCount();
                        }


                    }
                }
        );

        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//        // Permissions already granted
            return;
        }

//        if less than two
        if (viewModel.getPermissionRequestCount() < 1) {
            activityResultLauncher.launch(

                    android.Manifest.permission.CAMERA

            );
            return;
        }


    }



    private void showPermissionExplanationDialog() {

        String title;
        String message;
        String negativeMessage = "Enable Permissions";

        DialogInterface.OnClickListener negativeButtonListener;

        int requestCount = 1;


        Timber.d("request count: %s", requestCount);

        if (requestCount == 1) {
            // For the second request
            title = "Final Request for Camera Permission!!";
            message = "This is the last time we're asking. To enable real-time object detection, please grant camera permission. If you decline, you'll need to enable it later in the app settings.";

        } else  {
            // For the first request
            title = "Camera Permission Required";
            message = "This app requires camera permission to detect objects in real-time! Grant the permission or browse the app to see the detected objects.";
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Browse Gallery", (dialog, which) ->  dialog.dismiss())
                .setNegativeButton("Enable Permissions", (dialog, which) ->  requestPermissions())
                .create().show();


    }


    private void requestPermissions() {

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return;
        }

//
        activityResultLauncher.launch(

                android.Manifest.permission.CAMERA

        );



    }

    private void gotToSystemSettingsForPermission() {

        if (viewModel.getPermissionRequestCount() >= 2 && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {

//            go directly to settings
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);

            return;
        }
    }

}