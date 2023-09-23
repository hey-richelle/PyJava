package com.sys.system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class student_java extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_logo, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; show dialog to confirm going back to menu
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to go back to the menu?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(student_java.this, modules.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        finish();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private RecyclerView filesRecyclerView;
    private FilesAdapter6 filesAdapter;
    private DatabaseReference databaseRef;
    SearchView search;
    FloatingActionButton float23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_java);

        search = findViewById(R.id.et_search);
        getSupportActionBar().setTitle("Java Modules");
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));
        // Get a reference to the RecyclerView
        filesRecyclerView = findViewById(R.id.recyclerView2);
        float23 = findViewById(R.id.recyclerView4);
        // Set the layout manager for the RecyclerView
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the FilesAdapter with the data and context
        List<FileModel> fileList = new ArrayList<>();
        filesAdapter = new FilesAdapter6(this, fileList);

        // Set the adapter to the RecyclerView
        filesRecyclerView.setAdapter(filesAdapter);

        // Get a reference to the Firebase database
        databaseRef = FirebaseDatabase.getInstance().getReference("Java");

        // Add a listener to retrieve data from the Firebase database
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the existing list
                fileList.clear();

                // Loop through the data and add it to the list
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FileModel fileModel = dataSnapshot.getValue(FileModel.class);
                    fileList.add(fileModel);
                }

                // Notify the adapter of the data change
                filesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
            }
        });

        float23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Back to Modules",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),modules.class);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0); // Disable animation

            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<FileModel> filteredList = new ArrayList<>();

                for (FileModel file : fileList) {
                    if (file.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(file);
                    }
                }

                filesAdapter.filterList(filteredList);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(student_java.this, modules.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
        super.onBackPressed();
    }

}

