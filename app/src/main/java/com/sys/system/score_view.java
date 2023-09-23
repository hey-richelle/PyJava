package com.sys.system;

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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class score_view extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private DatabaseReference pythonScoreRef;
    FloatingActionButton fab23;
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
                        Intent intent = new Intent(getApplicationContext(),Admin_page.class);
                        startActivity(intent);
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
    private DatabaseReference studentRef;
    private DatabaseReference easyScoreRef;
    private DatabaseReference normalScoreRef;
    private DatabaseReference hardScoreRef;
    private ListView memberListView;
    private ArrayList<Student> studentList;
    private ArrayList<Student> filteredStudentList;
    private StudentAdapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_view);
        // Get the home button drawable
        getSupportActionBar().setTitle("Java User Progress");
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        studentRef = firebaseDatabase.getReference("Student");
        easyScoreRef = firebaseDatabase.getReference("Java_Easy_scores");
        normalScoreRef = firebaseDatabase.getReference("Java_normal_score");
        hardScoreRef = firebaseDatabase.getReference("Java_hard_score");
        fab23 = findViewById(R.id.fab);

        // Initialize views
        memberListView = findViewById(R.id.memberListView);
        searchView = findViewById(R.id.et_search);
        searchView.setOnQueryTextListener(this);

        // Initialize student list and adapter
        studentList = new ArrayList<>();
        filteredStudentList = new ArrayList<>();
        adapter = new StudentAdapter(this, filteredStudentList);
        memberListView.setAdapter(adapter);



        fab23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "System Analysis", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),System_analysis.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0); // Disable animation
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();

// Check the third item (index 2)
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;

                switch (item.getItemId()) {
                    case R.id.home:
                        Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_LONG).show();
                        intent = new Intent(getApplicationContext(), Admin_page.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0); // Disable animation
                        break;
                    case R.id.quiz:
                        Toast.makeText(getApplicationContext(), "Create Quiz", Toast.LENGTH_LONG).show();
                        intent = new Intent(getApplicationContext(), quiz_category.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0); // Disable animation
                        break;
                    case R.id.picture:
                        Toast.makeText(getApplicationContext(), "Delete Quiz", Toast.LENGTH_LONG).show();
                        intent = new Intent(getApplicationContext(), Quiz_manager.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0); // Disable animation
                        break;
                    case R.id.progress23:
                        Toast.makeText(getApplicationContext(), "Student progress", Toast.LENGTH_LONG).show();
                        intent = new Intent(getApplicationContext(), User_progress.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0); // Disable animation
                        break;
                }

                return true;
            }
        });



        // Retrieve student data from Firebase
        studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String studentId = snapshot.getKey();
                    String imageUrl = snapshot.child("image").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    Student student = new Student(studentId, imageUrl, name, 0);
                    studentList.add(student);
                }
                applySearch("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

        // Retrieve score data from Firebase - Easy
        easyScoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateScores(dataSnapshot, "Java_Easy_scores");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

        // Retrieve score data from Firebase - Normal
        normalScoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateScores(dataSnapshot, "Java_normal_score");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

        // Retrieve score data from Firebase - Hard
        hardScoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateScores(dataSnapshot, "Java_hard_score");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

        ;
    }

    private void updateScores(DataSnapshot dataSnapshot, String scoreType) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String studentId = snapshot.getKey();
            int score = snapshot.getValue(Integer.class);
            for (Student student : studentList) {
                if (student.getId().equals(studentId)) {
                    switch (scoreType) {
                        case "Java_Easy_scores":
                            student.setEasyScore(score);
                            break;
                        case "Java_normal_score":
                            student.setNormalScore(score);
                            break;
                        case "Java_hard_score":
                            student.setHardScore(score);
                            break;

                    }
                    break;
                }
            }
        }
        applySearch(searchView.getQuery().toString()); // Apply search filter after scores update
    }

    private void applySearch(String query) {
        if (query == null || query.isEmpty()) {
            filteredStudentList.clear();
            filteredStudentList.addAll(studentList);
        } else {
            String filterPattern = query.toLowerCase().trim();
            filteredStudentList.clear();
            for (Student student : studentList) {
                if (student.getName().toLowerCase().contains(filterPattern)) {
                    filteredStudentList.add(student);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        applySearch(newText);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),Admin_page.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
