package com.example.myapplication2;

public class Record {
    String date,time;
    double bmi;
    int weight,length;

    public Record(){}

    public Record(int weight, int length, String date, String time, double bmi) {
        this.weight = weight;
        this.length = length;
        this.date = date;
        this.time = time;
        this.bmi = bmi;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
