package com.example.epoll;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epoll.R;
import com.example.epoll.UserProfile;
import com.example.epoll.UserVoteSelectAdapter;
import com.example.epoll.UserVoteSelectList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;


import org.w3c.dom.Element;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
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


public class LiveUpdatesSelected extends AppCompatActivity {

    String electionId;
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    List<LiveUpdatesList> myLists;
    RecyclerView rv;
    LiveUpdatesAdapter adapter;
    ConstraintLayout generateReport;

    Intent intent;
    Bundle bundle;
    ArrayList<String> positionList;
    ArrayList<String> candidateList;
    ArrayList<String> voteList;


    TextView textPosition,textNumber;
    Button buttonNext;
    int x;

    Calendar calendar;
    SimpleDateFormat simpledateformat;
    String Date;

    static TextView timeText,electionStatus;
    static CardView updatedPop;
    String docu;
    int t = 0;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    Context context;
    String electionName;





    //   Map <String,Object> candidateList;
    //   List<DocumentSnapshot> positionList;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_updates_selected);
        rv = (RecyclerView) findViewById(R.id.updatesrec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this, 1));
        myLists = new ArrayList<>();
        timeText = (TextView) findViewById(R.id.timeText);
        updatedPop = (CardView) findViewById(R.id.updatedPop);
        electionStatus= (TextView)findViewById(R.id.electionStatus);
        generateReport = (ConstraintLayout) findViewById(R.id.generateReport);
        CardView generateCard = (CardView) findViewById(R.id.generateCard);

        intent= getIntent();
        bundle = intent.getExtras();

        View parentLayout = findViewById(android.R.id.content);


        electionStatus.setText(bundle.get("election_status").toString());
      //  if (bundle.get("election_status").toString().matches("CLOSED")){

            generateCard.setVisibility(View.VISIBLE);
     //   }else{
       //     generateCard.setVisibility(View.GONE);
     //   }


        int permissionWriteExternal = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int permissionReadExternal = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        generateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               // ActivityCompat.requestPermissions(LiveUpdatesSelected.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

                if (permissionWriteExternal == PackageManager.PERMISSION_GRANTED && permissionReadExternal == PackageManager.PERMISSION_GRANTED) {
                    String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                    File file = new File(pdfPath +"/eVPOLL.pdf");

                    if (file.exists())
                        file.delete();
                    try {

                        generatePDF();

                        Snackbar.make(parentLayout, "File has been successfully generated"+ System.getProperty("line.separator") + pdfPath, Snackbar.LENGTH_LONG)
                                .setAction("OPEN", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String sPath = Environment.getExternalStorageDirectory() + "/"  + "/";
                                        Uri uri = Uri.parse(pdfPath);

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri,"*/*");
                                        startActivity(intent);
                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Failed " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }

                }else{

                    ActivityCompat.requestPermissions(LiveUpdatesSelected.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                }


      //          try {
     //               generatePDF();
     //               Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
     //           } catch (FileNotFoundException e) {
      //              Toast.makeText(context, "failed " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
      //              e.printStackTrace();
      //          }


            }
        });


        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);

        textNumber  = (TextView) findViewById(R.id.textNumber);

        mStore = FirebaseFirestore.getInstance();



        positionList = new ArrayList<>();
        candidateList = new ArrayList<>();
        voteList = new ArrayList<>();


        getTally();

/*
        mStore.collection("Election")
                .document(bundle.get("key").toString())
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                        getTally();


                    }
                });
*/



     //   getTally();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getTally();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                    }
                }, 1000);
            }
        });




    }


/*
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void createPDF(){

        PdfDocument myPdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(300,600,1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);

        Paint myPaint = new Paint();
        String myString = "test";
        int x = 10, y=25;

        for (String line:myString.split("\n")){
            myPage.getCanvas().drawText(line, x, y, myPaint);
            y+=myPaint.descent()-myPaint.ascent();
        }

        myPdfDocument.finishPage(myPage);

        String myFilePath = Environment.getExternalStorageDirectory().getPath() + "/myPDFFile.pdf";
        File myFile = new File(myFilePath);

   //     String myFilePath = Environment.getDataDirectory().getPath() + "/myPDFFile.pdf";
    //    File myFile = new File(myFilePath);

    //    String myFilePath = Environment.getDataDirectory().getPath() + "/myPDFFile.pdf";
     //   File myFile = new File(context.getFilesDir(),"myPDFFile.pdf");
        try {
            myPdfDocument.writeTo(new FileOutputStream(myFile));
            Toast.makeText(this, "success " + myFile, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
          //  myEditText.setText("“"ERROR”);
        }



        myPdfDocument.close();
    }

    */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void generatePDF() throws FileNotFoundException {

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath +"/eVPOLL.pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(String.valueOf(file));

        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new com.itextpdf.layout.Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A6);
        document.setMargins(0,0,0,0);

        Drawable d = getDrawable(R.drawable.badge);
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] bitmapData = stream.toByteArray();

        ImageData imageData= ImageDataFactory.create(bitmapData);
        Image image = new Image(imageData).setHeight(30).setWidth(30).setHorizontalAlignment(HorizontalAlignment.CENTER).setMarginTop(10);


        Paragraph title = new Paragraph(electionName).setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER);
        Paragraph string1 = new Paragraph("Eastern Visayas State University").setFontSize(12).setTextAlignment(TextAlignment.CENTER).setMarginTop(-5);
        Paragraph string2 = new Paragraph("Main campus").setFontSize(10).setTextAlignment(TextAlignment.CENTER).setMarginTop(-8).setMarginBottom(8);

        float[] width = {100f,100f};
        Table table = new Table(width);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
/*
        table.addCell(new Cell().add(new Paragraph("")));
        table.addCell(new Cell().add(new Paragraph("")));

        table.addCell(new Cell().add(new Paragraph("")));
        table.addCell(new Cell().add(new Paragraph("")));

        table.addCell(new Cell().add(new Paragraph("")));
        table.addCell(new Cell().add(new Paragraph("")));
*/
        document.add(image);
        document.add(title);
        document.add(string1);
        document.add(string2);
        electionStatus.setText(bundle.get("election_status").toString());
        if (bundle.get("election_status").toString().matches("CLOSED")){

            document.add(new Paragraph("CLOSED").setFontSize(10).setTextAlignment(TextAlignment.CENTER).setMarginTop(-8).setFontColor(Color.RED));
            document.add(new Paragraph("As of " + DateFormat.format("MMM dd yyyy hh:mm aa",new Date().getTime())).setFontSize(8).setTextAlignment(TextAlignment.CENTER).setMarginTop(-5).setMarginBottom(8).setFontColor(Color.RED));

        }else{
            document.add(new Paragraph("ONGOING").setFontSize(8).setTextAlignment(TextAlignment.CENTER).setMarginTop(-8).setFontColor(Color.RED));
            document.add(new Paragraph("As of " + DateFormat.format("MMM dd yyyy hh:mm aa",new Date().getTime())).setFontSize(8).setTextAlignment(TextAlignment.CENTER).setMarginTop(-5).setMarginBottom(8).setFontColor(Color.RED));

        }



        for (int x = 0;x<myLists.size();x++){
            if (myLists.get(x).getName() == null){
                document.add(new Paragraph(myLists.get(x).getPosition()).setFontSize(10).setTextAlignment(TextAlignment.LEFT).setMarginLeft(20).setMarginBottom(10).setBold());
            }else{
                document.add(new Paragraph(myLists.get(x).getName() +"  -   " +myLists.get(x).getVotes()+ " votes").setFontSize(10).setTextAlignment(TextAlignment.LEFT).setMarginLeft(25).setMarginTop(-5));
            }
        }


        document.close();


    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getTally(){
        positionList.clear();
        candidateList.clear();
        voteList.clear();
        myLists.clear();
        adapter = new LiveUpdatesAdapter(myLists, LiveUpdatesSelected.this);
        rv.setAdapter(adapter);

        calendar = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("MM/dd/yyy hh:mm:ss");
        Date = simpledateformat.format(calendar.getTime());

        timeText.setText("Synced on: "+Date);





        mStore.collection("Election")
                .document(bundle.get("key").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    String[] values;
                    electionName = task.getResult().get("title").toString();
                    values = String.valueOf(task.getResult().get("position").toString()).replace("[", "").replace("]", "").replace("","").split(", ");
                    for (int x = 0; x < values.length;x++){

                        positionList.add(values[x]);
                    }
                    for (x = 0; x < positionList.size(); x++){

                        mStore.collection("Election")
                                .document(bundle.get("key").toString())
                                .collection("tally").document(positionList.get(x))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if(task.isSuccessful()){


                                            DocumentSnapshot document = task.getResult();

                                            candidateList.clear();
                                            voteList.clear();


                                            Map<String, Object> map = document.getData();
                                            for (Map.Entry<String, Object> entry : map.entrySet()) {


                                                candidateList.add(entry.getKey());
                                                voteList.add(entry.getValue().toString());

                                            }
                                            //    Toast.makeText(LiveUpdatesSelected.this, document.getId(), Toast.LENGTH_SHORT).show();

                                            myLists.add(new LiveUpdatesList(null, bundle.get("key").toString(),null, document.getId()));

                                            for (int y = 0; y < candidateList.size(); y++) {
                                                myLists.add(new LiveUpdatesList(candidateList.get(y), bundle.get("key").toString(),voteList.get(y),document.getId()));

                                            }


                                            displayView();


                                        }


                                    }


                                });

                    }

                }




            }
        });
    }

/*
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                positionList.add(document.getId());
                                Toast.makeText(context, positionList.toString(), Toast.LENGTH_SHORT).show();

                            }

                            for (x = 0; x < positionList.size(); x++){

                                mStore.collection("Election")
                                        .document(bundle.get("key").toString())
                                        .collection("tally").document(positionList.get(x))   //< ------ President
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if(task.isSuccessful()){


                                                    DocumentSnapshot document = task.getResult();

                                                    candidateList.clear();
                                                    voteList.clear();


                                                    Map<String, Object> map = document.getData();
                                                    for (Map.Entry<String, Object> entry : map.entrySet()) {


                                                        candidateList.add(entry.getKey());
                                                        voteList.add(entry.getValue().toString());

                                                    }
                                                //    Toast.makeText(LiveUpdatesSelected.this, document.getId(), Toast.LENGTH_SHORT).show();

                                                    myLists.add(new LiveUpdatesList(null, bundle.get("key").toString(),null, document.getId()));

                                                    for (int y = 0; y < candidateList.size(); y++) {
                                                        myLists.add(new LiveUpdatesList(candidateList.get(y), bundle.get("key").toString(),voteList.get(y),document.getId()));

                                                    }

                                                    displayView();


                                                }


                                            }


                                        });

                            }


                        }

                    }
                });
*/


    public void displayView(){
        //Toast.makeText(LiveUpdatesSelected.this, "test", Toast.LENGTH_SHORT).show();
        adapter = new LiveUpdatesAdapter(myLists, this);
        rv.setAdapter(adapter);

      }


    }


