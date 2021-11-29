package com.example.epoll;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class UserPoll extends AppCompatActivity {

    String electionId;
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    static List<UserPollList> myLists;
    static RecyclerView rv;
    static UserPollAdapter adapter;

    List<String> documentlist;
    ArrayList<String> pollsParticipated;
    ArrayList<String> allowed;
    TextView collectionText;

    Intent intent;
    Bundle bundle;

    static String type;

    String[] values;

    Boolean isUpdated;
    static  View parentLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_poll);
        rv = (RecyclerView) findViewById(R.id.userpollrec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,1 ));
        myLists = new ArrayList<>();

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        parentLayout = findViewById(android.R.id.content);
        intent= getIntent();
        bundle = intent.getExtras();

        documentlist = new ArrayList<>();
        pollsParticipated = new ArrayList<>();
        allowed = new ArrayList<>();
        CardView pollCard = (CardView) findViewById(R.id.pollCard);

        ImageButton pollAdd = (ImageButton) findViewById(R.id.pollAdd);

        isUpdated = false;

        if (UserProfile.userType.matches("Student")){



        }else if (UserProfile.userType.matches("Faculty"))

        {
            if (UserProfile.category.matches("create_poll")){
                pollCard.setVisibility(View.VISIBLE);
                pollAdd.setVisibility(View.VISIBLE);
            }

        }else if (UserProfile.userType.matches("Administrator"))
        {
            if (UserProfile.category.matches("create_poll")){
                pollCard.setVisibility(View.VISIBLE);
                pollAdd.setVisibility(View.VISIBLE);
            }


        }

        pollAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(UserPoll.this, AdminPoll.class);
                UserPoll.this.startActivity(myIntent);

            }

        });


        if (UserProfile.category.matches("poll")) {
          //  vote_image.setBackgroundResource(R.drawable.counts);
          //  typeText.setText("Poll");
        }
        if (UserProfile.category.matches("conduct_poll")){
          //  vote_image.setBackgroundResource(R.drawable.counts);
          //  typeText.setText("Conduct a poll");
        }




        clearData();
        start();




    }

    public void start(){

        getPollsParticipated();
        mStore.collection("Poll").whereEqualTo("approved", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        documentlist.add(document.getId());

                        getSpecificDocument(document.getId());

                    }

                }

                if (task.isComplete()){
                    new CountDownTimer(5000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }
                        @Override
                        public void onFinish() {
                            isUpdated = true;
                        }
                    }.start();

                }

            }
        });
    }


    public void clearData(){
        pollsParticipated.clear();
        documentlist.clear();
        myLists.clear();
        adapter = new UserPollAdapter(myLists, this);
        rv.setAdapter(adapter);
    }


    public void getPollsParticipated(){

        String userId = mAuth.getCurrentUser().getUid();
        mStore.collection("Users")
                .document(userId)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        String currentString = value.get("polls_participated").toString();
                        values = String.valueOf(currentString).replace("[", "").replace("]", "").replace(" ","").split(",");
                        for (int x = 0; x < values.length;x++){
                            pollsParticipated.add(values[x]);
                        }
                    }
                });
    }


    public void getSpecificDocument(String docuID){
        DocumentReference documentReference = mStore.collection("Poll").document(docuID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {


            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(isUpdated == true){

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }else{

                    if (value.exists()){

                        Timestamp timestamp = (Timestamp) value.getTimestamp("date_created");
                        Date date_created = timestamp.toDate();



                        myLists.add(new UserPollList(value.getString("title"),date_created,docuID,value.getString("description") ,pollsParticipated,value.get("allowed").toString(),value.getString("creator_id"),value.getString("creator_name")));
                        getdata();
                    }

                }

            }

        });

    }

    public void getdata() {
        adapter = new UserPollAdapter(myLists, this);
        rv.setAdapter(adapter);



    }
    @Override
    public void onBackPressed()
    {
        Intent myIntent = new Intent(UserPoll.this, UserProfile.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // overridePendingTransition(R.anim.fadeout, R.anim.fadein);
        UserPoll.this.startActivity(myIntent);
        overridePendingTransition(R.anim.slide_out_right, R.anim.no_animation);
    }


}