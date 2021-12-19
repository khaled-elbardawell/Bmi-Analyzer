package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView welcome_msg ,current_status ,logout;
    AppCompatButton add_food,add_record,view_food;


    ArrayList<Record> records;
    RecordRecyclerAdapter adapter;

    String messages_list[][] =
        {
           { "So Bad"     , "So Bad"     ,"So Bad"          , "Little Changes" ,"Little Changes" , "Still Good" , "Go Ahead"   ,"Go Ahead"  },
           { "So Bad"     , "Be Careful" ,"Be Careful"      , "Little Changes" ,"Little Changes" , "Be Careful" , "Be Careful" ,"Be Careful"},
           { "Be Careful" , "Go Ahead"   ,"Still Good"      , "Little Changes" ,"Little Changes" , "Be Careful" , "So Bad"     ,"So Bad"    },
           { "Go Ahead"   , "Go Ahead"   ,"Little Changes"  , "Little Changes" ,"Be Careful"     , "So Bad"     , "So Bad"     ,"So Bad"    }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // view elements
        recyclerView = findViewById(R.id.rv_records);
        welcome_msg = findViewById(R.id.welcome_msg);
        current_status = findViewById(R.id.current_status);
        logout = findViewById(R.id.logout);
        add_food = (AppCompatButton)findViewById(R.id.add_food);
        add_record = (AppCompatButton)findViewById(R.id.add_record);
        view_food = (AppCompatButton)findViewById(R.id.view_food);

        // set welcome msg
        setWelcomeMsg();


        // init & set list and recyclerView
        records = new ArrayList<>();
        adapter = new RecordRecyclerAdapter(records);

        RecyclerView.LayoutManager lm = new GridLayoutManager(this,1);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);


        // get records from firebase (Listener)
        list_records_listener_from_firebase();


        // logout firebase
        logoutListener();


        // buttons redirect to another activity
        add_food_view();
        add_record_view();
        view_food_view();

    }

    private void setWelcomeMsg(){
        FireBaseDB.DB.getCurrentUsername().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                welcome_msg.setText("Hi, " + snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void list_records_listener_from_firebase(){
        FireBaseDB.DB.getCurrentUserRecords().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                records.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Record record = dataSnapshot.getValue(Record.class);
                    records.add(record);
                }
                adapter.notifyDataSetChanged();
                current_status.setText(getCurrentStatus());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void logoutListener(){
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
            }
        });
    }

    private void add_food_view(){
        add_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddFoodActivity.class);
                startActivity(intent);
            }
        });

    }

    private void add_record_view(){
        add_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NewRecordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void view_food_view(){
        view_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FoodListActivity.class);
                startActivity(intent);
            }
        });
    }


    private String getCurrentStatus(){
        int col = 0,row = 0;
        String current_status = null;
        double last_bmi_record = records.get(records.size()-1).getBmi();

        if ( last_bmi_record < 18.5){ // Underweight
            row = 0;
            current_status = "Underweight";
        }else if (18.5 <= last_bmi_record &&  last_bmi_record < 25){ // Healthy Weight
            row = 1;
            current_status = "Healthy Weight";
        }else if (25 <= last_bmi_record &&  last_bmi_record < 30){ // Overweight
            row = 2;
            current_status = "Overweight";
        }else if ( last_bmi_record >= 30){ // Obesity
            row = 3;
            current_status = "Obesity";
        }


        if (records.size() > 1){ // multi records
            double before_last_bmi_record = records.get(records.size() - 2 ).getBmi();
            double diff = last_bmi_record - before_last_bmi_record;

            // format double diff number
            DecimalFormat df = new DecimalFormat("0.0");
            diff = Double.parseDouble(df.format(diff)); // ex: 0.7

            if(diff == 0.0){
                current_status = current_status +  " (Still Good)";
            }else{
                // get col index
                if(diff < -1){
                    col = 0;
                }else if(-1 <= diff && diff < -0.6){
                    col = 1;
                }else if(-0.6 <= diff && diff < -0.3){
                    col = 2;
                }else if(-0.3 <= diff && diff < 0){
                    col = 3;
                }else if(0 < diff && diff < 0.3){
                    col = 4;
                }else if(0.3 <= diff && diff < 0.6){
                    col = 5;
                }else if(0.6 <= diff && diff < 1){
                    col = 6;
                }else if(diff >= 1){
                    col = 7;
                }

                current_status = current_status +  " (" + (messages_list[row][col]) + ")";
            }


        }else{ // just one record
            current_status = current_status +  " (Still Good)";
        }

        return current_status;
    }





}