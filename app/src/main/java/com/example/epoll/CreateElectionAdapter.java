package com.example.epoll;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CreateElectionAdapter extends RecyclerView.Adapter<CreateElectionAdapter.ViewHolder> {
    private List<CreateElectionList> myListList;
    private Context ct;

    public CreateElectionAdapter(List<CreateElectionList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.election_candidates,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        CreateElectionList myList=myListList.get(position);
        holder.elecName.setText(myList.getCandidName());
        holder.elecInfo.setText(myList.getCandidInfo());
        holder.elecParty.setText(myList.getCandidParty());



        holder.deleteCandid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateElection.myLists.remove(position);
                CreateElection.adapter = new CreateElectionAdapter(CreateElection.myLists,ct);
                CreateElection.rv.setAdapter(CreateElection.adapter);
            }
        });





    }

    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView deleteCandid;
        private EditText choicePoll;
        private TextView elecName,elecInfo,elecParty;
        private ConstraintLayout candidateAdd,layoutChoice;
        CreateElection createElection;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            createElection = new CreateElection();
            elecName=(TextView)itemView.findViewById(R.id.elecName);
            elecInfo=(TextView)itemView.findViewById(R.id.elecInfo);
            elecParty=(TextView)itemView.findViewById(R.id.elecParty);

         //   choicePoll=(EditText)itemView.findViewById(R.id.choicePoll);
            deleteCandid=(ImageView)itemView.findViewById(R.id.deleteCandid);
          //  layoutChoice=(ConstraintLayout)itemView.findViewById(R.id.layoutChoice);
          //  layoutAddChoice=(ConstraintLayout)itemView.findViewById(R.id.layoutAddChoice);
            //timeView=(TextView)itemView.findViewById(R.id.pollAllowed);
        }
    }
}