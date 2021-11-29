package com.example.epoll;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Logs extends AppCompatActivity {
    List<LogsList> myLists;
    RecyclerView rv;
    LogsAdapter adapter;
    String userId;

    ArrayList<String> dateList;
    ArrayList<String> activityList;

    Map<Date, String> sortedLogs = new TreeMap<Date, String>(Collections.reverseOrder());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        rv=(RecyclerView)findViewById(R.id.reclogs);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,1));
        myLists=new ArrayList<>();

        dateList = new ArrayList<>();
        activityList = new ArrayList<>();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseFirestore mStore = FirebaseFirestore.getInstance();

        userId = mAuth.getCurrentUser().getUid();



        mStore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                  //  dateList.add(e.getKey());
                                  //  activityList.add(e.getValue().toString());


                                    String string_date = e.getKey();

                                    long l = 0;
                                    SimpleDateFormat f = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                        f = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZ yyyy");
                                    }
                                    try {
                                        Date d = null;
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            d = f.parse(string_date);
                                        }
                                        l = d.getTime();
                                    } catch (ParseException exception) {
                                        exception.printStackTrace();
                                    }
                                    Date convertedDate = new Date(l);
                                    sortedLogs.put(convertedDate,e.getValue().toString());


                                }
                            }
                        }



                        getdata();
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });





     /*

        mStore.collection("Users")
                .document(userId)
                .collection("others").document("logs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();

                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {

                            dateList.add(entry.getKey());
                            activityList.add(entry.getValue().toString());

                        }

                      getdata();

                    }

                });
                */


    }


    private void getdata() {

        Set set = sortedLogs.entrySet();
        Iterator i = set.iterator();

        while (i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();

            myLists.add(new LogsList(me.getKey().toString(),me.getValue().toString(),false));

        }


      //  for (Map.Entry<Date, String> entry : sortedLogs.entrySet()) {


      //  }






      //  for (int x = dateList.size() - 1; x >= 0; x--){
       //     Toast.makeText(this, String.valueOf(sortedLogs.get(x)), Toast.LENGTH_SHORT).show();

            //myLists.add(new LogsList(dateList.get(x),activityList.get(x),false));
         //   myLists.add(new LogsList(sortedLogs.get(x),sortedLogs.get(x),false));
//
      //  }

        adapter=new LogsAdapter(myLists,this);
        rv.setAdapter(adapter);

    }
}