package com.example.delftaiobjectdetector.ui;

import androidx.lifecycle.ViewModel;

import com.example.delftaiobjectdetector.core.ml.MLUtils;
import com.example.delftaiobjectdetector.core.utils.SharedPrefUtil;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {

//    livedata errors (camera and permission)

    MLUtils mlUtils;
    SharedPrefUtil sharedPrefUtil;



    @Inject
    public MainViewModel(MLUtils mlUtils, SharedPrefUtil sharedPrefUtil) {
        this.mlUtils = mlUtils;
        this.sharedPrefUtil = sharedPrefUtil;


    }

    public void increasePermissionRequestCount() {
        sharedPrefUtil.increasePermissionRequestCount();
    }

    public int getPermissionRequestCount() {
        return sharedPrefUtil.getPermissionRequestCount();
    }

    public void bindToMLLifecycle(MainActivity mainActivity) {
        mlUtils.bindToLifecycle(mainActivity);
    }



}
