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
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class chat_view extends AppCompatActivity {
    private String currentUserEmail;
    private ListView memberListView;
    private List<Message3> memberList; // Assuming you are using Message2 objects for both Admin_login and DB_Member
    private CustomAdapter adapter;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private List<Message3> originalMemberList;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_logo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        getSupportActionBar().setTitle("Private chat");
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
// Set its color to black
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
// Set the modified drawable as the home button icon
        getSupportActionBar().setHomeAsUpIndicator(homeButton);
        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserEmail = currentUser.getEmail();
        }

        // Initialize ListView and List to store data
        originalMemberList = new ArrayList<>();
        memberListView = findViewById(R.id.memberListView);
        memberList = new ArrayList<>();
        adapter = new CustomAdapter(this, memberList);
        memberListView.setAdapter(adapter);
        // Fetch and populate the memberList with data from both "Admin_login" and "DB_Member"
        retrieveAdminDataFromFirebase();
        // retrieveDBMemberDataFromFirebase();
        SearchView searchView = findViewById(R.id.et_search);

        // Add a listener to the SearchView to perform filtering
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the memberList based on the search query
                filterMemberList(newText);
                return true;
            }
        });

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(chat_view.this);
                        builder.setTitle("Logout");
                        builder.setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(chat_view.this, "Logout", Toast.LENGTH_SHORT).show();
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

    private void filterMemberList(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            // If the search text is empty, restore the original data
            adapter.updateList(originalMemberList);
        } else {
            // Convert the search query to lowercase or uppercase for case-insensitive comparison
            String searchQuery = searchText.toLowerCase(); // or toUpperCase()

            List<Message3> filteredList = new ArrayList<>();
            for (Message3 member : memberList) {
                // Perform filtering based on your criteria
                // For example, you can search by email, full name, profession, etc.

                // Convert the fields to lowercase or uppercase for case-insensitive comparison
                String email = member.getEmail() != null ? member.getEmail().toLowerCase() : "";
                String fullName = member.getFullName() != null ? member.getFullName().toLowerCase() : "";
                String profession = member.getProfession() != null ? member.getProfession().toLowerCase() : "";

                if (email.contains(searchQuery) ||
                        fullName.contains(searchQuery) ||
                        profession.contains(searchQuery)) {
                    filteredList.add(member);
                }
            }

            // Update the adapter with the filtered list
            adapter.updateList(filteredList);
        }

    }



    // ... (other methods)
    private void retrieveAdminDataFromFirebase() {
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mergeAdminDataFromSnapshot(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }


    private void mergeAdminDataFromSnapshot(DataSnapshot dataSnapshot) {
        for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {
            // Create a Message2 object directly from the adminSnapshot
            Message3 message2 = adminSnapshot.getValue(Message3.class);

            if (message2 != null) {
                String email = message2.getEmail();

                // Check if the current user's email matches the admin's email
                if (!TextUtils.equals(email, currentUserEmail)) {
                    // Add the Message2 object to the memberList
                    memberList.add(message2);
                    originalMemberList.clear();
                    originalMemberList.addAll(memberList);
                }
            }
        }

        // Notify the adapter about the data changes
        adapter.notifyDataSetChanged();
    }


    // private void retrieveDBMemberDataFromFirebase() {
    //  DatabaseReference memberRef = FirebaseDatabase.getInstance().getReference("Student");

    // memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
    //    @Override
    //   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    //  mergeDBMemberDataFromSnapshot(dataSnapshot);
    //   }

    //  @Override
    // public void onCancelled(@NonNull DatabaseError databaseError) {
    // Handle any errors
    //   }
    // });
    //  }



    // private void mergeDBMemberDataFromSnapshot(DataSnapshot dataSnapshot) {
     //   for (DataSnapshot dbMemberSnapshot : dataSnapshot.getChildren()) {
      //      String email = dbMemberSnapshot.child("email").getValue(String.class);
        //    String imageUrl = dbMemberSnapshot.child("image").getValue(String.class);
       //     String profession = dbMemberSnapshot.child("username").getValue(String.class);
        //    String fullName = dbMemberSnapshot.child("name").getValue(String.class);

            // Check if the current user's email matches the DBMember's email
         //   if (!TextUtils.equals(email, currentUserEmail)) {
                // Create a Message2 object and add it to the memberList
           //     Message2 message2 = new Message2(null, null, fullName, profession, null, null, null, imageUrl, null, false, email);
             //   memberList.add(message2);
            //    originalMemberList.clear();
            //    originalMemberList.addAll(memberList);
          //  }
       // }

        // Notify the adapter about the data changes
      //  adapter.notifyDataSetChanged();
 //   }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(chat_view.this, student_page.class);
        startActivity(intent);
        overridePendingTransition(0,0);
        super.onBackPressed();
    }
}




