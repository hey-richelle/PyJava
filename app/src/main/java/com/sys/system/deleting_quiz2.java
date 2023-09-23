package com.sys.system;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class deleting_quiz2 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QuizAdapter10 quizAdapter;
    private List<QuizItem> quizItemList;
    private List<String> nodeNames;
    private List<Integer> nodeCounts;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_logo, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleting_quiz2);
        getSupportActionBar().setTitle("Python quiz Management");
        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));
        recyclerView = findViewById(R.id.quiz);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Assuming you have already initialized recyclerView and other required components
        Context context = this;

        // Assuming you have already initialized recyclerView and other required components
        quizItemList = new ArrayList<>();
        nodeNames = new ArrayList<>();
        nodeCounts = new ArrayList<>();
        quizAdapter = new QuizAdapter10(context, quizItemList, nodeNames, nodeCounts);
        recyclerView.setAdapter(quizAdapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();

// Check the third item (index 2)
        MenuItem menuItem = menu.getItem(2);
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



        //  references from the nodes

        DatabaseReference easyRef = FirebaseDatabase.getInstance().getReference("Python_Easy");
        DatabaseReference hard  = FirebaseDatabase.getInstance().getReference("Python_Hard");
        DatabaseReference normalRef = FirebaseDatabase.getInstance().getReference("Python_Normal");

        // Add a ValueEventListener for each node separately to retrieve data from Firebase
        ValueEventListener easyListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                nodeCounts.add(count);
                nodeNames.add("Python_Easy");
                updateAdapterData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching data from Firebase: " + databaseError.getMessage());
            }
        };

        ValueEventListener normalListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                nodeCounts.add(count);
                nodeNames.add("Python_Normal");
                updateAdapterData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching data from Firebase: " + databaseError.getMessage());
            }
        };

        ValueEventListener hard_java = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                nodeCounts.add(count);
                nodeNames.add("Python_Hard");
                updateAdapterData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching data from Firebase: " + databaseError.getMessage());
            }
        };



        // Add the listeners to the database references
        easyRef.addValueEventListener(easyListener);
        normalRef.addValueEventListener(normalListener);
        hard.addValueEventListener(hard_java);
    }

    private void updateAdapterData() {
        // Check if all data is retrieved for all nodes
        if (nodeCounts.size() == 3) { // Assuming you have four nodes: Easy_mode, Normal_mode, Hard_mode, and 4pics
            // Clear the list before adding new items
            quizItemList.clear();
            // Add a dummy QuizItem for each node (you can modify this according to your actual data structure)
            for (int i = 0; i < nodeCounts.size(); i++) {
                quizItemList.add(new QuizItem());
            }
            quizAdapter.notifyDataSetChanged(); // Notify the adapter that data has changed
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),Quiz_manager.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
