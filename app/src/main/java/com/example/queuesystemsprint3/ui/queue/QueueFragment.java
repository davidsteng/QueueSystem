package com.example.queuesystemsprint3.ui.queue;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.queuesystemsprint3.MainActivity;
import com.example.queuesystemsprint3.R;
import com.example.queuesystemsprint3.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.WriteResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QueueFragment extends Fragment implements View.OnClickListener{

    //Dear god dont touch this variable decleration
    //Need it to grab the rest of the buttons
    private FragmentHomeBinding binding;
    //Button to add Course
    private Button addCourseButton;
    //Button to join queue
    private Button joinQueueButton;
    //Course Dropdown Selector
    private Spinner courseDropdown;
    //The entire fucking database. Do not alter bad things
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //For testing purposes only, please delete when login implemented
    //Will access from login when needed
    private String userID = "Test Student";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        QueueModel queueModel =
                new ViewModelProvider(this).get(QueueModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        addCourseButton = binding.addCourseButton;
        addCourseButton.setOnClickListener(this);

        courseDropdown = binding.courseDropdown;

        //update Spinner on Startup Please dear god don't break
        DocumentReference getDropdown = db.collection("Students")
                .document(userID);
        getDropdown.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> tempMapping = document.getData();

                    //System.out.println(document);
                    //System.out.println(tempMapping);

                    ArrayList<String> courseMapList = (ArrayList<String>) tempMapping.get("CourseList");

                    //System.out.println(courseMapList);

                    Object[] courseMaps = courseMapList.toArray();
                    String[] courseMap = Arrays.stream(courseMaps).toArray(String[]::new);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, courseMap);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    courseDropdown.setAdapter(adapter);
                }
            }
        });

        joinQueueButton = binding.joinQueueButton;

        //Can I do it like this without crashing
        //Probably not prepare to comment out this method
        //Begin
        joinQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("Join Queue Button Flag");
                String dropdownSelect = courseDropdown.getSelectedItem().toString();
                //If Spinner empty exit the button input
                if(dropdownSelect == null) {
                    //Insert torch response?
                    return;
                }
                joinQueue(userID, dropdownSelect);

                Bundle bundle = new Bundle();
                bundle.putString("dearGodWork", dropdownSelect);

                Navigation.findNavController(view).navigate(R.id.navigation_queue, bundle);
            }
        });
        //End

        return root;
    }

    //Method to connect to join queue button
    public void joinQueue(String userID, String CourseID) {
        if(CourseID == null) return;
        Map<String, Object> studentQueueUpdate = new HashMap<>();
        studentQueueUpdate.put("inQueue", true);
        studentQueueUpdate.put("inQueueFor", CourseID);
        db.collection("Students").document(userID)
                .set(studentQueueUpdate, SetOptions.merge());
        DocumentReference courseQueueUpdate = db.collection("Courses")
                .document(CourseID);
        courseQueueUpdate.update("CourseQueue", FieldValue.arrayUnion(userID));
        courseQueueUpdate.update("totalQueueSize", FieldValue.increment(1));
        Map<String, Object> addTimeWait = new HashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            addTimeWait.put(userID, Instant.now().getEpochSecond());
            courseQueueUpdate.update("TimeWaitCalc", addTimeWait);
            courseQueueUpdate.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentReference getCourseInfo = db.collection("Courses")
                                .document(CourseID);
                        Integer pos = (Integer) task.getResult().get("totalQueueSize");
                        Map<String, Object> QueueSize = new HashMap<>();
                        QueueSize.put(userID, pos);
                        courseQueueUpdate.update("QueueOnJoin", QueueSize);
                    }
                }
            });
        }
        System.out.println("I did not crash");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        EditText enterCode = binding.textView;
        String stringEntry = enterCode.getText().toString();
        DocumentReference getCourses = db.collection("Courses").document("CodeList");
        getCourses.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    //System.out.println("Test");
                    DocumentSnapshot document = task.getResult();
                    //System.out.println(document.getData());
                    Map<String, Object> tempMap = document.getData();
                    Map<String, Object> courseMap = (Map<String, Object>) tempMap.get("CourseMap");
                    //System.out.println(courseMap);
                    //Why the fuck isnt this working
                    //Oh I forgot to capitalize maps haha
                    //System.out.println(courseMap.size());
                    //System.out.println(stringEntry);
                    String courseID = null;
                    if(stringEntry.toString() != null) {
                        courseID = (String) courseMap.get(stringEntry.toString());
                    }
                    else return;
                    //System.out.println(courseID);

                    if(courseID != null) {
                        DocumentReference studentDoc = db.collection("Students")
                                .document(userID);
                        studentDoc.update("CourseList", FieldValue.arrayUnion(courseID));
                    }
                }
            }
        });

        //Update Spinner
        DocumentReference getDropdown = db.collection("Students")
                .document(userID);
        getDropdown.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> tempMapping = document.getData();
                    ArrayList<String> courseMapList = (ArrayList<String>) tempMapping.get("CourseList");
                    Object[] courseMaps = courseMapList.toArray();
                    String[] courseMap = Arrays.stream(courseMaps).toArray(String[]::new);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, courseMap);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    courseDropdown.setAdapter(adapter);
                }
            }
        });
    }
}