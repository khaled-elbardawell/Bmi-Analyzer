package com.example.myapplication2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseDB {


    public static class DB{
        public static DatabaseReference getUsers(){
            return FirebaseDatabase.getInstance().getReference("users");
        }

        public static DatabaseReference getCurrentUserData(){
            return getUsers().child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        public static DatabaseReference getCurrentUserFoods(){
            return getCurrentUserData().child("foods");
        }

        public static DatabaseReference getCurrentUserRecords(){
            return getCurrentUserData().child("records");
        }

        public static DatabaseReference getCurrentUsername(){
            return getCurrentUserData().child("name");
        }
    }
}
