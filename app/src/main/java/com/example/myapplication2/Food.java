package com.example.myapplication2;

public class Food {
    String name,category,calory,imageUrl,key;

    public Food(String name, String category, String calory, String imageUrl) {
        this.name = name;
        this.category = category;
        this.calory = calory;
        this.imageUrl = imageUrl;
    }

    public Food(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCalory() {
        return calory;
    }

    public void setCalory(String calory) {
        this.calory = calory;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
