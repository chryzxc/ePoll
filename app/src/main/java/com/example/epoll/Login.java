package com.example.epoll;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {
    ConstraintLayout loginLayout,resetLayout;
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    ProgressBar progressBar2;
    public EditText idBox,passwordBox;
    String userId;

    ImageView evsuView,splashView;
    TextView splashText;
    ConstraintLayout loginForm;
    private static int SPLASH_TIME_OUT = 3000;

    LayoutInflater inflater;
    View myLayout;
    androidx.appcompat.app.AlertDialog dialog;
    ImageView tipsImage;



    Boolean isAdmin = false;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView userSignup = (TextView) findViewById(R.id.userSignup);
        TextView forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        TextView resetBack = (TextView) findViewById(R.id.resetBack);
        EditText emailReset = (EditText) findViewById(R.id.emailReset);

        Button resetButton = (Button) findViewById(R.id.resetButton);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        tipsImage = findViewById(R.id.tipsImage);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("docuPhotoPath", null);
        editor.putString("selfiePhotoPath", null);
        editor.apply();



/*
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.select) //set icon for notification
                        .setContentTitle("Notifications Example") //set title of notification
                        .setContentText("This is a notification message")//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        Intent notificationIntent = new Intent(this, NotificationView.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("message", "This is a notification message");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

*/




        progressBar2 = findViewById(R.id.progressBar2);

        idBox = (EditText) findViewById(R.id.userIDNumber);
        idBox.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        idBox.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
        passwordBox = (EditText) findViewById(R.id.userPassword);

        loginLayout = findViewById(R.id.loginLayout);
        resetLayout = findViewById(R.id.resetLayout);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        loginForm = (ConstraintLayout)findViewById(R.id.loginForm);
        splashView=(ImageView)findViewById(R.id.splashView);
        splashText = (TextView) findViewById(R.id.splashText);
        evsuView = (ImageView) findViewById(R.id.evsuView);

        inflater = getLayoutInflater();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        RotateAnimation anim = new RotateAnimation(0f, 350f, 80f, 15f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(1);
        anim.setDuration(700);

        Animation animation= AnimationUtils.loadAnimation(this,R.anim.myanimation);


        Animation animation1= AnimationUtils.loadAnimation(this,R.anim.fade);
        splashText.startAnimation(animation1);
        splashView.startAnimation(animation);








        userSignup.setPaintFlags(userSignup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        userSignup.setText("Sign up");

       userSignup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent myIntent = new Intent(Login.this, Registration.class);
           //    myIntent.putExtra("key", value); //Optional parameters
               Login.this.startActivity(myIntent);

           }
       });

        resetBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {


               loginLayout.setVisibility(View.VISIBLE);
               resetLayout.setVisibility(View.GONE);
           }
       });

       forgotPassword.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {



                loginLayout.setVisibility(View.GONE);
                resetLayout.setVisibility(View.VISIBLE);
           }
       });

       resetButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String email = emailReset.getText().toString().trim();

               if(email.isEmpty()){
                   emailReset.setError("Email is required");
                   emailReset.requestFocus();
                   return;
               }

               if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                   emailReset.setError("Invalid email");
                   emailReset.requestFocus();
                   return;
               }

               mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       Toast.makeText(Login.this, "Password reset link was sent to your email", Toast.LENGTH_SHORT).show();
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(Login.this, "Failed. Password reset link was not sent: " + e.getMessage().toString(),  Toast.LENGTH_SHORT).show();
                   }
               });
           }
       });

       tipsImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showTips();
           }
       });

       loginButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String id = idBox.getText().toString().trim();
               String password = passwordBox.getText().toString();


               if(id.isEmpty()){
                   idBox.setError("ID number is required");
                   idBox.requestFocus();
                   return;
               }

           //    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //       emailBox.setError("Invalid email");
            //       emailBox.requestFocus();
             //      return;
            //   }

               if(password.isEmpty()){
                   passwordBox.setError("Password is required");
                   passwordBox.requestFocus();
                   return;
               }

               getEmail(id);





           }
       });

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide);
                animation.setDuration(800);
              //  splashText.startAnimation(animation);
              //  splashView.startAnimation(animation);
                splashText.setVisibility(View.GONE);
                splashView.setVisibility(View.GONE);
                evsuView.setVisibility(View.VISIBLE);
                loginForm.setVisibility(View.VISIBLE);
                tipsImage.setVisibility(View.VISIBLE);

                if (preferences.getBoolean("show", true) == true){
                    showTips();

                }

            }
        }, SPLASH_TIME_OUT);

    }

    public void showTips(){
        myLayout = inflater.inflate(R.layout.tips, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Login.this);

        TextView tipsClose = (TextView) myLayout.findViewById(R.id.tipsClose);
        Switch tipsSwitch = (Switch) myLayout.findViewById(R.id.tipsSwitch);
        Button tipsConfirm = (Button) myLayout.findViewById(R.id.tipsConfirm);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        tipsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (tipsSwitch.isChecked() == true){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("show", true);
                    editor.apply();

                }else{
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("show",false);
                    editor.apply();

                }

            }
        });

        if (preferences.getBoolean("show", true) == true){
            tipsSwitch.setChecked(true);

        }else{
            tipsSwitch.setChecked(false);
        }

        tipsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("show", false);
                editor.apply();
                dialog.dismiss();
            }
        });

        tipsConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });





        builder.setView(myLayout);
        dialog = builder.create();

        dialog.setCancelable(false);
        dialog.show();
    }


    public void getEmail(String id){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        progressBar2.setVisibility(View.VISIBLE);
        mStore.collection("Users")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){

                                progressBar2.setVisibility(View.GONE);
                                idBox.setError("ID Number does not exist");
                                Toast.makeText(Login.this, "ID Number does not exist", Toast.LENGTH_SHORT).show();

                        }else{

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {


                                    if (document.exists()){
                                        // progressBar2.setVisibility(View.VISIBLE);
                                        mAuth.signInWithEmailAndPassword(document.getString("email") ,passwordBox.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (task.isSuccessful()){

                                                    verify();

                                                }else{
                                                    Toast.makeText(Login.this, "Failed to login: "+  task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    progressBar2.setVisibility(View.GONE);
                                                }

                                            }
                                        });
                                    }

                                }


                            } else {
                                Toast.makeText(Login.this, "Failed to login: "+  task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar2.setVisibility(View.GONE);
                            }
                        }



                    }
                });


    }

    public void verify() {

        userId = mAuth.getCurrentUser().getUid();


        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().getUid();

      //  Map<String,Object> logs = new HashMap<>();
     //   long dateNow = new Date().getTime();
     //   logs.put(String.valueOf(dateNow),"Successfully logged in to eVPOLL.");
     //   DocumentReference documentReference = mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("others").document("logs");
     //   documentReference.update(logs);

        Intent myIntent = new Intent(Login.this, UserProfile.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Login.this.startActivity(myIntent);






       /*
        DocumentReference docRef = mStore.collection("Administrator").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Intent myIntent = new Intent(Login.this, Administrator.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Login.this.startActivity(myIntent);

                    } else {
                        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.getCurrentUser().getUid();

                        Map<String,Object> logs = new HashMap<>();
                        long dateNow = new Date().getTime();
                        logs.put(String.valueOf(dateNow),"Successfully logged in to eVPOLL.");
                        DocumentReference documentReference = mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("others").document("logs");
                        documentReference.update(logs);

                        Intent myIntent = new Intent(Login.this, UserProfile.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Login.this.startActivity(myIntent);
                    }
                } else {
                    Toast.makeText(Login.this, "Please try again: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


*/
    /*    userId = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = mStore.collection("Administrator").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                final String type = value.getString("type");


                if (value.getString("type").equals("admin")){

                    isAdmin = true;

               }else{

                    isAdmin = false;
                }
*/


    }




    }

