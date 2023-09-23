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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class score_chart extends AppCompatActivity {
    ImageView userimg;
    TextView username;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_firemess, menu);
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
                        Intent intent = new Intent(score_chart.this, easy2.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
                return true;
            case R.id.action_refresh:
                // refresh button clicked; implement refresh logic here
                Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(score_chart.this, score_chart.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private TextView points;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_chart);


        getSupportActionBar().setTitle("My progress");

        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#606C5D")));
        username = findViewById(R.id.user_name);
        userimg = findViewById(R.id.user_img);
        points = findViewById(R.id.points);
        pieChart = findViewById(R.id.barChart);

        retrieveStudentDetails();
        retrieveScore();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();

// Check the third item (index 2)
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        // Handle Home item click
                        Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_LONG).show();
                        Intent homeIntent = new Intent(getApplicationContext(), student_page.class);
                        startActivity(homeIntent);
                        finish();
                        break;

                    case R.id.quiz:
                        // Handle Quiz item click
                        Toast.makeText(getApplicationContext(), "Quiz", Toast.LENGTH_LONG).show();
                        Intent quizIntent = new Intent(getApplicationContext(), easy2.class);
                        startActivity(quizIntent);
                        finish();
                        break;

                    case R.id.picture:
                        // Handle Profile item click
                        Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_LONG).show();
                        Intent profileIntent = new Intent(getApplicationContext(), profile.class);
                        startActivity(profileIntent);
                        finish();
                        break;
                }
                return true;
            }
        });


        DatabaseReference easymode = FirebaseDatabase.getInstance().getReference().child("Easy_mode");
        DatabaseReference normal = FirebaseDatabase.getInstance().getReference().child("Normal_mode");
        DatabaseReference hard = FirebaseDatabase.getInstance().getReference().child("Hard_mode");
        DatabaseReference picture = FirebaseDatabase.getInstance().getReference().child("4pics");

        easymode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long easyCount = dataSnapshot.exists() ? 1 : 0; // Check if "Easy_mode" node exists and set count accordingly

                normal.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long normalCount = dataSnapshot.exists() ? 1 : 0; // Check if "Normal_mode" node exists and set count accordingly

                        hard.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long hardCount = dataSnapshot.exists() ? 1 : 0; // Check if "Hard_mode" node exists and set count accordingly

                                picture.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        long pictureCounts = dataSnapshot.exists() ? 1 : 0; // Check if "Hard_mode" node exists and set count accordingly

                                        // Add a badge to the "Quiz" menu item
                                        MenuItem quizMenuItem = bottomNavigationView.getMenu().findItem(R.id.quiz);
                                        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(quizMenuItem.getItemId());
                                        badgeDrawable.setVisible(true);
                                        badgeDrawable.setNumber((int) (easyCount + normalCount + hardCount + pictureCounts));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle any errors that occur during the database operation
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors that occur during the database operation
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle any errors that occur during the database operation
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur during the database operation
            }
        });




    }





    private void retrieveStudentDetails() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("Student").child(userId);
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String usernameValue = dataSnapshot.child("name").getValue(String.class);
                    String imageUrl = dataSnapshot.child("image").getValue(String.class);

                    if (usernameValue != null) {
                        username.setText(usernameValue);
                    }

                    // Load the image into the ImageView using a library like Picasso or Glide
                    // For example, using Picasso:
                    if (imageUrl != null) {
                        Picasso.get().load(imageUrl).into(userimg);
                    }
                } else {
                    // Handle the case if student data does not exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if necessary
            }
        });
    }
    private void retrieveScore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference easyScoreRef = rootRef.child("easy_score");
        DatabaseReference normalScoreRef = rootRef.child("normal_score");
        DatabaseReference hardScoreRef = rootRef.child("hard_score");
        DatabaseReference picScoreRef = rootRef.child("picture_score");

        final int[] totalScore = {0};
        final int[] nodesRetrieved = {0};
        final List<Integer> scores = new ArrayList<>();  // Store the scores here

        ValueEventListener scoreListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int score = dataSnapshot.getValue(Integer.class);
                    scores.add(score);  // Store the score value
                    totalScore[0] += score;
                } else {
                    scores.add(0);  // If the score node doesn't exist, set it as 0
                }

                nodesRetrieved[0]++;

                if (nodesRetrieved[0] == 4) {
                    points.setText("My score = " + totalScore[0]);

                    // Refresh the chart
                    createPieChart(scores);  // Call a method to create the chart
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if necessary
            }
        };

        easyScoreRef.child(userId).addListenerForSingleValueEvent(scoreListener);
        normalScoreRef.child(userId).addListenerForSingleValueEvent(scoreListener);
        hardScoreRef.child(userId).addListenerForSingleValueEvent(scoreListener);
        picScoreRef.child(userId).addListenerForSingleValueEvent(scoreListener);
    }

    private void createPieChart(List<Integer> scores) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // Add the scores to the PieEntry list
        for (int i = 0; i < scores.size(); i++) {
            entries.add(new PieEntry(scores.get(i), getCategoryLabel(i)));  // Get the category label based on index
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        // Define an array of colors for the pie slices
        int[] colors = new int[]{Color.parseColor("#A1CCD1"), // blue
                Color.parseColor("#1A5D1A"), // green
                Color.parseColor("#FFD966"), // yellow
                Color.parseColor("#FD8A8A")}; // red
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.setHoleRadius(35f); // Set the hole radius to a non-zero value (e.g., 35f)
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.getDescription().setEnabled(false);

        // Set the center text for the pie chart
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Progress");
        pieChart.setCenterTextSize(18f);
        pieChart.setCenterTextColor(Color.BLACK);

        pieChart.animateXY(1400, 1400);
        pieChart.invalidate();
    }

    private String getCategoryLabel(int index) {
        switch (index) {
            case 0:
                return "Easy";
            case 1:
                return "Normal";
            case 2:
                return "Hard";
            case 3:
                return "Picture";
            default:
                return "";  // Handle the case where there is no label available
        }
    }
}