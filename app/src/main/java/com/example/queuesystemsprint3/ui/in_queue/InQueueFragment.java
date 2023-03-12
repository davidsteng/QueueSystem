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
import androidx.core.app.NotificationCompat;
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
import java.util.Objects;

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

    private String CHANNEL_ID = "1";

    private String notification_title = "CS 2050 Queue Notification";
    private String notification_content = userID + ", you are at the top of the queue for " + courseID;

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
        createNotificationChannel();


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

                    if (courseWaitlist.indexOf(userID) == -1) {
                        createNotificationIntent();
                    }
                }
            }
        });

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}