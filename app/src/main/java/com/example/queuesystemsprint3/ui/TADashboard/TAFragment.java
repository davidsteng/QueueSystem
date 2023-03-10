package com.example.queuesystemsprint3.ui.TADashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.queuesystemsprint3.R;
import com.example.queuesystemsprint3.databinding.FragmentTaBinding;
import com.example.queuesystemsprint3.databinding.FragmentTaBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class TAFragment extends Fragment implements View.OnClickListener {

    private FragmentTaBinding binding;

    //Button to leave TA Queue
    private Button leaveTAQueueButton;

    //Button to pop from queue
    private Button popButton;

    //Button to add TA course
    private Button addTACourseButton;

    private EditText enterTACourseText;

    private ListView QueueList;

    private String userID = "Test TA";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TAViewModel TAViewModel =
                new ViewModelProvider(this).get(TAViewModel.class);

        binding = FragmentTaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        addTACourseButton = binding.addTACourseButton;
        addTACourseButton.setOnClickListener(this::onClick);

        leaveTAQueueButton = binding.leaveTAQueueButton;
        popButton = binding.popButton;



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        enterTACourseText = binding.enterTACourseText;
        String stringEntry = enterTACourseText.getText().toString();
        // check if the entered TA Course code is an actual course code
        DocumentReference getTACourseIDs = db.collection("Courses")
                .document("TACodeList");

        getTACourseIDs.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                }
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> tempMap = document.getData();
                    Map<String, Object> TAcourseMap = (Map<String, Object>) tempMap.get("TAMap");
                    String TAcourseID = null;
                    if(stringEntry.toString() != null) {
                        TAcourseID = (String) TAcourseMap.get(stringEntry.toString());
                    }
                    else return;

                    if(TAcourseID != null) {
                        DocumentReference studentDoc = db.collection("Students")
                                .document(userID);
                        studentDoc.update("isTAFor", TAcourseID);
                    }
                }
            });
        }
}