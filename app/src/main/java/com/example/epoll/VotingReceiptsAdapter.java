package com.example.epoll;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VotingReceiptsAdapter extends RecyclerView.Adapter<VotingReceiptsAdapter.ViewHolder> {
    private List<VotingReceiptsList> myListList;
    private Context ct;

    public VotingReceiptsAdapter(List<VotingReceiptsList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_list,parent,false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VotingReceiptsList myList=myListList.get(position);
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        holder.positionList= new ArrayList<>();

        mStore.collection("Election").document(myList.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String[] values;
                    values = String.valueOf(task.getResult().get("position").toString()).replace("[", "").replace("]", "").replace("", "").split(", ");

                    for (int x = 0; x < values.length; x++) {
                        holder.positionList.add(values[x]);

                    }
                    holder.hasData = true;
                }else {
                    holder.hasData = false;
                }




            }
        });





        holder.receiptName.setText(myList.getName());
        holder.receiptDate.setText("Date voted: " + DateFormat.format("MMM dd yyyy hh:mm aa",myList.getTime()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.hasData == true){
                    mStore.collection("Users").document(UserProfile.creatorId).collection("receipts").document(myList.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            String details = "";
                            details += "Eastern Visayas State University"+ System.getProperty("line.separator");
                            details += "Election: "+myList.getName()  + System.getProperty("line.separator")+ System.getProperty("line.separator");
                            int x = 0;
                            do {
                                String convert;
                                convert = String.valueOf(task.getResult().get(holder.positionList.get(x)).toString()).replace("[", "").replace("]", "");
                                if (convert.matches("")){
                                    details += holder.positionList.get(x) + " : " + "None" + System.getProperty("line.separator");
                                }else{
                                    details += holder.positionList.get(x) + " : " + convert + System.getProperty("line.separator");
                                }


                                x++;
                            } while(x < holder.positionList.size());

                            AlertDialog.Builder builder = new AlertDialog.Builder(ct);
                            //builder.setTitle(myList.getParty_name());

                            builder.setMessage(details);
                            builder.setCancelable(true);

                            builder.setPositiveButton(
                                    "Hide",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.show();

                            //  TextView titleView = (TextView)alert.findViewById(ct.getResources().getIdentifier("alertTitle", "id", "android"));
                            //  if (titleView != null) {
                            //      titleView.setGravity(Gravity.CENTER);
                            //  }

                            TextView messageView = (TextView)alert.findViewById(android.R.id.message);
                            messageView.setGravity(Gravity.CENTER);



                        }
                    });
                }else{

                    Toast.makeText(ct, "No data", Toast.LENGTH_SHORT).show();

                }








            }
        });



    }



    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{



        private TextView receiptName,receiptDate;
        ArrayList<String> positionList;
        private ImageView deletePoll;
        Boolean hasData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hasData = false;

            receiptName=(TextView)itemView.findViewById(R.id.receiptName);
            receiptDate=(TextView)itemView.findViewById(R.id.receiptDate);




        }
    }
}