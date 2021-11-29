package com.example.epoll;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminElection extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    static List<AdminElectionList> myLists;
    static RecyclerView rv;
    static AdminElectionAdapter adapter;

    EditText startVote,endVote,titleText,allowedText;
    Calendar startCalendar;
    Calendar endCalendar;

    CardView addText;
    static LayoutInflater inflater;
    static View myLayout;
    public Context ct;

    TextView selectAllowed;

    static final Context context1 = null;
    Spinner spinnerCourse;
    String str;
    static List<String> partyList;


    private static final int REQUEST_CODE_EXAMPLE = 0x9988;

    private String blockCharacterSet = "~#^|$%&*!.,";
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_election);

        rv = (RecyclerView) findViewById(R.id.adminelecrec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,1 ));
        myLists = new ArrayList<>();

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        titleText = findViewById(R.id.titleText);
        titleText.setFilters(new InputFilter[] { filter });
        allowedText = findViewById(R.id.allowedText);
        allowedText.setFilters(new InputFilter[] { filter });
        partyList = new ArrayList<>();
        partyList.add("Independent");




        ct = getApplicationContext();
        spinnerCourse = (Spinner) findViewById(R.id.spinnerCourse);
    //    ArrayAdapter<String> adapterS = new ArrayAdapter<String>(this,
     //           android.R.layout.simple_list_item_multiple_choice, arrayCourse);
      //  adapterS.setDropDownViewResource(android.R.layout.simple_list_item_multiple_choice);
    //    spinnerCourse.setAdapter(adapterS);

        TextView selectText = (TextView)findViewById(R.id.textSelect);
        selectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // spinnerCourse.performClick();
                selectCourse();
            }
        });


       spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               String text = allowedText.getText().toString();
               if (text.isEmpty()){
                   allowedText.setText(spinnerCourse.getSelectedItem().toString());
               }else if (text.equals("ALL")){
                   allowedText.setText(null);
                   allowedText.setText(spinnerCourse.getSelectedItem().toString());

               }else if (spinnerCourse.getSelectedItem().toString().equals("ALL")){
                   allowedText.setText(null);
                   allowedText.setText(spinnerCourse.getSelectedItem().toString());
               }else{
                   allowedText.setText(text+","+spinnerCourse.getSelectedItem().toString());
               }


           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });




        FloatingActionButton fabSendData = (FloatingActionButton) findViewById(R.id.fabSendData);
        fabSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleText.getText().toString().trim();
                String allowed = allowedText.getText().toString().trim();
                String voteS = startVote.getText().toString().trim();
                String voteE = endVote.getText().toString().trim();


                if (title.isEmpty()) {
                    titleText.setError("Election name cannot be empty");
                    titleText.requestFocus();
                    return;
                }
                if (voteS.isEmpty()) {
                    startVote.setError("Starting date is required");
                    startVote.requestFocus();
                    return;
                }
                if (voteE.isEmpty()) {
                    endVote.setError("End date is required");
                    endVote.requestFocus();

                    return;
                }
                if (myLists.size() == 1){
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Add some candidates", Snackbar.LENGTH_LONG)

                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();

                    return;

                }

                if (allowed.isEmpty()) {
                    allowedText.setText("ALL");

                }

                List<String> positionList = new ArrayList<>();

                for(int x = 0;x < myLists.size() -1;x++){
                  //  Toast.makeText(ct, myLists.get(x).getPosition(), Toast.LENGTH_SHORT).show();

                        //positionList.add(String.valueOf(x));


                    positionList.add(myLists.get(x).getPosition().toString());

                }
               // Toast.makeText(ct, String.valueOf(positionList), Toast.LENGTH_SHORT).show();

                String str = allowedText.getText().toString().trim();
                List<String> allowedList = Arrays.asList(str.split(","));


                Map<String, Object> election = new HashMap<>();
                election.put("allowed", allowedList);
                election.put("date_created", new Date());
                election.put("party", partyList);
                election.put("position",positionList);
                election.put("end", endCalendar.getTime());
                election.put("start", startCalendar.getTime());
                election.put("title", title);


               if(UserProfile.userType.matches("Faculty")){
                    election.put("approved", false);
                    election.put("creator_name", UserProfile.creatorName);
                    election.put("creator_id", UserProfile.creatorId);
                }else{
                    election.put("approved", true);
                    election.put("creator_name", UserProfile.creatorName);
                    election.put("creator_id", UserProfile.creatorId);
                }

                election.put("updates", 0);


                //

                DocumentReference documentReference = mStore.collection("Election").document();
                documentReference.set(election).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Map<String, Integer> insert = new HashMap<>();
                        for(int x = 0;x < myLists.size() -1;x++){



                            insert.put(myLists.get(x).getPosition(),myLists.get(x).getLimitFinal().get(myLists.get(x).getPosition()));

                        }




                        for(int x = 0;x < myLists.size() -1;x++){

                            mStore.collection("Election").document(documentReference.getId()).collection("party-list").document(myLists.get(x).getPosition()).set(myLists.get(x).getCandidatesParty());
                            mStore.collection("Election").document(documentReference.getId()).collection("details").document(myLists.get(x).getPosition()).set(myLists.get(x).getCandidates());
                            mStore.collection("Election").document(documentReference.getId()).collection("limit").document("position").set(insert);
                            mStore.collection("Election").document(documentReference.getId()).collection("tally").document(myLists.get(x).getPosition()).set(myLists.get(x).getTally());
                            mStore.collection("Election").document(documentReference.getId()).collection("total").document(myLists.get(x).getPosition()).set(myLists.get(x).getTotal());
                            mStore.collection("Election").document(documentReference.getId()).collection("profile").document(myLists.get(x).getPosition()).set(myLists.get(x).getCandidatesId());


                        }



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

                                        logsList.put(new Date().toString(),"You created an election " + title +".");
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








                  //      Map <String,Object> logs = new HashMap<>();
                      //  long dateNow = new Date().getTime();
                  //      logs.put(new Date().toString(),"You created an election " + title +".");
                  //      DocumentReference documentReference = mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("others").document("logs");
                  //      documentReference.update(logs);


                        if (UserProfile.userType.equals("Administrator")){

                            Intent myIntent = new Intent(AdminElection.this, UserVote.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            AdminElection.this.startActivity(myIntent);
//here
                        }else if (UserProfile.userType.equals("Faculty")){
                            Intent myIntent = new Intent(AdminElection.this, AdminApproval.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            AdminElection.this.startActivity(myIntent);
                        }

                        //   Snackbar.make(UserVote.parentLayout, "success", Snackbar.LENGTH_LONG).show();

                        // Toast.makeText(Registration.this, "Success", Toast.LENGTH_SHORT).show();
                        //  progressBar.setVisibility(View.GONE);
                        //  onBackPressed();


                    }


                });
            }
        });


                inflater = getLayoutInflater();
                // myLayout = inflater.inflate(R.layout.candidates_list, null);
                myLayout = LayoutInflater.from(ct).inflate(R.layout.candidates_list, null);

                startCalendar = Calendar.getInstance();
                endCalendar = Calendar.getInstance();

                startVote = (EditText) findViewById(R.id.startVote);
                DatePickerDialog.OnDateSetListener startdate = new DatePickerDialog.OnDateSetListener() {

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        startCalendar.set(Calendar.YEAR, year);
                        startCalendar.set(Calendar.MONTH, monthOfYear);
                        startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateStartVote();
                    }

                };

                startVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(AdminElection.this, startdate, startCalendar
                                .get(Calendar.YEAR), startCalendar.get(Calendar.MONTH),
                                startCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                endVote = (EditText) findViewById(R.id.endVote);
                DatePickerDialog.OnDateSetListener enddate = new DatePickerDialog.OnDateSetListener() {

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        endCalendar.set(Calendar.YEAR, year);
                        endCalendar.set(Calendar.MONTH, monthOfYear);
                        endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateEndVote();
                    }

                };

                endVote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(AdminElection.this, enddate, endCalendar
                                .get(Calendar.YEAR), endCalendar.get(Calendar.MONTH),
                                endCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });


                myLists.clear();
                myLists.add(new AdminElectionList(null, null, null,null,null,null,null,null ));
                adapter = new AdminElectionAdapter(myLists, this);
                rv.setAdapter(adapter);


                // candidatesAdd();





            }

/*
            public void candidatesAdd(Context context) {
                //inflater = getLayoutInflater();
                myLayout = LayoutInflater.from(context).inflate(R.layout.candidates_list, null);
                String[] electionType = new String[] {
                        "Non Party-list","Party-list"};

                ArrayList<String> candidateArray = new ArrayList<>();

                ListView candidatesList = (ListView) myLayout.findViewById(R.id.testlist);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1,
                        candidateArray);

                TextView candidateNameText = (TextView) myLayout.findViewById(R.id.candidateNameText);
                TextView candidateCourseText = (TextView) myLayout.findViewById(R.id.candidateCourseText);
                TextView candidateIdText = (TextView) myLayout.findViewById(R.id.candidateIdText);
                TextView positionText = (TextView) myLayout.findViewById(R.id.positionText);
                TextView limitText = (TextView) myLayout.findViewById(R.id.limitText);

                limitText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                limitText.setKeyListener(DigitsKeyListener.getInstance("123456789"));


                Map<String, String> candidateData = new HashMap<>();
                Map<String, String> candidateProfile = new HashMap<>();

                Map<String, Integer> limitFinal = new HashMap<>();
                Map<String, Integer> tally = new HashMap<>();
                Map<String, Integer> total = new HashMap<>();


                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);

                TextView insertText = (TextView) myLayout.findViewById(R.id.insertText);
                insertText.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {
                        int i = 0;

                        String candidateName = candidateNameText.getText().toString().trim();
                        String candidateCourse = candidateCourseText.getText().toString().trim();
                        String candidateId = candidateIdText.getText().toString().trim();

                        if (candidateName.isEmpty()) {
                            candidateNameText.setError("Candidate is required");
                            candidateNameText.requestFocus();
                            return;
                        }

                        if (candidateCourse.isEmpty()) {
                            candidateCourseText.setError("Details cannot be empty");
                            candidateCourseText.requestFocus();
                            return;
                        }

                        if (candidateId.isEmpty()){
                            candidateProfile.put(candidateNameText.getText().toString(), null);
                        }else{
                            candidateProfile.put(candidateNameText.getText().toString(), candidateIdText.getText().toString());
                        }

                        candidateProfile.put(candidateNameText.getText().toString(), candidateIdText.getText().toString());


                        candidateData.put(candidateNameText.getText().toString(), candidateCourseText.getText().toString());
                        tally.put(candidateNameText.getText().toString(), 0);




                        Object firstKey = candidateData.keySet().toArray()[i];
                        Object valueForFirstKey = candidateData.get(firstKey);


                        //  for (Map.Entry<String, Object> entry : candidateData.entrySet()) {
                        //       if (entry.getValue().equals(valueForFirstKey)){
                        //       //    Toast.makeText(context,valueForFirstKey.toString() , Toast.LENGTH_SHORT).show();
                        //           candidateArray.add(entry.getKey().toString() + " - " +valueForFirstKey.toString());
                        //          Toast.makeText(context, candidateData.toString(), Toast.LENGTH_SHORT).show();

                        //       }
                        //    }

                        candidateArray.add(candidateNameText.getText().toString() + " - " + candidateCourseText.getText());


                        candidatesList.setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();

                        i++;

                        candidateNameText.setText(null);
                        candidateCourseText.setText(null);
                        candidateIdText.setText(null);

                    }

                });


                builder.setView(myLayout).setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                 builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                    //     dialog.cancel();
                        // finish();
                     }
                 });
                androidx.appcompat.app.AlertDialog dialog = builder.create();

                dialog.setCancelable(false);
                dialog.show();


                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String position = positionText.getText().toString().trim();
                        String limit = limitText.getText().toString().trim();

                        String candidateName = candidateNameText.getText().toString().trim();
                        String candidateCourse = candidateCourseText.getText().toString().trim();

                        if (position.isEmpty()) {
                            positionText.setError("Position is required");
                            positionText.requestFocus();
                            return;
                        }

                        if (limit.isEmpty()) {
                            limitText.setError("Limit cannot be empty");
                            limitText.requestFocus();
                            return;
                        }



                        if (candidateArray.size() == 0){
                            candidateNameText.setError("You need to add some candidates");
                            candidateNameText.requestFocus();
                            return;
                        }

                        if (candidateCourse.isEmpty()) {
                            candidateNameText.setText(null);


                        }
                        limitFinal.put(positionText.getText().toString(), Integer.valueOf(limit));
                        total.put("total_votes", 0);


                        int listSize = myLists.size() - 1;
                        myLists.remove(listSize);
                        //  myLists.clear();
                        myLists.add(new AdminElectionList(positionText.getText().toString(), Integer.parseInt(limitText.getText().toString()), candidateData,candidateProfile,limitFinal,tally,total));

                        myLists.add(new AdminElectionList(null, null, null,null,null,null,null ));
                        adapter = new AdminElectionAdapter(myLists, context);
                        rv.setAdapter(adapter);
                        dialog.dismiss();


                    }
                });

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();


                    }
                });


            }
            */


            static void delete(int pos) {

                myLists.remove(pos);
                //  myLists.clear();

                adapter = new AdminElectionAdapter(myLists, context1);
                rv.setAdapter(adapter);

            }


            @RequiresApi(api = Build.VERSION_CODES.N)
            private void updateStartVote() {
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                TextView startError = (TextView) findViewById(R.id.startError);

              //  if (startCalendar.getTime().before(new Date())) {

               //     startError.setText("Date must be greater than today's date");
              //  } else {
                    startVote.setText(sdf.format(startCalendar.getTime()));
                    startVote.setError(null);
                    startError.setText("");
              //  }
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            private void updateEndVote() {
                String myFormat = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                TextView endError = (TextView) findViewById(R.id.endError);

                if (endCalendar.getTime().after(new Date())) {
                    if (endCalendar.getTime().after(startCalendar.getTime())) {
                        endVote.setText(sdf.format(endCalendar.getTime()));
                        endVote.setError(null);
                        endError.setText("");
                    } else {
                        endError.setText("Date must be greater than starting date");
                    }


                } else {

                    endError.setText("Date must be greater than today's date");
                }

                //endVote.setText(sdf.format(myCalendar.getTime()));
            }



            public void selectCourse(){

                String[] arrayCourse = new String[] {
                        "ALL",
                        "BSAr",
                        "BSID",
                        "BSEcon",
                        "BAEL",
                        "BSMath",
                        "BSStat",
                        "BSChem",
                        "BSES",
                        "BSA",
                        "BSE",
                        "BSM",
                        "BSOA",
                        "BEED",
                        "BSEd",
                        "BCAEd",
                        "BPEd",
                        "DTS",
                        "BTVTEd",
                        "BTLEd",
                        "BSChE",
                        "BSCE",
                        "BSEE",
                        "BSECE",
                        "BSGE",
                        "BSME",
                        "BSIE",
                        "BSIT",
                        "BIT",
                        "BMT",
                        "BSHRT",
                        "BSHM",
                        "BSND",
                        "BSInT",
                        "BSMT"
                };

                String[] department = new String[] {
                        "",
                        "College of Architecture and Allied Discipline",
                        "College of Architecture and Allied Discipline",
                        "College of Arts and Sciences",
                        "College of Arts and Sciences",
                        "College of Arts and Sciences",
                        "College of Arts and Sciences",
                        "College of Arts and Sciences",
                        "College of Arts and Sciences",
                        "College of Business and Entrepreneurship",
                        "College of Business and Entrepreneurship",
                        "College of Business and Entrepreneurship",
                        "College of Business and Entrepreneurship",
                        "College of Education",
                        "College of Education",
                        "College of Education",
                        "College of Education",
                        "College of Education",
                        "College of Education",
                        "College of Education",
                        "College of Engineering",
                        "College of Engineering",
                        "College of Engineering",
                        "College of Engineering",
                        "College of Engineering",
                        "College of Engineering",
                        "College of Engineering",
                        "College of Engineering",
                        "College of Technology",
                        "College of Technology",
                        "College of Technology",
                        "College of Technology",
                        "College of Technology",
                        "College of Technology",
                        "College of Technology"
                };


               myLayout = LayoutInflater.from(this).inflate(R.layout.courses, null);

            //   ListView listView = (ListView)myLayout.findViewById(R.id.selectCourse);

            //    CourseList adapter = new
            //            CourseList( arrayCourse, department,false);



           //     listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
           //     listView.setItemChecked(2, true);



                 List<CourseList> myLists1;
                 RecyclerView rv1;
                 CourseAdapter adapter1;


                rv1 = (RecyclerView) myLayout.findViewById(R.id.selectCourse);
                rv1.setHasFixedSize(true);
                rv1.setLayoutManager(new GridLayoutManager(this,1 ));
                myLists1 = new ArrayList<>();
                for (int x = 0; x < arrayCourse.length;x++){
                    myLists1.add(new CourseList(arrayCourse[x],department[x],false ));
                }



                adapter1 = new CourseAdapter(myLists1, this);
                rv1.setAdapter(adapter1);

            //    listView.setAdapter(adapter);
             //   listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice));
                //listView.setOnItemClickListener(this);



               // listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, arrayCourse));

               /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        SparseBooleanArray sp = listView.getCheckedItemPositions();
                       // Toast.makeText(ct,sp.toString(), Toast.LENGTH_SHORT).show();
                       str = "";
                        for(int i=0;i<sp.size();i++)
                        {

                            if (sp.valueAt(i) == true) {

                                str+=arrayCourse[sp.keyAt(i)]+",";
                               // Toast.makeText(ct, arrayCourse[sp.keyAt(i)], Toast.LENGTH_SHORT).show();

                            }


                       //     if (sp.get(i) == true){
                               // str+=arrayCourse[sp.keyAt(i)]+",";
                          //      Toast.makeText(ct, arrayCourse[sp.keyAt(i)], Toast.LENGTH_SHORT).show();
                           //     Toast.makeText(ct,sp.toString(), Toast.LENGTH_SHORT).show();

                         //   }


                            //Toast.makeText(ct, sp.toString(), Toast.LENGTH_SHORT).show();
                           // Toast.makeText(ct, ""+str, Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(ct, str, Toast.LENGTH_SHORT).show();



                    }
                });
*/

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);


                builder.setView(myLayout).setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //     dialog.cancel();
                        // finish();
                    }
                });
                androidx.appcompat.app.AlertDialog dialog = builder.create();

                dialog.setCancelable(false);
                dialog.show();


                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str="";
                       for (int y = 0; y < myLists1.size(); y ++){
                           if (myLists1.get(y).getIsChecked() == true){
                               str+=myLists1.get(y).getCourse()+",";
                              // Toast.makeText(ct,  myLists1.get(y).getCourse(), Toast.LENGTH_SHORT).show();

                           }
                       }
                       allowedText.setText(str);
                       dialog.dismiss();

                    }
                });

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();


                    }
                });


            }

    public void addCandid(String position, Integer limit,HashMap<String, String> candidates,HashMap<String, String> candidatesId,HashMap<String, String> candidatesParty,HashMap<String, Integer> limitFinal,HashMap<String, Integer> tally,HashMap<String, Integer> total){



        int listSize = myLists.size() - 1;

        myLists.remove(listSize);



        myLists.add(new AdminElectionList(position,limit,candidates,candidatesId,candidatesParty,limitFinal,tally,total));
        myLists.add(new AdminElectionList(null,null,null,null,null,null,null,null));


        adapter = new AdminElectionAdapter(myLists, this);
        rv.setAdapter(adapter);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE_EXAMPLE) {


            if (resultCode == Activity.RESULT_OK) {

                String position = data.getStringExtra("position");
                Integer limit = data.getIntExtra("limit",0);
                ArrayList<String> partylist = data.getStringArrayListExtra("partylist");

                HashMap<String, String> candidateData = (HashMap<String, String>) data.getSerializableExtra("candidateData");
                HashMap<String, String> candidateProfile = (HashMap<String, String>) data.getSerializableExtra("candidateProfile");
                HashMap<String, String> candidateParty = (HashMap<String, String>) data.getSerializableExtra("candidateParty");
                HashMap<String, Integer> limitFinal = (HashMap<String, Integer>) data.getSerializableExtra("limitFinal");

                HashMap<String, Integer> tally = (HashMap<String, Integer>) data.getSerializableExtra("tally");
                HashMap<String, Integer> total = (HashMap<String, Integer>) data.getSerializableExtra("total");


                //start

                addCandid(position,limit,candidateData,candidateProfile,candidateParty,limitFinal,tally,total);



            } else {

            }
        }
    }


    public void startAnother(Context context){

        Intent intent = new Intent(context, CreateElection.class);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_EXAMPLE);



    }


}