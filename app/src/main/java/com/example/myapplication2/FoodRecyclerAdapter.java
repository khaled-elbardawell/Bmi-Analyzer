package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodRecyclerAdapter extends RecyclerView.Adapter<FoodRecyclerAdapter.FoodViewHolder> {

    ArrayList<Food> foods;
    Context context;

    public FoodRecyclerAdapter(Context context,ArrayList<Food> foods) {
        this.foods = foods;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_food_item,null,false);
        FoodViewHolder foodViewHolder = new FoodViewHolder(view);
        return foodViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder,int position) {
         Food food = foods.get(position);
         holder.food_title.setText(food.name);
         holder.tv_nameCategory.setText(food.category);
         holder.tv_calory.setText(food.calory+" cal/g");

         // render food image to imageView
        Picasso.get()
                .load(food.getImageUrl())
//                .resize(50, 50)
                .fit()
                .centerCrop()
                .into(holder.img);




         // edit food listener
         holder.edit_food.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(context, EditFoodActivity.class);
                 intent.putExtra("name",food.name);
                 intent.putExtra("calory",food.getCalory());
                 intent.putExtra("category",food.getCategory());
                 intent.putExtra("image_url",food.getImageUrl());
                 intent.putExtra("key",food.getKey());
                 context.startActivity(intent);
             }
         });

        // delete food listener
        String image_name_from_url = Uri.parse(food.getImageUrl()).getLastPathSegment();
        final String image_url_before_delete = image_name_from_url.substring(image_name_from_url.lastIndexOf('/') +1);

         holder.delete_food.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 FireBaseDB.DB.getCurrentUserFoods().child(food.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                /****************************************************
                                 *  Delete food file after delete food successfully
                                 ****************************************************/
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                // Create a storage reference from our app
                                StorageReference storageRef = storage.getReference();
                                // Create a reference to the file to delete
                                StorageReference desertRef = storageRef.child("foods/"+image_url_before_delete);

                                // Delete file from firebase storage
                                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully
                                        Toast.makeText(context, "Successfully deleted food", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                    }
                                });
                            }else{
                                Toast.makeText(context, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                     }
                 });
             }
         });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder{
        TextView food_title,tv_nameCategory,tv_calory;
        ImageView delete_food,img;
        AppCompatButton edit_food;
        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            food_title   = itemView.findViewById(R.id.food_title);
            tv_nameCategory = itemView.findViewById(R.id.tv_nameCategory);
            tv_calory = itemView.findViewById(R.id.tv_calory);
            delete_food = itemView.findViewById(R.id.delete_food);
            edit_food = itemView.findViewById(R.id.edit_food);
            img = itemView.findViewById(R.id.img);
        }
    }
}
