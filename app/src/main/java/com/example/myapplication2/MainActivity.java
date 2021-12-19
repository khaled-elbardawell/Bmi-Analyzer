package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void calcAge(View view) {
        EditText value = (EditText) findViewById(R.id.editTextText1);
        int v = Integer.parseInt(value.getText().toString());
        v = 2021 - v;
        Toast.makeText(this,"age: " + String.valueOf(v),Toast.LENGTH_LONG).show();
    }
}