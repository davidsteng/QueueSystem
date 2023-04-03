package com.example.queuesystemsprint3.ui.TADashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.queuesystemsprint3.R;
import com.example.queuesystemsprint3.databinding.FragmentTaBinding;
import com.example.queuesystemsprint3.databinding.FragmentTaBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TAFragment extends Fragment implements View.OnClickListener {

    private FragmentTaBinding binding;

    //Button to leave TA Queue
    private Button leaveTAQueueButton;

    //Button to pop from queue
    private Button popButton;

    //Button to add TA course
    private Button addTACourseButton;

    private EditText enterTACourseText;

    private TextView queueList;

    private TextView currentClassTAText;

    private String userID = "Test TA";
    private String currTACourse;

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
        leaveTAQueueButton.setOnClickListener(this::onClick);

        popButton = binding.popButton;
        popButton.setOnClickListener(this::onClickPop);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queueList = (TextView) view.findViewById(R.id.QueueList);

        getQueueList();
        //getCurrentTAClass();
    }

    public void getQueueList() {

        DocumentReference getCourseInfo = db.collection("Courses")
                .document("CS2050");
        getCourseInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList list = (ArrayList) task.getResult().get("CourseQueue");
                    System.out.println(list);
                    if (Objects.requireNonNull(list).size() == 0) {
                        queueList.setText(R.string.queueEmpty);
                    } else {
                        queueList.setText((String) list.get(0));
                    }

                }
            }
        });
    }

    public void getCurrentTAClass() {
        //currentClassTAText = binding.currentClassTAText;
        System.out.println("entered method");
        DocumentReference getTAInfo = db.collection("Students")
                .document("userID");

        getTAInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // need to figure out this method...
                    System.out.println("task successful");
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> tempMapping = document.getData();
                    String isTAFor = (String) tempMapping.get("isTAFor");
                    System.out.println(isTAFor);


                    if (isTAFor != null) {
                        currentClassTAText.setText(isTAFor);
                        System.out.println("set text");
                    } else {
                        currentClassTAText.setText(" ");

                    }

                }
            }
        });
    }

    public void onClickPop(View view) {
        DocumentReference getCourseInfo = db.collection("Courses")
                .document("CS2050");
        getCourseInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList list = (ArrayList) task.getResult().get("CourseQueue");
                    CharSequence text;
                    System.out.println(list);
                    if (Objects.requireNonNull(list).size() == 0) {
                         text = "There are no students in the queue!";
                    } else {
                        String firstInList = (String) list.get(0);
                        //Updating new list
                        getCourseInfo.update("CourseQueue", FieldValue.arrayRemove(firstInList));
                        text = firstInList + " has been popped from the queue!";
                    }
                    // Display student at top of queue
                    Context context = requireActivity().getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

//                    //Remove queue from student's end of the DB
//                    DocumentReference getStudentInfo = db.collection("Students")
//                            .document(firstInList);
//
//                    getStudentInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.isSuccessful()) {
//                                getStudentInfo.update("inQueue", false);
//                                getStudentInfo.update("inQueueFor", "");
//                            }
//                        }
//                    });

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        System.out.println(view.getId());
        enterTACourseText = binding.enterTACourseText;
        currentClassTAText = binding.currentClassTAText;

        String stringEntry = enterTACourseText.getText().toString();
        // check if the entered TA Course code is an actual course code
        DocumentReference getTACourseIDs = db.collection("Courses")
                .document("TACodeList");

        getTACourseIDs.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    switch (view.getId()) {
                        case R.id.addTACourseButton:
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> tempMap = document.getData();
                            Map<String, Object> TAcourseMap = (Map<String, Object>) tempMap.get("TAMap");
                            String TAcourseID = null;
                            if (stringEntry.toString() != null) {
                                TAcourseID = (String) TAcourseMap.get(stringEntry.toString());
                            } else return;

                            if (TAcourseID != null) {
                                // add course to user's isTAFor
                                DocumentReference studentDoc = db.collection("Students")
                                        .document(userID);
                                studentDoc.update("isTAFor", TAcourseID);
                                currTACourse = TAcourseID;
                                // add user as a TA for course
                                DocumentReference courseDoc = db.collection("Courses")
                                        .document(currTACourse);
                                courseDoc.update("TAList", FieldValue.arrayUnion(userID));
                                currentClassTAText.setText(currTACourse);
                            }
                            enterTACourseText.setText("");

                            // TO DO: Make the queue viewable
                        break;
                        case R.id.leaveTAQueueButton:
                            System.out.println("leavingTAQueue Button");
                            // remove course to user's isTAFor
                            DocumentReference studentDoc = db.collection("Students")
                                    .document(userID);
                            studentDoc.update("isTAFor", "");
                            studentDoc.update("isTA", false);
                            // remove user as a TA for course
                            DocumentReference courseDoc = db.collection("Courses")
                                    .document(currTACourse);
                            courseDoc.update("TAList", FieldValue.arrayRemove(userID));

                            // TO DO: Make the queue unviewable
                        break;
                        case R.id.popButton:
                            DocumentReference getCourseInfo = db.collection("Courses")
                                    .document("CS2050");
                            ArrayList list = (ArrayList) task.getResult().get("CourseQueue");

                            String firstInList = (String) list.remove(0);
                            System.out.println("First in list " + firstInList);

                            //Updating new list
                            getCourseInfo.update("CourseQueue", FieldValue.arrayRemove(firstInList));

                            //Funny Average Time Calc Method Do not remove
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                float temp = (float) Instant.now().getEpochSecond();
                                Map <String, Float> finder = (Map<String, Float>) task.getResult().get("TimeWaitCalc");
                                Map <String, Integer> finder2 = (Map<String, Integer>) task.getResult().get("QueueOnJoin");
                                if(finder != null && firstInList != null && finder.get(firstInList) != null && finder2 != null) {
                                    float joinTime = finder.get(firstInList).floatValue();
                                    Integer joinpos = finder2.get(firstInList);
                                    if (joinpos == null) {
                                        break;
                                    }
                                    getCourseInfo.update("totalWaitTime", FieldValue.increment(temp - joinTime));
                                    getCourseInfo.update("AverageTimeWait", (temp - joinTime) / 60 * joinpos.intValue());
                                }
                            }

                            // Display student at top of queue
                            Context context = requireActivity().getApplicationContext();
                            CharSequence text = firstInList + " has been popped from the queue!";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        break;
                        default:
                            throw new RuntimeException("Unknown button ID");
                    }

                }
            }
        });
    }


}