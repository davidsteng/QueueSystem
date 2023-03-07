package com.example.queuesystemsprint3.ui.queue;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class QueueModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public QueueModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}