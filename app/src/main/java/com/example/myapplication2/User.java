package com.example.myapplication2;

import java.util.ArrayList;

public class User {
    private String email,name,dob = null;
    private int gender;

    public User(){}

    public User(String email,String name){
         this.email = email;
         this.name  = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
