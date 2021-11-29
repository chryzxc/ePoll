package com.example.epoll;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.ViewHolder> {
    private List<LogsList> myListList;
    private Context ct;


    public LogsAdapter(List<LogsList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.logs_list,parent,false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LogsList myList=myListList.get(position);
        String string_date = myList.getDate();
       // long milliseconds = 0;

        long l = 0;
        SimpleDateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZ yyyy");
        try {
            Date d = f.parse(string_date);
            l = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    //    Toast.makeText(ct, String.valueOf(l), Toast.LENGTH_SHORT).show();

     //   String dateString = myList.getDate();
      //  long l = Long.parseLong(myList.getDate());

        Date convertedDate = new Date(l);
     //   Toast.makeText(ct, String.valueOf(convertedDate), Toast.LENGTH_SHORT).show();

      //  long millisecond = myList.getDate().;
      //  Toast.makeText(ct, String.valueOf(millisecond), Toast.LENGTH_SHORT).show();
     //   Date convertedDate = new Date(millisecond);
     //   Toast.makeText(ct, convertedDate.toString(), Toast.LENGTH_SHORT).show();


        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
      //  Date convertedDate = null;
        try {
            convertedDate = dateFormat.parse(myList.getDate());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      //  Toast.makeText(ct, convertedDate.toString(), Toast.LENGTH_SHORT).show();

        String dayOfTheWeek = (String) DateFormat.format("EEEE",convertedDate );

        String day          = (String) DateFormat.format("dd",   convertedDate); // 20
        String monthString  = (String) DateFormat.format("MMM",  convertedDate); // Jun
        String year         = (String) DateFormat.format("yyyy", convertedDate); // 2013
        String time         = (String) DateFormat.format("hh:mm aa", convertedDate); // 2013



        //holder.textView.setSelected(true);
// edit here
    //    holder.activityLogs.setText(myList.getDescription());
        holder.dayLogs.setText(dayOfTheWeek);
        holder.dateLogs.setText(monthString +" " +day+ ", " + year);
        holder.timeLogs.setText(time);

        holder.activityLogs.setText(myList.getDescription());
        holder.dayLogs.setText(dayOfTheWeek);

       // holder.dateLogs.setVisibility(View.INVISIBLE);
      //  holder.timeLogs.setText(monthString +" " +day+ ", " + year);



    }

    @Override
    public int getItemCount() {
        return myListList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView dateLogs,dayLogs,activityLogs,timeLogs;
        private ConstraintLayout headerView;
        private LinearLayout logsView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
         //   imageView=(ImageView)itemView.findViewById(R.id.myimage);
            dateLogs=(TextView)itemView.findViewById(R.id.dateLogs);
            dayLogs=(TextView)itemView.findViewById(R.id.dayLogs);
            activityLogs=(TextView)itemView.findViewById(R.id.activityLogs);
            timeLogs=(TextView)itemView.findViewById(R.id.timeLogs);
            logsView= (LinearLayout)itemView.findViewById(R.id.logsView);

        }
    }
}