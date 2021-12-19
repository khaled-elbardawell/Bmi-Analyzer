package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfoActivity extends AppCompatActivity  {

    RadioGroup gender;
    EditText et_dob,et_num_length,et_num_weight;
    AppCompatButton plus,minus,minus_length,plus_length,save_data ;
    int gender_value = 1;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // view elements
        gender = findViewById(R.id.gender);
        et_dob = findViewById(R.id.et_dob);
        et_num_length = findViewById(R.id.et_num_length);
        et_num_weight = findViewById(R.id.et_num_weight);
        plus = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        plus_length = findViewById(R.id.plus_length);
        minus_length = findViewById(R.id.minus_length);
        save_data = (AppCompatButton)findViewById(R.id.save_data);

        // radio listener (gender)
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.male:
                        gender_value = 1;
                        break;
                    case R.id.female:
                        gender_value = 0;
                        break;
                }
            }
        });

        // listener weight and length buttons (+ , -)
        weight_listener();
        length_listener();

        // save data listener
        save_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_data();
            }
        });

    }

    private void save_data(){
        String dob = this.et_dob.getText().toString().trim();
        String length = this.et_num_length.getText().toString().trim();
        String weight = this.et_num_weight.getText().toString().trim();

        // validation
        if ( weight.isEmpty() ){
            this.et_num_weight.setError("weight is required!");
            this.et_num_weight.requestFocus();
            return;
        }

        int num_weight = Integer.parseInt(this.et_num_weight.getText().toString().trim());
        if (num_weight < 10){
            this.et_num_weight.setError("weight must be grater than 10!");
            this.et_num_weight.requestFocus();
            return;
        }


        if ( length.isEmpty() ){
            this.et_num_length.setError("length is required!");
            this.et_num_length.requestFocus();
            return;
        }

        int num_length = Integer.parseInt(this.et_num_length.getText().toString().trim());
        if (num_length < 20){
            this.et_num_length.setError("length must be grater than 20!");
            this.et_num_length.requestFocus();
            return;
        }

        if (dob.isEmpty()){
            this.et_dob.setError("date must be valid ex. 06/09/2022");
            this.et_dob.requestFocus();
            return;
        }

        if(!validateDOB(dob)){
            this.et_dob.setError("date must be valid ex. 06/09/2022");
            this.et_dob.requestFocus();
            return;
        }

        if (gender_value < 0){
            return;
        }
       // .... end validation


        // save info in firebase
        SaveInfoInFireBase(dob,num_length,num_weight,gender_value);

    }

    private void SaveInfoInFireBase(String dob,int length,int weight,int gender){
        // show dialog
        dialog = new ProgressDialog(InfoActivity.this);
        dialog.setMessage("loading please wait..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        // update user data => (gender)
        FireBaseDB.DB.getCurrentUserData().child("gender").setValue(gender);
        FireBaseDB.DB.getCurrentUserData().child("dob").setValue(dob);

       // update user data (Info) => First record
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());


        // calc bmi
        double bmi = (((double) weight / length) * getAgePercent(dob,gender));

        // create record obj with data
        Record record = new Record(weight,length,date,time,bmi);

        // save first record in current user records
        FireBaseDB.DB.getCurrentUserData().child("records").push().setValue(record).
                   addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // hide dialog
                    if(task.isSuccessful()){
                        dialog.hide();
                        // redirect to home activity
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        dialog.hide();
                        Toast.makeText(getApplicationContext(), "Network Connection Error", Toast.LENGTH_SHORT).show();
                    }
                }
             });

        // hide dialog
        dialog.hide();
      }

    private double getAgePercent(String dob,int gender){
           int age =  getAge(dob);
            if (age <= 2 && age <= 10){
                return 70;
            }else if ( (age < 10 && age <= 20) && (gender == 1) ){
                return 90;
            }else if ( (age < 10 && age <= 20) && (gender == 0) ){
                return 80;
            }else{
                return 100;
            }
    }

    private boolean validateDOB(String date){
        String regEx ="^((0|1|2|3)[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d{2}$";
        Matcher matcherObj = Pattern.compile(regEx).matcher(date);
        if (matcherObj.matches())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void weight_listener(){
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementAndDecreaseAction(et_num_weight,1);
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementAndDecreaseAction(et_num_weight,0);
            }
        });
    }

    private void length_listener(){
        plus_length.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementAndDecreaseAction(et_num_length,1);
            }
        });

        minus_length.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementAndDecreaseAction(et_num_length,0);
            }
        });
    }

    private void incrementAndDecreaseAction(EditText num,int status){
        String num_str_value = num.getText().toString();
        int n = Integer.parseInt(num_str_value);
        if(status == 0){ // decrease action
            if(n > 0){
                --n;
            }
        }else{ // increment action
            n++;
        }
        num.setText(String.valueOf(n));
    }

    private static int getAge(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int age = 0;
        try {
            Date date1 = dateFormat.parse(date);
            Calendar now = Calendar.getInstance();
            Calendar dob = Calendar.getInstance();
            dob.setTime(date1);
            if (dob.after(now)) {
                throw new IllegalArgumentException("Can't be born in the future");
            }
            int year1 = now.get(Calendar.YEAR);
            int year2 = dob.get(Calendar.YEAR);
            age = year1 - year2;
            int month1 = now.get(Calendar.MONTH);
            int month2 = dob.get(Calendar.MONTH);
            if (month2 > month1) {
                age--;
            } else if (month1 == month2) {
                int day1 = now.get(Calendar.DAY_OF_MONTH);
                int day2 = dob.get(Calendar.DAY_OF_MONTH);
                if (day2 > day1) {
                    age--;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return age ;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( dialog!=null && dialog.isShowing() ){
            dialog.cancel();
        }
    }
}