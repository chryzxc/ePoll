package com.example.epoll;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class AdminPollAdapter extends RecyclerView.Adapter<AdminPollAdapter.ViewHolder> {
    private List<AdminPollList> myListList;
    private Context ct;

    public AdminPollAdapter(List<AdminPollList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_items,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminPollList myList=myListList.get(position);
      //  holder.titleView.setText(myList.getTitle());
       // holder.timeView.setText(myList.getTime());

        if (myList.getQuestion() == null){

        }else{
            holder.layoutAddPoll.setVisibility(View.GONE);
            holder.layoutPoll.setVisibility(View.VISIBLE);
            holder.choicesPoll.setText("Choices: " +myList.getChoices().toString());
            holder.question.setText("Question: "+myList.getQuestion());
            holder.limitPoll.setText("Limit: " + myList.getLimit());

        //    Toast.makeText(ct, myList.getLimitFinal().toString(), Toast.LENGTH_SHORT).show();

          //  Toast.makeText(ct, myList.getChoices().toString(), Toast.LENGTH_SHORT).show();
        }

        holder.removeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminPoll.myLists.remove(position);
                AdminPoll.adapter = new AdminPollAdapter(AdminPoll.myLists,ct);
                AdminPoll.rv.setAdapter(AdminPoll.adapter);
            }
        });




        holder.layoutAddPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        // holder.adminPoll.addPoll(ct);

                holder.adminPoll.startAnother(ct);


                        //   ct.startActivity(new Intent(ct, CreatePoll.class).putExtra("holderPosition", position));


            }
        });



    }

    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView question;
        private TextView choicesPoll;
        private TextView limitPoll;
        private CardView layoutAddPoll;
        private ConstraintLayout layoutPoll;
        AdminPoll adminPoll;
        private ImageView removeCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            adminPoll = new AdminPoll();
            removeCard=(ImageView)itemView.findViewById(R.id.removeCard);

            question=(TextView)itemView.findViewById(R.id.question);
            choicesPoll=(TextView)itemView.findViewById(R.id.choicesPoll);
            limitPoll=(TextView)itemView.findViewById(R.id.limitPoll);

            layoutPoll=(ConstraintLayout)itemView.findViewById(R.id.layoutPoll);
            layoutAddPoll=(CardView)itemView.findViewById(R.id.layoutAddPoll);
            //timeView=(TextView)itemView.findViewById(R.id.pollAllowed);
        }
    }
}