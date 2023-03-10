package com.example.queuesystemsprint3.ui.TADashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.queuesystemsprint3.databinding.FragmentTaBinding;
import com.example.queuesystemsprint3.databinding.FragmentTaBinding;

public class TAFragment extends Fragment {

    private FragmentTaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TAViewModel TAViewModel =
                new ViewModelProvider(this).get(TAViewModel.class);

        binding = FragmentTaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}