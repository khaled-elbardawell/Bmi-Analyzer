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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddFoodActivity extends AppCompatActivity {

    EditText et_name,et_calory;
    Spinner et_category;
    AppCompatButton upload_photo,save;
    ImageView image_food;
    Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    String image_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        // view elements
        et_name = findViewById(R.id.et_name);
        et_calory = findViewById(R.id.et_calory);
        image_food = findViewById(R.id.image_food);
        et_category = findViewById(R.id.et_category);
        upload_photo = (AppCompatButton)findViewById(R.id.upload_photo);
        save = (AppCompatButton)findViewById(R.id.save);


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
        String name = this.et_name.getText().toString().trim();
        String calory = this.et_calory.getText().toString().trim();
        String category = et_category.getSelectedItem().toString();

        // validation
        if ( name.isEmpty() ){
            this.et_name.setError("name is required!");
            this.et_name.requestFocus();
            return;
        }

        if ( calory.isEmpty() ){
            this.et_calory.setError("calory is required!");
            this.et_calory.requestFocus();
            return;
        }


        if (category.isEmpty() ){
            Toast.makeText(getApplicationContext(),"category is required ",Toast.LENGTH_LONG).show();
            return;
        }

        if (image_food.getDrawable()==null){
            Toast.makeText(getApplicationContext(),"Food photo is required ",Toast.LENGTH_LONG).show();
            return;
        }
        // .... end validation


        // save food data and upload image to storage in firebase
        SaveFoodInFireBase(name,calory,category);

    }


    private void SaveFoodInFireBase(String name,String calory,String category){
        // show progress dialog
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Save Data...");
        pd.show();

        // set image_name attr in this class => (image food name)
        createImageName();


         // upload image with save food data
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

                                // create food obj with data
                                Food food = new Food(name,category,calory,image_name);

                                // save new food in current user foods
                                FireBaseDB.DB.getCurrentUserFoods().push().setValue(food).
                                        addOnCompleteListener(new OnCompleteListener<Void>() {
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



//    method upload image to firebase with progress (%)

//    private void uploadImageToFirebaseStorage(){
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setTitle("Uploading Image...");
//        pd.show();
//
//        final String randomKey = UUID.randomUUID().toString();
//        StorageReference riversRef = storageReference.child("foods/"+randomKey);
//
//        riversRef.putFile(imageUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                        pd.dismiss();
//                        Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_LONG).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        pd.dismiss();
//                        Toast.makeText(getApplicationContext(),"Failed To Uploaded",Toast.LENGTH_LONG).show();
//                    }
//                })
//        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
//                pd.setMessage("Progress: "+ (int) progressPercent + "%");
//            }
//        })
//        ;
//
//    }
}