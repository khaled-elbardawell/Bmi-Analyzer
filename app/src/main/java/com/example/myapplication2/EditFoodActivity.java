package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class EditFoodActivity extends AppCompatActivity {

    EditText et_name,et_calory;
    Spinner et_category;
    AppCompatButton upload_photo,save;
    ImageView image_food;
    Uri imageUri = null;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    String image_name;
    Food food = new Food();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        // view elements
        et_name = findViewById(R.id.et_name);
        et_calory = findViewById(R.id.et_calory);
        image_food = findViewById(R.id.image_food);
        et_category = findViewById(R.id.et_category);
        upload_photo = (AppCompatButton)findViewById(R.id.upload_photo);
        save = (AppCompatButton)findViewById(R.id.save);


        // get default data from prev activity
        Intent intent = getIntent();
        food.setName(intent.getStringExtra("name"));
        food.setCalory(intent.getStringExtra("calory"));
        food.setCategory(intent.getStringExtra("category"));
        food.setImageUrl(intent.getStringExtra("image_url"));
        food.setKey(intent.getStringExtra("key"));


        // set view elements default value
        et_name.setText(food.getName());
        et_calory.setText(food.getCalory());

        int selected_item_pos = 0;
        String[] categories;
        categories=getResources().getStringArray(R.array.list_category);
        for (int i = 0; i < categories.length;i++ ){
            if(food.getCategory().equals(categories[i])){
                selected_item_pos = i;
                break;
            }
        }

        et_category.setSelection(selected_item_pos);


        Picasso.get()
                .load(food.getImageUrl())
//                .resize(50, 50)
                .fit()
//                .centerCrop()
                .into(image_food);



        // init firebase attr
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_data();
            }
        });

    }

    private void save_data(){
        food.setName(this.et_name.getText().toString().trim());
        food.setCalory(this.et_calory.getText().toString().trim());
        food.setCategory(et_category.getSelectedItem().toString());

        // validation
        if ( food.getName().isEmpty() ){
            this.et_name.setError("name is required!");
            this.et_name.requestFocus();
            return;
        }

        if ( food.getCalory().isEmpty() ){
            this.et_calory.setError("calory is required!");
            this.et_calory.requestFocus();
            return;
        }


        if (food.getCategory().isEmpty() ){
            Toast.makeText(getApplicationContext(),"category is required ",Toast.LENGTH_LONG).show();
            return;
        }

        if (image_food.getDrawable()==null){
            Toast.makeText(getApplicationContext(),"Food photo is required ",Toast.LENGTH_LONG).show();
            return;
        }
        // .... end validation


        // update food data and upload image to storage in firebase
        SaveFoodInFireBase();

    }



    private void SaveFoodInFireBase(){
        if(imageUri != null){ // save data with upload new image
            // set image_name attr in this class => (image food name)
            createImageName();
            updateFoodWithupdateImage();

        }else{ // just save data

            // show progress dialog
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Save Data...");
            pd.show();

            FireBaseDB.DB.getCurrentUserFoods().child(food.getKey()).setValue(food)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.dismiss();
                            if(task.isSuccessful()){
                                // redirect to home activity
                                Intent intent = new Intent(getApplicationContext(),FoodListActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Network Connection Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // hide dialog
                            pd.dismiss();
                        }
                    });
        }

    }

    private void updateFoodWithupdateImage(){
        // show progress dialog
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Save Data...");
        pd.show();

        // set image_name attr in this class => (image food name)
        createImageName();

        // image url before delete
        String image_name_from_url = Uri.parse(food.getImageUrl()).getLastPathSegment();
        final String image_url_before_delete = image_name_from_url.substring(image_name_from_url.lastIndexOf('/') +1);


        // update  image with save food data and delete old image
        StorageReference riversRef = storageReference.child("foods/"+image_name);
        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // get image uri from firebase
                                image_name = uri.toString();
                                food.setImageUrl(image_name);

                                // save new food in current user foods
                                FireBaseDB.DB.getCurrentUserFoods().child(food.getKey()).setValue(food).
                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pd.dismiss();

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
                                                            // redirect to home activity
                                                            Intent intent = new Intent(getApplicationContext(),FoodListActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            // Uh-oh, an error occurred!
                                                        }
                                                    });

                                                }else{
                                                    Toast.makeText(getApplicationContext(), "Network Connection Error", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // hide dialog
                                        pd.dismiss();
                                    }
                                });

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed To Uploaded",Toast.LENGTH_LONG).show();
                    }
                });
    }



    private void createImageName(){
        image_name = UUID.randomUUID().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        image_name = currentDateandTime + "_" + image_name;
    }


    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            image_food.setImageURI(imageUri);
        }
    }
}