package com.sys.system;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Admin_page extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    CardView archives2,java23,python23,message23,ahas,forum;
    TextView archives3;
    TextView java56,pyton56,py;
    private boolean isSoundPlayed;
    LottieAnimationView archive,java,python,message;
    private TextView greetingTextView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_logo, menu);
        return true;
    }
    ImageView imageView2;

    String[] position = {"Upload from subjects", "Java", "Python"};
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Banner banner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        greetingTextView = findViewById(R.id.tv_morning);
        drawerLayout = findViewById(R.id.drawer_layout);
        message23 = findViewById(R.id.arjay55);
        archives2 = findViewById(R.id.archives);
        python23 = findViewById(R.id.arjay33);
        java23 = findViewById(R.id.arjay31);
        message = findViewById(R.id.message_lottie);
        archives3 = findViewById(R.id.archive_text);
        pyton56 = findViewById(R.id.python);
        py = findViewById(R.id.archive_py);
        forum = findViewById(R.id.arjay56);
        java56 = findViewById(R.id.java);
        archive = findViewById(R.id.archive_lottie);
        python = findViewById(R.id.python_lottie);
        java = findViewById(R.id.java_lottie);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        getSupportActionBar().setTitle("Admin Home Page");
// Set the color of the title to black
        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle.syncState();
        DrawerArrowDrawable toggleDrawable = drawerToggle.getDrawerArrowDrawable();
        toggleDrawable.setColor(Color.BLACK);
        ahas = findViewById(R.id.python_ahas);
        banner = findViewById(R.id.banner);
        Spinner spin = findViewById(R.id.spinnerposition); //spinner is spinner id
        spin.setOnItemSelectedListener(this);

        //arrayadapter (set position data to arrayadapter)
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, position);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //set array adapter to fill spinner
        spin.setAdapter(aa);
        updateGreeting();
        archive.animate().setStartDelay(300).setDuration(1000);
        python.animate().setStartDelay(300).setDuration(1000);
        java.animate().setStartDelay(300).setDuration(1000);
        message.animate().setStartDelay(300).setDuration(1000);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // DatabaseReference privateChatsRef = database.getReference("private_chats").child("user1");
//
// privateChatsRef.addChildEventListener(new ChildEventListener() {
//     @Override
//     public void onChildAdded(@NonNull DataSnapshot chatSnapshot, @Nullable String previousChildName) {
//         for (DataSnapshot messageSnapshot : chatSnapshot.getChildren()) {
//             if (messageSnapshot.exists()) {
//                 ChatMessage chatMessage = messageSnapshot.getValue(ChatMessage.class);
//                 if (chatMessage != null) {
//                     String Sender = chatMessage.getSender();
//                     String message = chatMessage.getMessage();
//                     String messageId = chatMessage.getMessageId();
//
//                     showNotification2(message, Sender, messageId);
//                 }
//             }
//         }
//     }
//
//     @Override
//     public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//         // Handle child data changes if needed
//     }
//
//     @Override
//     public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//         // Handle child removal if needed
//     }
//
//     @Override
//     public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//         // Handle child movement if needed
//     }
//
//     @Override
//     public void onCancelled(@NonNull DatabaseError databaseError) {
//         // Handle any errors that occur during the database operation
//     }
// });


        ValueAnimator animator = ValueAnimator.ofFloat(0f, 2000f);
        animator.setDuration(5000);
        animator.setStartDelay(200);
        animator.setInterpolator(new LinearInterpolator()); // Optional: Sets a linear interpolator for smooth animation.

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationX = (float) animation.getAnimatedValue();
                greetingTextView.setTranslationX(translationX);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Restart the animation when it completes
                animator.start();
            }
        });

// Start the animation
        animator.start();

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.javabanner234);
        images.add(R.drawable.python234);

        // Set up the Banner with image data
        banner.setAdapter(new ImageAdapter(images))
                .setIndicator(new CircleIndicator(this))
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(Object data, int position) {
                        // Handle banner item click events here
                    }
                })
                .start();


        ahas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), archive2.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Disable animation
                finish();
            }
        });

      forum.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(getApplicationContext(), announce_view.class);
              startActivity(intent);
              overridePendingTransition(0, 0); // Disable animation
              finish();
          }
      });

        message23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), chat_view2.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Disable animation
                finish();
            }
        });


        archives2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), archive.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Disable animation
                finish();
            }
        });

        java23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), jav_module.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Disable animation
                finish();
            }
        });


        python23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), python_module.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Disable animation
                finish();
            }
        });


        //archives
        DatabaseReference archive = FirebaseDatabase.getInstance().getReference().child("Archive_modules");
        final int[] archive_count = {0};
        archive.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                archive_count[0] = (int) dataSnapshot.getChildrenCount();
                archives3.setText(String.valueOf(archive_count[0]));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });




        //archives
        DatabaseReference archive2 = FirebaseDatabase.getInstance().getReference().child("Archive_modules2");
        final int[] archive_count2 = {0};
        archive2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                archive_count2[0] = (int) dataSnapshot.getChildrenCount();
                py.setText(String.valueOf(archive_count2[0]));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });

        DatabaseReference java = FirebaseDatabase.getInstance().getReference().child("Java");
        final int[] java_count = {0};
        java.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                java_count[0] = (int) dataSnapshot.getChildrenCount();
                java56.setText(String.valueOf(java_count[0]));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });





        DatabaseReference Pyhton = FirebaseDatabase.getInstance().getReference().child("Python");
        final int[] python_count = {0};
        Pyhton.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                python_count[0] = (int) dataSnapshot.getChildrenCount();
                pyton56.setText(String.valueOf(python_count[0]));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home: {
                        Toast.makeText(Admin_page.this, "Closed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Admin_page.class);
                        overridePendingTransition(0, 0); // Disable animation
                        startActivity(intent);
                        finish();

                        break;
                    }



                    case R.id.QuizItems: {
                        Toast.makeText(Admin_page.this, "reference", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Reference2.class);
                        overridePendingTransition(0, 0); // Disable animation
                        startActivity(intent);
                        finish();

                        break;
                    }


                    case R.id.archive: {
                        Toast.makeText(Admin_page.this, "Archive quiz", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Archive_quiz.class);
                        overridePendingTransition(0, 0); // Disable animation
                        startActivity(intent);
                        finish();

                        break;
                    }


                    case R.id.update: {
                        Toast.makeText(Admin_page.this, "Update Admin", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), admin_update.class);
                        overridePendingTransition(0, 0); // Disable animation
                        startActivity(intent);
                        finish();
                        break;
                    }


                    case R.id.creation: {
                        Toast.makeText(Admin_page.this, "create admin", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), admin_create.class);
                        overridePendingTransition(0, 0); // Disable animation
                        startActivity(intent);
                        finish();
                        break;
                    }




                    case R.id.logout: {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Admin_page.this);
                        builder.setTitle("Logout");
                        builder.setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(Admin_page.this, "Logout", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), login.class);
                                startActivity(intent);
                                clearNotification2();
                                FirebaseAuth.getInstance().signOut();
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

                }
                return false;

            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
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

    }

    private void updateGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greetingText;

        if (hour >= 4 && hour < 12) {
            greetingText = "Good Morning !";
        } else if (hour >= 12 && hour < 18) {
            greetingText = "Good Afternoon !";
        } else {
            greetingText = "Good Evening !";
        }

        greetingTextView.setText(greetingText);
    }

    private void clearNotification2() {
        // Create an object of NotificationManager class to notify the user
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
    private void showNotification2(String messageText, String Sender, String messageId) {
        // Check if the notification has already been seen by the user
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference notificationsSeenRef = FirebaseDatabase.getInstance().getReference("notifications_seen").child(currentUserUid);
        notificationsSeenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(messageId)) {
                    // The notification has not been seen, display it

                    // Pass the intent to switch to the MainActivity
                    Intent intent = new Intent(getApplicationContext(), chat_view2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                    // Assign channel ID
                    String channel_id = "notification_channel";

                    // Set the custom sound for the notification
                    Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound);

                    // Create a Builder object using NotificationCompat class
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                            .setSmallIcon(R.drawable.logo_java)
                            .setContentTitle(Sender)
                            .setContentText(messageText)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setSound(soundUri);
                    // Create an object of NotificationManager class to notify the user
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    // Check if the Android version is greater than or equal to Oreo
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(notificationChannel);
                    }

                    if (!isSoundPlayed) {
                        // Create a MediaPlayer object
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), soundUri);

                        // Start playing the custom sound
                        mediaPlayer.start();

                        // Set the flag to indicate that the sound has been played
                        isSoundPlayed = true;
                    }

                    // Display the notification
                    notificationManager.notify(0, builder.build());

                    // Add the notification ID to the "notifications_seen" node to mark it as seen
                    notificationsSeenRef.child(messageId).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred while accessing the database
            }
        });
    }




    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position).equals("Java")) {
            Toast.makeText(getApplicationContext(), "Java module", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(),java.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Disable animation
            finish();

        } else if (parent.getItemAtPosition(position).equals("Python")) {

            Toast.makeText(getApplicationContext(), "Python Module", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(),python.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Disable animation
            finish();

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onPause() {

        super.onPause();
        super.onResume();

    }
}