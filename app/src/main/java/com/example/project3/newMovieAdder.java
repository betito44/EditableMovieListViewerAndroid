package com.example.project3;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.UUID;

public class newMovieAdder  extends AppCompatActivity {


    private static final int REQUEST_FOR_CAMERA=0011;
    private static final int OPEN_FILE=0012;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_PHOTO = 1;
    private Uri imageUri=null;
    private ImageView postImage;

    public EditText title;
    public EditText year;
    public EditText stars;
    public EditText rating;
    public EditText length;
    public EditText director;
    public EditText description;






    public class newMovie {
        public String title;
        public String year;
        public String stars;
        public String rating;
        public String picture;
        public String length;
        public String director;
        public String description;

        public newMovie(String title, String year, String stars, String rating, String picture,
                             String length, String director, String description) {
            this.title = title;
            this.year = year;
            this.stars = stars;
            this.rating = rating;
            this.picture = picture;
            this.length = length;
            this.director = director;
            this.description = description;
        }
    }









        @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_movie);

        Toolbar myToolBar = (Toolbar) findViewById(R.id.my_toolbar2);
        setSupportActionBar(myToolBar);

        postImage = findViewById(R.id.previewImage);

        title = findViewById(R.id.name_add);
        year = findViewById(R.id.year_add);
        length = findViewById(R.id.length_add);
        director = findViewById(R.id.director_add);
        stars = findViewById(R.id.cast_add);
        rating = findViewById(R.id.rating_add);
        description = findViewById(R.id.description_add);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.picture_selector, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.fileAttach:

                pickImage();

                return true;
            case R.id.camera:
                checkPermissions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }








    public void uploadNewPhoto(View view){
        checkPermissions();
    }
    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "This app needs permission to access your camera and photos.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_FOR_CAMERA);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    OPEN_FILE);
        } else {
            takePhoto();
        }
    }


    private void takePhoto(){
        //https://developer.android.com/training/camera-deprecated/photobasics
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        Intent chooser=Intent.createChooser(intent,"Select a Camera App.");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooser, REQUEST_FOR_CAMERA);}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // The Android Camera application encodes the photo in the return Intent delivered to onActivityResult()
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOR_CAMERA && resultCode == RESULT_OK) {
            if(imageUri==null)
            {
                Toast.makeText(this, "Error taking photo.", Toast.LENGTH_SHORT).show();
                return;
            }
            Picasso.get().load(imageUri).into(postImage);
            return;
        }

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            imageUri = uri;
            Picasso.get().load(uri).into(postImage);
        }

    }

    public void AddMovie2(){

        FirebaseStorage storage= FirebaseStorage.getInstance();
        final String fileNameInStorage= UUID.randomUUID().toString();
        String path=fileNameInStorage+".jpg";
        final StorageReference imageRef=storage.getReference(path);
        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference movieRef = database.getReference("movieData");
                        DatabaseReference newMovieRef = movieRef.push();
                        newMovieRef.setValue(new newMovie(title.getText().toString(), year.getText().toString(),
                                        stars.getText().toString(), rating.getText().toString(),
                                        uri.toString(), length.getText().toString(),
                                        director.getText().toString(), description.getText().toString()))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(newMovieAdder.this, "Upload Success", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(newMovieAdder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(newMovieAdder.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void AddMovie(View view){
        AddMovie2();
        finish();
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


}
