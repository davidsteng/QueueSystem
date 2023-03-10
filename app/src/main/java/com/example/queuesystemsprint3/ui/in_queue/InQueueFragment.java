package com.example.queuesystemsprint3.ui.in_queue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.queuesystemsprint3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.queuesystemsprint3.databinding.FragmentInQueueBinding;
import com.google.firebase.firestore.SetOptions;


public class InQueueFragment extends Fragment{

    private FragmentInQueueBinding binding;

    //Button to leave queue
    private Button leaveQueueButton;
    //Text view to display queue position
    private TextView queuePositionNum;
    //Text view to display average queue time
    private TextView averageWaitTimeNum;
    //The database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //For testing purposes only, please delete when login implemented
    //Will access from login when needed
    private String userID = "Test Student";
    private String courseID;
    // Find out how to get class name from home fragment

    private boolean pauser = true;

    //Test string
    //private String whyTheShitIsCourseIDAutoUpdating;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        courseID = getArguments().getString("dearGodWork");
        //System.out.println(courseID);
        InQueueModel InQueueModel =
                new ViewModelProvider(this).get(InQueueModel.class);

        binding = FragmentInQueueBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //System.out.println(courseID);
        //System.out.println(whyTheShitIsCourseIDAutoUpdating);
        queuePositionNum = binding.queuePositionNum;
        leaveQueueButton = binding.leaveQueueButton;
        leaveQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(courseID == null) {
                    System.out.println("Wait");
                }
                else {
                    //System.out.println("Leave Queue Attempt");

                    DocumentReference studentQueueExit = db.collection("Students")
                            .document(userID);

                    //System.out.println(courseID);

                    DocumentReference courseQueueExit = db.collection("Courses")
                            .document(courseID);

                    Map<String, Object> studentQueueUpdate = new HashMap<>();
                    studentQueueUpdate.put("inQueue", false);
                    studentQueueUpdate.put("inQueueFor", "");
                    studentQueueExit.set(studentQueueUpdate, SetOptions.merge());

                    courseQueueExit.update("CourseQueue", FieldValue.arrayRemove(userID));

                    Navigation.findNavController(view).navigate(R.id.navigation_home);
                    //System.out.println(courseID);
                }
            }
        });
        //OH GOD FIREBASE API IS ASYNCHRONOUS
        //THATS NOT GOOD
        //Need to somehow make sure courseID is not null before running this line yes

        DocumentReference getWaitlist = db.collection("Courses")
                    .document(courseID);

        getWaitlist.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> tempMapping = document.getData();


                    ArrayList<String> courseWaitlist = (ArrayList<String>) tempMapping.get("CourseQueue");

                    //System.out.println(courseWaitlist);

                    String x = Integer.toString(courseWaitlist.indexOf(userID));
                    queuePositionNum.setText(x);
                }
            }
        });

        //To Be Updated
        averageWaitTimeNum = binding.averageWaitTimeNum;

        //final TextView textView = binding.textInQueue;
        //InQueueModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    public void getCourseID() {
        DocumentReference getCourse = db.collection("Students")
                .document(userID);
        pauser = true;
        getCourse.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> tempMapping = document.getData();

                    //System.out.println(tempMapping);

                    courseID = (String) tempMapping.get("inQueueFor");
                    //String courseIDcopyAttempt = courseID;
                    //whyTheShitIsCourseIDAutoUpdating = courseID;
                    //System.out.println(courseID);
                    pauser = false;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}