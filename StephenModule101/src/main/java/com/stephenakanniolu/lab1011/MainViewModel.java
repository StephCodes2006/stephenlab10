//Stephen Akanniolu n01725208
package com.stephenakanniolu.lab1011;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<Boolean> _isReady = new MutableLiveData<>(false);
    public LiveData<Boolean> isReady() {
        return _isReady;
    }

    public MainViewModel() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            _isReady.setValue(true);
        }, 5000);
    }
}
