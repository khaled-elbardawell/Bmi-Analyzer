package com.example.myapplication2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecordRecyclerAdapter extends RecyclerView.Adapter<RecordRecyclerAdapter.RecordViewHolder> {

    ArrayList<Record> records;

    public RecordRecyclerAdapter(ArrayList<Record> records) {
        this.records = records;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_record_item,null,false);
        RecordViewHolder recordViewHolder = new RecordViewHolder(view);
        return recordViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
         Record record = records.get(position);
         holder.tv_data.setText(record.date);
         if (record.bmi < 18.5){
             holder.tv_status.setText("Underweight");
         }else if (18.5 <= record.bmi &&  record.bmi < 25){
             holder.tv_status.setText("Healthy Weight");
        }else if (25 <= record.bmi &&  record.bmi < 30){
             holder.tv_status.setText("Overweight");
         }else if ( record.bmi >= 30){
             holder.tv_status.setText("Obesity");
         }

         holder.tv_length.setText(record.length+" cm");
         holder.tv_weight.setText(record.weight+" kg");
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder{
        TextView tv_data,tv_weight,tv_length,tv_status;
        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_data   = itemView.findViewById(R.id.tv_date);
            tv_weight = itemView.findViewById(R.id.tv_weight);
            tv_length = itemView.findViewById(R.id.tv_length);
            tv_status = itemView.findViewById(R.id.tv_status);
        }
    }
}
