package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String userID;

    EditText email,password;
    Button signin;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // firebase instance
        mAuth = FirebaseAuth.getInstance();

        // view elements
        email = findViewById(R.id.et_name);
        password = findViewById(R.id.et_password);
        signin = findViewById(R.id.signin);

        // signup button listener
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // signup logic method
                login();
            }
        });


    }



    private void login(){
        String email = this.email.getText().toString().trim();
        String password = this.password.getText().toString().trim();

        // validation
        if (email.isEmpty()){
            this.email.setError("email is required!");
            this.email.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            this.email.setError("Invalid email!");
            this.email.requestFocus();
            return;
        }

        if (password.isEmpty()){
            this.password.setError("password is required!");
            this.password.requestFocus();
            return;
        }


        if (password.length() < 6){
            this.password.setError("Min password length should be 6 characters!");
            this.password.requestFocus();
            return;
        }
        //... end validation

        // signup in firebase
        LogInFireBase(email,password);
    }


    private void LogInFireBase(String email,String password){
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("loading please wait..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            userID = firebaseUser.getUid();
                            databaseReference = FirebaseDatabase.getInstance().getReference("users");
                            databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    if(user != null){
                                        dialog.hide();
                                        if(user.getDob() != null){
                                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                            finish();
                                        }else{
                                            startActivity(new Intent(LoginActivity.this,InfoActivity.class));
                                            finish();
                                        }
                                    }else{
                                        dialog.hide();
                                        Toast.makeText(LoginActivity.this, "Failed to login Try again!", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    dialog.hide();
                                    Toast.makeText(LoginActivity.this, "Failed to login Try again!", Toast.LENGTH_LONG).show();
                                }
                            });

                        }else{
                            dialog.hide();
                            Toast.makeText(LoginActivity.this, "Something wrong happened!!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }



    public void signupView(View view) {
        Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( dialog!=null && dialog.isShowing() ){
            dialog.cancel();
        }
    }
}