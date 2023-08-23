package com.example.project3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final int REQUEST_FOR_CAMERA=0011;
    private static final int OPEN_FILE=0012;
    private Uri imageUri=null;
    private MyRecyclerAdapter myRecyclerAdapter;
    private HashMap<String, movie> key_to_Movie = null;

    public HomeActivity(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar myToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolBar);

        // Initialize Firebase Auth

        RecyclerView recyclerView=findViewById(R.id.recylcer_view);
        myRecyclerAdapter=new MyRecyclerAdapter(recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myRecyclerAdapter);
        myRecyclerAdapter.setOnListItemClickListener(new OnListItemClickListener(){
            @Override
            public void onItemClick(View v, int position) {


                movie movieMap = myRecyclerAdapter.getItem(position);
                Intent intent = new Intent(getBaseContext(), MainMovieViewer.class);
                intent.putExtra("title", movieMap.title);
                intent.putExtra("picture", movieMap.picture);
                intent.putExtra("year", movieMap.year);
                intent.putExtra("length", movieMap.length);
                intent.putExtra("director", movieMap.director);
                intent.putExtra("stars", movieMap.stars);
                intent.putExtra("rating", movieMap.rating);
                intent.putExtra("description", movieMap.description);
                startActivity(intent);

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast toast = Toast.makeText(getApplication(), "Query Text=" + query, Toast.LENGTH_SHORT);
                toast.show();
                myRecyclerAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myRecyclerAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.signout:
                mAuth.signOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        myRecyclerAdapter.removeListener();
    }


    public void newMovie(View view){

        startActivity(new Intent(this, newMovieAdder.class));

    }

}

