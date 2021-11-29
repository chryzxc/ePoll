package com.example.epoll;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;


public class UserPollSelect extends AppCompatActivity {

    String electionId;
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    List<UserPollSelectList> myLists;
    RecyclerView rv;
    UserPollSelectAdapter adapter;


    Intent intent;
    Bundle bundle;
    ArrayList<String> questionList;
    ArrayList<String> choicesList;
    ArrayList<String> countsList;
    ArrayList<String> limitList;

    TextView textQuestion,textNumberPoll;
    Button buttonNextPoll;
    int x = 0;
    TextView pageNumber;

    LayoutInflater inflater;
    View myLayout;


    //   Map <String,Object> candidateList;
    //   List<DocumentSnapshot> positionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_poll_select);
        rv = (RecyclerView) findViewById(R.id.userpollselectrec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this, 1));
        myLists = new ArrayList<>();


        textQuestion = (TextView) findViewById(R.id.textQuestion);
        textNumberPoll  = (TextView) findViewById(R.id.textNumberPoll);

        mStore = FirebaseFirestore.getInstance();

        intent= getIntent();
        bundle = intent.getExtras();

        questionList = new ArrayList<>();
        choicesList = new ArrayList<>();
        countsList = new ArrayList<>();
        limitList = new ArrayList<>();

        buttonNextPoll  = (Button) findViewById(R.id.buttonNextPoll);
        pageNumber = findViewById(R.id.pageNumberPoll);

        inflater = getLayoutInflater();
        myLayout = inflater.inflate(R.layout.poll_finish, null);
        mAuth = FirebaseAuth.getInstance();



        buttonNextPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (x < questionList.size()){
                    countsList.clear();
                    choicesList.clear();
                    clearView();
                    getdata();
                }else{

                    if (UserProfile.userType.equals("Faculty") || UserProfile.userType.equals("Administrator")) {
                       finish();
                    }else{




                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(UserPollSelect.this);



                    Button buttonSend = (Button)myLayout.findViewById(R.id.pollFinish);
                    buttonSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mStore.collection("Users")
                                    .document(mAuth.getCurrentUser().getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            String[] values;
                                            ArrayList<String> pollsParticipated = new ArrayList<>();
                                            DocumentSnapshot document = task.getResult();

                                            String currentString = document.get("polls_participated").toString();
                                            values = String.valueOf(currentString).replace("[", "").replace("]", "").replace(" ","").split(",");
                                            for (int x = 0; x < values.length;x++){
                                                pollsParticipated.add(values[x]);
                                            }

                                            // List<String> list = new ArrayList<>();
                                            pollsParticipated.add(bundle.get("key").toString());


                                            mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("polls_participated",pollsParticipated).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {





                                                    mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot document = task.getResult();
                                                                Map<String, Object> logsList = new HashMap<>();
                                                                if (document.exists()) {
                                                                    Map<String, Object> logs = document.getData();
                                                                    for (Map.Entry<String, Object> entry : logs.entrySet()) {
                                                                        if (entry.getKey().equals("logs")) {
                                                                            Map<String, Object> list = (Map<String, Object>) entry.getValue();
                                                                            for (Map.Entry<String, Object> e : list.entrySet()) {
                                                                                logsList.put(e.getKey().toString(),e.getValue().toString());
                                                                            }
                                                                        }
                                                                    }

                                                                    logsList.put(new Date().toString(),"Answered a poll ("+bundle.get("title").toString()+")" );
                                                                    DocumentReference documentReference = mStore.collection("Users").document(mAuth.getCurrentUser().getUid());
                                                                    documentReference.update("logs",logsList);


                                                                } else {
                                                                    Log.d("TAG", "No such document");
                                                                }
                                                            } else {
                                                                Log.d("TAG", "get failed with ", task.getException());
                                                            }
                                                        }
                                                    });





                                            //        Map<String,Object> logs = new HashMap<>();
                                                   // long dateNow = new Date().getTime();
                                               //     logs.put(new Date().toString(),"Answered poll. ("+bundle.get("title").toString()+")" );
                                               //     DocumentReference documentReference = mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("others").document("logs");
                                               //     documentReference.set(logs);


                                                    Intent myIntent = new Intent(UserPollSelect.this, UserProfile.class);
                                                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    UserPollSelect.this.startActivity(myIntent);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                        }

                                    });


                        }
                    });


                    builder.setView(myLayout);
                    androidx.appcompat.app.AlertDialog dialog = builder.create();

                    dialog.setCancelable(false);
                    dialog.show();



                }
                }


            }
        });



        start();







    }
    public void start(){
        mStore.collection("Poll")
                .document(bundle.get("key").toString())
                .collection("limit").document("questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();

                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            //   int i = Integer.valueOf((String) entry.getValue());
                            // int i = (Integer) entry.getValue();
                            limitList.add(entry.getValue().toString());



                        }




                    }


                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mStore.collection("Poll")
                        .document(bundle.get("key").toString())
                        .collection("questions")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        questionList.add(document.getId());


                                    }

                                    getdata();

                                    pageNumber.setText("Page 1 of "+questionList.size());
                                    //  Toast.makeText(UserVoteSelect.this, positionList.toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    // Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                                //   getdata();
                            }
                        });

            }
        });


    }


    public void getChoices(String questionId,String limit){

      //  Toast.makeText(UserPollSelect.this, questionId, Toast.LENGTH_SHORT).show();

        mStore.collection("Poll")
                .document(bundle.get("key").toString())
                .collection("questions").document(questionId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();

                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {

                            choicesList.add(entry.getKey());
                            countsList.add(entry.getValue().toString());

                        }

                        for (int y = 0; y < choicesList.size(); y++) {
                 //           Integer.parseInt(limitList.get(y));
                            myLists.add(new UserPollSelectList(choicesList.get(y), Integer.valueOf(countsList.get(y)),bundle.get("key").toString(),questionId, Integer.valueOf(limit)));
                        }
                        //   Toast.makeText(UserVoteSelect.this, "test :" + limitList.get(x).toString(), Toast.LENGTH_SHORT).show();

                        displayView();

                    }

                });

    }





    public void clearView(){
        myLists.clear();
        adapter = new UserPollSelectAdapter(myLists, this);
        rv.setAdapter(adapter);
    }

    public void displayView(){



        adapter = new UserPollSelectAdapter(myLists, this);
        rv.setAdapter(adapter);

    }





    public void getdata() {
        int num = x+1;
        pageNumber.setText("Page "+ num+" of "+questionList.size());

        if (x < questionList.size()){
            textQuestion.setText(num+". "+questionList.get(x));
            textQuestion.setSelected(true);

            if (limitList.get(x).equals("1")){
                textNumberPoll.setVisibility(View.GONE);
               // Toast.makeText(this, questionList.get(x).toString()+"  :  "+limitList.get(x).toString(), Toast.LENGTH_SHORT).show();
                getChoices(questionList.get(x),limitList.get(x));
            }else{
                textNumberPoll.setVisibility(View.VISIBLE);
                textNumberPoll.setText("Select ("+limitList.get(x)+") on the list");
             //   Toast.makeText(this, questionList.get(x).toString()+"  :  "+limitList.get(x).toString(), Toast.LENGTH_SHORT).show();
                getChoices(questionList.get(x),limitList.get(x));

            }



        }

        if(x == questionList.size() - 1){
            if (UserProfile.userType.equals("Faculty") || UserProfile.userType.equals("Administrator")) {
                buttonNextPoll.setText("Exit");
            }else{
                buttonNextPoll.setText("Finish");
            }


        }
        x++;




    }
}
