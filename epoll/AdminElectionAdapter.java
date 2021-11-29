package com.example.epoll;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AdminElectionAdapter extends RecyclerView.Adapter<AdminElectionAdapter.ViewHolder> {
    private List<AdminElectionList> myListList;
    private Context ct;

    public AdminElectionAdapter(List<AdminElectionList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.create_candidate,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.layoutCandidate = (ConstraintLayout) holder.itemView.findViewById(R.id.layoutCandidate);
        holder.layoutAdd = (CardView) holder.itemView.findViewById(R.id.layoutAdd);
        holder.layoutAddButton = (TextView) holder.itemView.findViewById(R.id.layoutAddText);

        AdminElectionList myList=myListList.get(position);

        if (myList.getPosition() == null){

        }else{
            String candidString = String.valueOf(myList.getCandidates().toString()).replace("{", "[").replace("}", "]").replace("=","-");
            holder.layoutCandidate.setVisibility(View.VISIBLE);
            holder.layoutAdd.setVisibility(View.GONE);
            holder.position.setText(myList.getPosition());
            holder.candidatesArray.setText(candidString);
            holder.limit.setText("Limit: "+myList.getLimit().toString());
        }

        holder.removeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     Toast.makeText(ct, String.valueOf(position), Toast.LENGTH_SHORT).show();
                //AdminElection.delete(position);
                AdminElection.myLists.remove(position);
                AdminElection.adapter = new AdminElectionAdapter(AdminElection.myLists,ct);
                AdminElection.rv.setAdapter(AdminElection.adapter);
               //
               // AdminElection.adapter.notifyDataSetChanged();
            }
        });





        holder.layoutAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                holder.adminElection.startAnother(ct);
           // here    holder.adminElection.candidatesAdd(ct);


/*

                holder.myLayout = LayoutInflater.from(ct).inflate(R.layout.candidates_list, null);

                ListView candidateList = (ListView) holder.myLayout.findViewById(R.id.candidatesList);

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ct);


                TextView insertText = (TextView) holder.myLayout.findViewById(R.id.insertText);
                insertText.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(View v) {


                        TextView candidateNameText = (TextView) holder.myLayout.findViewById(R.id.candidateNameText);
                        TextView candidateCourseText = (TextView) holder.myLayout.findViewById(R.id.candidateCourseText);
                        candidateList.setNestedScrollingEnabled(true);


                        holder.candidateArray.add("something");
                        holder.adapter=new ArrayAdapter<String>(ct,
                                android.R.layout.simple_list_item_1,
                                holder.candidateArray);
                        candidateList.setAdapter(holder.adapter);
                        holder.adapter.notifyDataSetChanged();
                    }

                });




                builder.setView(holder.myLayout).setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {




                    }
                });

                builder.setNegativeButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                androidx.appcompat.app.AlertDialog dialog = builder.create();

                dialog.setCancelable(false);
                dialog.show();

                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AdminElection.candidatesAdd();

                       // AdminElection.selectAllowed.performClick();
                       // dialog.dismiss();
                       // dialog.cancel();
                     //   dialog.dismiss();
                    }
                });

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     //   holder.candidateArray.clear();
                     //   holder.adapter=new ArrayAdapter<String>(ct,
                   //             android.R.layout.simple_list_item_1,
                   //             holder.candidateArray);
                   //     candidateList.setAdapter(holder.adapter);
                   //     holder.adapter.notifyDataSetChanged();
                       dialog.dismiss();
                        dialog.cancel();

                    }
                });


*/

            }
        });





    }

    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView layoutAdd,addTest;
        TextView layoutAddButton;
        AdminElection adminElection;
        ConstraintLayout layoutCandidate;

        TextView position,candidatesArray,limit;

        ImageButton removeCard;



        ArrayList<String> candidateArray;
        ArrayAdapter<String> adapter;


        View myLayout;



        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            adminElection = new AdminElection();
            removeCard = (ImageButton) itemView.findViewById(R.id.removeCard);


            candidatesArray=(TextView)itemView.findViewById(R.id.candidateArray);
            position=(TextView)itemView.findViewById(R.id.position);
            limit=(TextView)itemView.findViewById(R.id.limit);

        }
    }
}