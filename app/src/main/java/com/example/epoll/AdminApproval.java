package com.example.epoll;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminApproval extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    static List<AdminApprovalList> myLists;
    static RecyclerView rv;
    static AdminApprovalAdapter adapter;

    static LayoutInflater inflater;
    static View myLayout;

    static View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_approval);


        rv = (RecyclerView) findViewById(R.id.adminapprovalrec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,1 ));
        myLists = new ArrayList<>();

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        parentLayout = findViewById(android.R.id.content);

        if (UserProfile.userType.equals("Administrator")){

            fetchDataApproval();
        }else   if (UserProfile.userType.equals("Faculty")){

            fetchDataPending();
        }






    }



    public void fetchDataApproval(){


        mStore.collection("Election")
                .whereEqualTo("approved", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                myLists.add(new AdminApprovalList(document.getString("title"), document.getDate("date_created"),document.getId(),"Election",document.getString("creator_name") ));

                            }


                        } else {

                        }
                        mStore.collection("Poll")
                                .whereEqualTo("approved", false)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {


                                                myLists.add(new AdminApprovalList(document.getString("title"), document.getDate("date_created"),document.getId(),"Poll",document.getString("creator_name") ));

                                            }


                                        } else {

                                        }
                                        displayData();
                                    }
                                });
                    }
                });





    }

    public void fetchDataPending(){


        mStore.collection("Election")
                .whereEqualTo("approved", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (UserProfile.creatorId.matches(document.getString("creator_id"))){
                                    myLists.add(new AdminApprovalList(document.getString("title"), document.getDate("date_created"),document.getId(),"Election",document.getString("creator_name") ));
                                }


                            }


                        } else {

                        }
                        mStore.collection("Poll")
                                .whereEqualTo("approved", false)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                if (UserProfile.creatorId.matches(document.getString("creator_id"))){
                                                    myLists.add(new AdminApprovalList(document.getString("title"), document.getDate("date_created"),document.getId(),"Poll",document.getString("creator_name") ));
                                                }


                                            }


                                        } else {

                                        }
                                        displayData();
                                    }
                                });
                    }
                });





    }

    public void displayData(){
        adapter = new AdminApprovalAdapter(myLists, this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        Intent myIntent = new Intent(AdminApproval.this, UserProfile.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        AdminApproval.this.startActivity(myIntent);
        overridePendingTransition(R.anim.slide_out_right, R.anim.no_animation);
    }
}