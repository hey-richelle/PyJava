package com.sys.system;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class profile extends AppCompatActivity {

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
                        Intent intent = new Intent(getApplicationContext(), student_page.class);
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


    private TextView fullNameTextView;
    private TextView usernameTextView;
    private TextView emailTextView;
    private ImageView imageView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        button = findViewById(R.id.btn23);


        // Set the title of the action bar
        getSupportActionBar().setTitle("User Profile");

        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));

        imageView = findViewById(R.id.student_img);
        fullNameTextView = findViewById(R.id.fullname);
        usernameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);

        retrieveStudentDetails();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), profile_update.class);

                // Pass the retrieved data as extras
                intent.putExtra("name", fullNameTextView.getText().toString());
                intent.putExtra("username", usernameTextView.getText().toString());
                intent.putExtra("email", emailTextView.getText().toString());

                // Pass the image URL as an extra
                intent.putExtra("imageUrl", (String) imageView.getTag());

                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();

// Check the third item (index 2)
        MenuItem menuItem = menu.getItem(2);
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
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                    case R.id.quiz:
                        // Handle Quiz item click
                        Toast.makeText(getApplicationContext(), "Quiz", Toast.LENGTH_LONG).show();
                        Intent quizIntent = new Intent(getApplicationContext(), category.class);
                        startActivity(quizIntent);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                    case R.id.picture:
                        // Handle Profile item click
                        AlertDialog.Builder builder = new AlertDialog.Builder(profile.this);
                        builder.setTitle("Logout");
                        builder.setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(profile.this, "Logout", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getApplicationContext(), login.class);
                                overridePendingTransition(0, 0);
                                int javaNormalNotificationId = "Java normal".hashCode();
                                int javaHardNotificationId = "Java hard".hashCode();
                                int javaEasyNotificationId = "Java Easy".hashCode();
                                int javaPython1NotificationId = "Python Easy".hashCode();
                                int javaPython2NotificationId = "Python Normal".hashCode();
                                int javaPython3NotificationId = "Python Hard".hashCode();
                                cancelNotification(javaPython1NotificationId);
                                cancelNotification(javaPython2NotificationId);
                                cancelNotification(javaPython3NotificationId);
                                cancelNotification(javaHardNotificationId);
                                cancelNotification(javaNormalNotificationId);
                                cancelNotification(javaEasyNotificationId);
                                startActivity(intent);
                                finish();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                            }
                        });
                        builder.show();
                        break;

                }
                return true;
            }
        });



        DatabaseReference easymode = FirebaseDatabase.getInstance().getReference().child("Java_Easy");
        DatabaseReference normal = FirebaseDatabase.getInstance().getReference().child("Java_normal");
        DatabaseReference hard = FirebaseDatabase.getInstance().getReference().child("Java_hard");

        DatabaseReference python1 = FirebaseDatabase.getInstance().getReference().child("Python_Easy");
        DatabaseReference python2 = FirebaseDatabase.getInstance().getReference().child("Python_Normal");
        DatabaseReference python3 = FirebaseDatabase.getInstance().getReference().child("Python_Hard");

        DatabaseReference picture = FirebaseDatabase.getInstance().getReference().child("4pics");

        easymode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long easyCount = dataSnapshot.exists() ? 1 : 0; // Check if "Java_Easy" node exists and set count accordingly

                normal.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long normalCount = dataSnapshot.exists() ? 1 : 0; // Check if "Java_normal" node exists and set count accordingly

                        hard.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long hardCount = dataSnapshot.exists() ? 1 : 0; // Check if "Java_hard" node exists and set count accordingly

                                python1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        long python1Count = dataSnapshot.exists() ? 1 : 0; // Check if "Python_Easy" node exists and set count accordingly

                                        python2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                long python2Count = dataSnapshot.exists() ? 1 : 0; // Check if "Python_normal" node exists and set count accordingly

                                                python3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        long python3Count = dataSnapshot.exists() ? 1 : 0; // Check if "Python_hard" node exists and set count accordingly

                                                        picture.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                long pictureCounts = dataSnapshot.exists() ? 1 : 0; // Check if "4pics" node exists and set count accordingly

                                                                // Calculate the total count for all categories and add a badge
                                                                int totalCount = (int) (easyCount + normalCount + hardCount + python1Count + python2Count + python3Count + pictureCounts);

                                                                // Add a badge to the "Quiz" menu item
                                                                MenuItem quizMenuItem = bottomNavigationView.getMenu().findItem(R.id.quiz);
                                                                BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(quizMenuItem.getItemId());
                                                                badgeDrawable.setVisible(true);
                                                                badgeDrawable.setNumber(totalCount);
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
                    String fullName = dataSnapshot.child("name").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String imageUrl = dataSnapshot.child("image").getValue(String.class);

                    fullNameTextView.setText(fullName);
                    usernameTextView.setText(username);
                    emailTextView.setText(email);

                    // Set the image URL to the ImageView's tag for later retrieval
                    imageView.setTag(imageUrl);

                    // Load the image into the ImageView using a library like Picasso or Glide
                    // For example, using Picasso:
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get().load(imageUrl).into(imageView);
                    } else {
                        // Set a default drawable image if the image URL is empty
                        imageView.setImageResource(R.drawable.ic_baseline_person_24);
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

    private void cancelNotification(int notificationId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }



    private void clearNotification2() {
        // Create an object of NotificationManager class to notify the user
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    private void clearNotification() {
        // Create a NotificationManager instance
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Cancel the notification by its unique identifier
        notificationManager.cancel("Java_Easy".hashCode());
        notificationManager.cancel("Java_hard".hashCode());
        notificationManager.cancel("Java_normal".hashCode());
        notificationManager.cancel("Python_Easy".hashCode());
        notificationManager.cancel("Python_Normal".hashCode());
        notificationManager.cancel("Python_hard".hashCode());
        notificationManager.cancel("4pics".hashCode());

        // Stop playing the sound

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(profile.this, student_page.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        super.onBackPressed();
    }
}