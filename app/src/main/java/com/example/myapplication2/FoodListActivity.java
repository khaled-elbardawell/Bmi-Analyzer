package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FoodListActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<Food> foods;
    FoodRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        // view elements
        recyclerView = findViewById(R.id.rv_food_list);

        // init & set list and recyclerView
        foods = new ArrayList<>();
        adapter = new FoodRecyclerAdapter(this,foods);

        RecyclerView.LayoutManager lm = new GridLayoutManager(this,1);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(adapter);


        // get foods from firebase (Listener)
        list_foods_listener_from_firebase();

    }


    private void list_foods_listener_from_firebase(){
        FireBaseDB.DB.getCurrentUserFoods().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foods.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Food food = dataSnapshot.getValue(Food.class);
                    food.setKey(dataSnapshot.getKey());
                    foods.add(food);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}