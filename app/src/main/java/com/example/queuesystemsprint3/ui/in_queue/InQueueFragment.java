package com.example.queuesystemsprint3.ui.in_queue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.WriteResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.queuesystemsprint3.databinding.FragmentInQueueBinding;
import com.example.queuesystemsprint3.databinding.FragmentNotificationsBinding;
import com.example.queuesystemsprint3.ui.notifications.NotificationsViewModel;

import org.w3c.dom.Text;

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
    private String userID = "Test Student 2";

    // Find out how to get class name from home fragment
    private String courseID = "CS2050";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InQueueModel InQueueModel =
                new ViewModelProvider(this).get(InQueueModel.class);

        binding = FragmentInQueueBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        leaveQueueButton = binding.leaveQueueButton;
        leaveQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Join Queue Button Flag");
                Navigation.findNavController(view).navigate(R.id.navigation_home);
                // TO DO: delete student from queue when leaving!! (by index??)
            }
        });


        queuePositionNum = binding.queuePositionNum;
        DocumentReference getWaitlist = db.collection("Courses")
                .document(courseID);

        getWaitlist.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> tempMapping = document.getData();


                    ArrayList<String> courseWaitlist = (ArrayList<String>) tempMapping.get("CourseQueue");

                    System.out.println(courseWaitlist);

                    String x = Integer.toString(courseWaitlist.indexOf(userID));
                    queuePositionNum.setText(x);
                }
            }
        });

        averageWaitTimeNum = binding.averageWaitTimeNum;





        //final TextView textView = binding.textInQueue;
        //InQueueModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}