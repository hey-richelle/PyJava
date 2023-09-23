package com.sys.system;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class student_page extends AppCompatActivity {
    ImageView student_img;
    CardView modules, quiz, profile, message,forum;
    LottieAnimationView lottie1, lottie2, lottie3, lottie4;
    private Handler handler = new Handler();
    private Runnable updateTimeRunnable;
    private boolean isSoundPlayed;
    private String currentUserUid;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_logo, menu);
        return true;
    }

    private DatabaseReference chatRef;
    private DatabaseReference chatReference;
    private ValueEventListener chatListener;
    private int initialModulesVisibility;
    private int initialQuizVisibility;
    private int initialProfileVisibility;
    private int initialMessageVisibility;
    private int initialForumVisibility;
    private RelativeLayout cardContainer;
    private Banner banner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_page);
        quiz = findViewById(R.id.btn5);
        modules = findViewById(R.id.btn1);
        forum = findViewById(R.id.forum);
        profile = findViewById(R.id.btn6);
        message = findViewById(R.id.btn7);
        cardContainer = findViewById(R.id.card_container);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        banner = findViewById(R.id.banner);
        getSupportActionBar().setTitle("User Home Page");
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        initialModulesVisibility = modules.getVisibility();
        initialQuizVisibility = quiz.getVisibility();
        initialProfileVisibility = profile.getVisibility();
        initialMessageVisibility = message.getVisibility();
        initialForumVisibility = forum.getVisibility();
        TextView animate = findViewById(R.id.textstart);


        final List<Integer> images = new ArrayList<>();
        images.add(R.drawable.javabanner234);
        images.add(R.drawable.python234);

// Create an array of intents to launch when an image is clicked
        final Intent[] intents = new Intent[images.size()];

// Set up the Banner with image data
        banner.setAdapter(new ImageAdapter(images))
                .setIndicator(new CircleIndicator(this))
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(Object data, int position) {
                        // Handle banner item click events here
                        if (position < intents.length && intents[position] != null) {
                            startActivity(intents[position]);
                            overridePendingTransition(0,0);
                            finish();
                        }
                    }
                })
                .start();

// Populate the intents array with the activities you want to open
        intents[0] = new Intent(this, student_java.class);
        intents[1] = new Intent(this, python_student.class);
// Add more intents as needed for each image


        ValueAnimator animator = ValueAnimator.ofFloat(0f, 2000f);
        animator.setDuration(5000);
        animator.setStartDelay(200);
        animator.setInterpolator(new LinearInterpolator()); // Optional: Sets a linear interpolator for smooth animation.

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationX = (float) animation.getAnimatedValue();
                animate.setTranslationX(translationX);
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


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();

// Check the third item (index 2)
        MenuItem menuItem = menu.getItem(1);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(student_page.this);
                        builder.setTitle("Logout");
                        builder.setMessage("Are you sure you want to logout?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(student_page.this, "Logout", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getApplicationContext(), login.class);
                                overridePendingTransition(0, 0);
                                // Assuming you have a unique notification ID for "Java Easy"
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.private23: {
                        Toast.makeText(getApplicationContext(), "Closed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Privacy.class);
                        overridePendingTransition(0, 0); // Disable animation
                        startActivity(intent);
                        finish();

                        break;
                    }

                }
                return false;

            }
        });

        modules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), modules.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), announce_view2.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), category.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), profile.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), chat_view.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });


        DatabaseReference fullnameRef = database.getReference("ADMIN");

        // Add a ValueEventListener to listen for changes in the "messages" node
        fullnameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterate through each message in the dataSnapshot
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    // Get the necessary data from the message snapshot
                    String fullName = messageSnapshot.child("fullName").getValue(String.class);
                    // Call the showNotification() method to display the notification
                    showNotification(fullName, "");
                }
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



    private void centerCardContainer() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cardContainer.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        cardContainer.setLayoutParams(layoutParams);

    }


    private void updateCardVisibility(String query) {
        String lowercaseQuery = query.toLowerCase();
        forum.setVisibility(lowercaseQuery.contains("forum")?View.VISIBLE : View.GONE );
        modules.setVisibility(lowercaseQuery.contains("modules") ? View.VISIBLE : View.GONE);
        quiz.setVisibility(lowercaseQuery.contains("quiz") ? View.VISIBLE : View.GONE);
        profile.setVisibility(lowercaseQuery.contains("profile") ? View.VISIBLE : View.GONE);
        message.setVisibility(lowercaseQuery.contains("chat") ? View.VISIBLE : View.GONE);
    }

    private void restoreCardVisibility() {
        setCardVisibility(initialModulesVisibility, initialQuizVisibility, initialProfileVisibility, initialMessageVisibility,initialForumVisibility);
    }

    private void setCardVisibility(int modulesVisibility, int quizVisibility, int profileVisibility, int messageVisibility,int initialForumVisibility) {
        modules.setVisibility(modulesVisibility);
        quiz.setVisibility(quizVisibility);
        forum.setVisibility(initialForumVisibility);
        profile.setVisibility(profileVisibility);
        message.setVisibility(messageVisibility);
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
                    Intent intent = new Intent(getApplicationContext(), private_chat.class);
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
        isSoundPlayed = false;
    }

    private void showNotification(String fullName, String message) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getApplicationContext(), "Please log in again", Toast.LENGTH_SHORT).show();
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            // Start the login activity
            Intent intent = new Intent(getApplicationContext(), login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // Dismiss the progress dialog after starting the login activity
            progressDialog.dismiss();
            return;
        }

        DatabaseReference java1 = FirebaseDatabase.getInstance().getReference("Java_Easy");
        DatabaseReference java2 = FirebaseDatabase.getInstance().getReference("Java_normal");
        DatabaseReference java3 = FirebaseDatabase.getInstance().getReference("Java_hard");
        DatabaseReference pictureRef = FirebaseDatabase.getInstance().getReference("4pics");
        DatabaseReference python2 = FirebaseDatabase.getInstance().getReference("Python_Easy");
        DatabaseReference python3 = FirebaseDatabase.getInstance().getReference("Python_Normal");
        DatabaseReference python4 = FirebaseDatabase.getInstance().getReference("Python_Hard");

        // Create listeners for each node
        ValueEventListener easyListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = dataSnapshot.exists() ? (int) dataSnapshot.getChildrenCount() : 0;
                if (count > 0) {
                    displayNotification("Java Easy", message, fullName,"");
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred while accessing the database
            }
        };


        ValueEventListener python1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = dataSnapshot.exists() ? (int) dataSnapshot.getChildrenCount() : 0;
                if (count > 0) {
                    displayNotification("Python Easy", message, fullName,"");
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred while accessing the database
            }
        };


        ValueEventListener pythonnorms = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = dataSnapshot.exists() ? (int) dataSnapshot.getChildrenCount() : 0;
                if (count > 0) {
                    displayNotification("Python Normal", message, fullName,"");
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred while accessing the database
            }
        };


        ValueEventListener pythonHards = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = dataSnapshot.exists() ? (int) dataSnapshot.getChildrenCount() : 0;
                if (count > 0) {
                    displayNotification("Python Hard", message, fullName,"");
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred while accessing the database
            }
        };



        // Create listeners for each node
        ValueEventListener pictureListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = dataSnapshot.exists() ? (int) dataSnapshot.getChildrenCount() : 0;
                if (count > 0) {
                    displayNotification("4pics", message, fullName,"");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred while accessing the database
            }
        };

        ValueEventListener normalListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = dataSnapshot.exists() ? (int) dataSnapshot.getChildrenCount() : 0;
                if (count > 0) {
                    displayNotification("Java normal", message, fullName,"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred while accessing the database
            }
        };


        ValueEventListener hard2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = dataSnapshot.exists() ? (int) dataSnapshot.getChildrenCount() : 0;
                if (count > 0) {
                    displayNotification("Java hard", message, fullName,"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred while accessing the database
            }
        };


        ValueEventListener hardListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    int count = dataSnapshot.child(uid).exists() ? (int) dataSnapshot.child(uid).getChildrenCount() : 0;
                    if (count > 0) {
                        displayNotification("Java hard", message, fullName,"");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred while accessing the database
            }
        };


        // Attach the listeners to each node
        python2.addListenerForSingleValueEvent(python1);
        python3.addListenerForSingleValueEvent(pythonnorms);
        python4.addListenerForSingleValueEvent(pythonHards);
        java1.addListenerForSingleValueEvent(easyListener);
        java2.addListenerForSingleValueEvent(normalListener);
        java3.addListenerForSingleValueEvent(hard2);
        pictureRef.addListenerForSingleValueEvent(pictureListener);
    }

    private void displayNotification(String mode, String message, String fullName, String messageId) {
        // Check if the notification has already been seen by the user
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference notificationsSeenRef = FirebaseDatabase.getInstance().getReference("notifications_seen").child(currentUserUid).child(mode);
        notificationsSeenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // The notification for this mode has not been seen, display it

                    Intent intent = new Intent(getApplicationContext(), category.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                    String channel_id = "notification_channel_" + mode.toLowerCase().replace(" ", "_");

                    Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                            .setSmallIcon(R.drawable.logo_java)
                            .setContentTitle("New Quiz Challenge!!")
                            .setContentText("Created by : " + fullName)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setSound(soundUri)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText("Created by : " + fullName + message + "\n" + mode));

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(notificationChannel);
                    }

                    if (!isSoundPlayed) {
                        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), soundUri);
                        mediaPlayer.start();
                        isSoundPlayed = true;
                    }

                    notificationManager.notify(mode.hashCode(), builder.build());

                    // Add the notification ID to the "notifications_seen" node under the specific mode to mark it as seen
                    notificationsSeenRef.setValue(true);
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
}