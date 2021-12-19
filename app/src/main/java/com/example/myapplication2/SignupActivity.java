package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText name,email,password,re_password;
    Button signup;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // firebase instance
        mAuth = FirebaseAuth.getInstance();

        // view elements
        name = findViewById(R.id.et_name);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        re_password = findViewById(R.id.et_repassword);
        signup = findViewById(R.id.sign_up);

        // signup button listener
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // signup logic method
                 signUp();
            }
        });

    }


    public void signUp(){
       String name = this.name.getText().toString().trim();
       String email = this.email.getText().toString().trim();
       String password = this.password.getText().toString().trim();
       String re_password = this.re_password.getText().toString().trim();

       // validation
        if (name.isEmpty()){
            this.name.setError("name is required!");
            this.name.requestFocus();
            return;
        }

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


        if (!password.equals(re_password)){
            this.password.setError("password is not match with re-password please try again!");
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
        SignUpInFireBase(name,email,password);
    }



    private void SignUpInFireBase(String name,String email,String password){
        dialog = new ProgressDialog(SignupActivity.this);
        dialog.setMessage("loading please wait..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                           User user = new User(email,name);

                         FireBaseDB.DB.getCurrentUserData()
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        dialog.hide();
                                        Toast.makeText(SignupActivity.this, "user has been registered successfully!", Toast.LENGTH_LONG).show();
                                        // redirect to info layout (complete profile)
                                        startActivity(new Intent(SignupActivity.this,InfoActivity.class));
                                        finish();
                                    }else{
                                        dialog.hide();
                                        Toast.makeText(SignupActivity.this, "Failed to registered Try again!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }else{
                            dialog.hide();
                            Toast.makeText(SignupActivity.this, "Failed to registered Try again!", Toast.LENGTH_LONG).show();
                        }

                    }
                });



    }


    public void LoginView(View view) {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }


//    public void infoView(View view) {
//        Intent intent = new Intent(getApplicationContext(),InfoActivity.class);
//        startActivity(intent);
//        finish();
//    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( dialog!=null && dialog.isShowing() ){
            dialog.cancel();
        }
    }

}
