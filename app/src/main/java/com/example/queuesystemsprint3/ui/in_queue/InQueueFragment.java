package com.example.queuesystemsprint3.ui.in_queue;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
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
import java.util.Objects;

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

    private String CHANNEL_ID = "1";

    private String notification_title = "CS 2050 Queue Notification";
    private String notification_content = userID + ", you are at the top of the queue for " + courseID;

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
        createNotificationChannel();

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

                    String x = Integer.toString(courseWaitlist.indexOf(userID) + 1);
                    queuePositionNum.setText(x);

                    if (courseWaitlist.indexOf(userID) == -1) {
                        createNotificationIntent();
                    }
                }
            }
        });

        //To Be Updated
        averageWaitTimeNum = binding.averageWaitTimeNum;

        //final TextView textView = binding.textInQueue;
        //InQueueModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }
    private void createNotificationIntent() {
        Intent intent = new Intent(this.getContext(), InQueueFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this.getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(notification_title)
                .setContentText(notification_content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("CS 2050 has an available TA. "))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.requireContext());

        // notificationId is a unique int for each notification that you must define
        if (ActivityCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
