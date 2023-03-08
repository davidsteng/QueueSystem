package com.example.queuesystemsprint3.ui.in_queue;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InQueueModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public InQueueModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}