package com.sys.system;

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
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class chat_view2 extends AppCompatActivity {
    private FloatingActionButton float23;
    private String currentUserEmail;
    private ListView memberListView;
    private List<Message2> memberList; // Assuming you are using Message2 objects for both Admin_login and DB_Member
    private CustomAdapter2 adapter;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private List<Message2> originalMemberList;
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
        setContentView(R.layout.activity_chat_view2);
        float23 = findViewById(R.id.bottom_navigation);
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
        adapter = new CustomAdapter2(this, memberList);
        memberListView.setAdapter(adapter);
        // Fetch and populate the memberList with data from both "Admin_login" and "DB_Member"
        //retrieveAdminDataFromFirebase();
        retrieveDBMemberDataFromFirebase();
        SearchView searchView = findViewById(R.id.et_search);


        float23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Admin_page.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Disable animation
                finish();
            }
        });

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

    }






    private void filterMemberList(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            // If the search text is empty, restore the original data
            adapter.updateList(originalMemberList);
        } else {
            // Convert the search query to lowercase or uppercase for case-insensitive comparison
            String searchQuery = searchText.toLowerCase(); // or toUpperCase()

            List<Message2> filteredList = new ArrayList<>();
            for (Message2 member : memberList) {
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
  //  private void retrieveAdminDataFromFirebase() {
      //  DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");

       // adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
       //     @Override
         //   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
         //       mergeAdminDataFromSnapshot(dataSnapshot);
          //  }

        //    @Override
          //  public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
         //   }
       // });
   // }

    private void retrieveDBMemberDataFromFirebase() {
        DatabaseReference memberRef = FirebaseDatabase.getInstance().getReference("Student");

        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mergeDBMemberDataFromSnapshot(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }

   // private void mergeAdminDataFromSnapshot(DataSnapshot dataSnapshot) {
     //   for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {
           // String email = adminSnapshot.child("email").getValue(String.class);
          //  String imageUrl = adminSnapshot.child("imageUrl").getValue(String.class);
          //  String fullName = adminSnapshot.child("fullName").getValue(String.class);

            // Check if the current user's email matches the admin's email
          //  if (!TextUtils.equals(email, currentUserEmail)) {
                // Create a Message2 object and add it to the memberList
              //  Message2 message2 = new Message2(null, null, fullName, null, null, null, null, imageUrl, null, false, email);
               // memberList.add(message2);
              //  originalMemberList.clear();
               // originalMemberList.addAll(memberList);
          //  }
      //  }

        // Notify the adapter about the data changes
      //  adapter.notifyDataSetChanged();
 //   }

    private void mergeDBMemberDataFromSnapshot(DataSnapshot dataSnapshot) {
        for (DataSnapshot dbMemberSnapshot : dataSnapshot.getChildren()) {
            String email = dbMemberSnapshot.child("email").getValue(String.class);
            String imageUrl = dbMemberSnapshot.child("image").getValue(String.class);
            String profession = dbMemberSnapshot.child("username").getValue(String.class);
            String fullName = dbMemberSnapshot.child("name").getValue(String.class);

            // Check if the current user's email matches the DBMember's email
            if (!TextUtils.equals(email, currentUserEmail)) {
                // Create a Message2 object and add it to the memberList
                Message2 message2 = new Message2(null, null, fullName, profession, null, null, null, imageUrl, null, false, email);
                memberList.add(message2);
                originalMemberList.clear();
                originalMemberList.addAll(memberList);
            }
        }

        // Notify the adapter about the data changes
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(chat_view2.this, Admin_page.class);
        startActivity(intent);
        overridePendingTransition(0, 0); // Disable animation
        super.onBackPressed();
    }

}
