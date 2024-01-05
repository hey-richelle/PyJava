package com.sys.system.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.sys.system.Class.Admin3;
import com.sys.system.Class.Comment;
import com.sys.system.Class.CommentAdapter;
import com.sys.system.R;
import com.sys.system.Class.Students;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommentActivity2 extends AppCompatActivity {
    ImageButton picture, sendBtn;
    EditText commentEditText;
    RecyclerView commentsRecyclerView;
    private Uri selectedImageUri;
    private DatabaseReference commentsRef;
    private DatabaseReference studentsRef; // Firebase reference for Students
    private DatabaseReference adminRef;    // Firebase reference for Admin3
    private FirebaseUser currentUser;
    private CommentAdapter commentAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
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
                        Intent intent = new Intent(getApplicationContext(), announce_view2.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        finish();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
                return true;
            case R.id.action_refresh:
                // refresh button clicked; implement refresh logic here
                Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), CommentActivity2.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment2);

        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
// Set its color to black
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
// Set the modified drawable as the home button icon
        getSupportActionBar().setHomeAsUpIndicator(homeButton);
        getSupportActionBar().setTitle("Community Forum");
        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));

        sendBtn = findViewById(R.id.sendMessageButton);
        commentEditText = findViewById(R.id.messageEditText);
        commentsRecyclerView = findViewById(R.id.recycler);

        // Initialize Firebase references
        studentsRef = FirebaseDatabase.getInstance().getReference("Student");
        adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize RecyclerView adapter and set it to the RecyclerView
        commentAdapter = new CommentAdapter(new ArrayList<>()); // Initialize with an empty list
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);

        // Retrieve announcement details from the Intent extras
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String time = intent.getStringExtra("time");
        String date = intent.getStringExtra("date");
        String fullName = intent.getStringExtra("fullName");
        String imageUrl = intent.getStringExtra("imageUrl");
        String announcementId = intent.getStringExtra("announcementId");
        commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(announcementId);

        // Display announcement details in the CommentActivity layout
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView contentTextView = findViewById(R.id.contentTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView fullNameTextView = findViewById(R.id.fullNameTextView);
        ImageView imageView = findViewById(R.id.imageView);

        titleTextView.setText(title);
        contentTextView.setText(content);
        timeTextView.setText(time);
        dateTextView.setText(date);
        fullNameTextView.setText("Upload by: "+fullName);

        // Load and display the image using Picasso
        if (imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder);
        }


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePopup(imageUrl); // Pass the image URL to the method
            }
        });


        picture = findViewById(R.id.buttonImage);
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity2.this);
                builder.setMessage("Are you sure you want to select picture to upload?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                openImagePicker();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // Set an OnClickListener for the send button to add a new comment
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the EditText
                String messageText = commentEditText.getText().toString().trim();

                // Check if the message is not empty
                if (!messageText.isEmpty()) {
                    // Generate a unique comment ID
                    String commentId = commentsRef.push().getKey();

                    // Get the current user's UID (assuming you are using Firebase Authentication)
                    String userId = currentUser.getUid();

                    // Call a method to get user details and save the comment
                    getUserDetailsAndSaveComment(userId, messageText, commentId,null);
                } else {
                    // Handle empty message input
                }
            }
        });


        // Listen for changes in the database and update the RecyclerView
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Comment> comments = new ArrayList<>();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    comments.add(comment);
                }
                commentAdapter.setComments(comments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Set the selected image to the ImageView
            picture.setImageURI(imageUri);

            // Compress and get the compressed image URI
            Uri compressedImageUri = compressImage(imageUri);
            // Use the compressedImageUri as needed
            selectedImageUri = compressedImageUri;

            // Upload the compressed image to Firebase Storage
            uploadImageToFirebaseStorage();
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (selectedImageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("images3/" + UUID.randomUUID().toString());

            ProgressDialog progressDialog = new ProgressDialog(CommentActivity2.this);
            progressDialog.setMessage("Uploading image...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.setCancelable(false);
            progressDialog.show();

            UploadTask uploadTask = imageRef.putFile(selectedImageUri);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    // Calculate the upload progress in percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    // Update the ProgressDialog with the current progress
                    progressDialog.setProgress((int) progress);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();

                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            String imageContent = downloadUrl.toString();
                            String commentId = commentsRef.push().getKey();
                            getUserDetailsAndSaveComment(currentUser.getUid(), " ", commentId, imageContent);

                            // Show the image drawable again
                            picture.setImageResource(R.drawable.ic_baseline_image_24);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to retrieve image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Uri compressImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            // Define the desired image size in kilobytes
            long maxSizeBytes = 550 * 1024; // 200 KB (in bytes)

            // Calculate the current file size of the bitmap
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            long currentSizeBytes = outputStream.toByteArray().length;
            outputStream.close();

            // Check if the current size is already smaller than the desired size
            if (currentSizeBytes <= maxSizeBytes) {
                return imageUri; // No need to compress, return the original image URI
            }

            // Calculate the desired compression quality
            int compressQuality = (int) (maxSizeBytes * 100 / currentSizeBytes);

            // Compress the bitmap with the desired quality
            ByteArrayOutputStream compressedOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, compressedOutputStream);

            // Save the compressed bitmap to a temporary file
            File cachePath = new File(getCacheDir(), "temp_image.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(cachePath);
            fileOutputStream.write(compressedOutputStream.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();

            // Get the file URI from the cache path
            return Uri.fromFile(cachePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null; // Return null if the image URI couldn't be retrieved
    }



    private void showImagePopup(String imageUrl) {
        // Create a custom dialog for displaying the image
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image_popup);

        ImageView imageView = dialog.findViewById(R.id.imageViewPopup);
        ImageButton closeButton = dialog.findViewById(R.id.closeButton);

        // Load and display the image using Picasso
        if (imageUrl != null) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder);
        }

        // Set an OnClickListener for the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog when the close button is clicked
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void getUserDetailsAndSaveComment(String userId, String messageText, String commentId,String imageContent) {
        DatabaseReference userRef = studentsRef.child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Students student = dataSnapshot.getValue(Students.class);
                    if (student != null) {
                        // Handle the student details
                        String fullName = student.getName();
                        String imageUrl = student.getImage();
                        String profession = ""; // You can set this to an appropriate value for students
                        String email = student.getEmail();

                        // Pass the user details to the saveMessage method
                        saveMessage(fullName, messageText, imageUrl, profession, email, commentId,imageContent);
                    }
                } else {
                    // If not found in Students, check in Admin3
                    DatabaseReference adminUserRef = adminRef.child(userId);
                    adminUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Admin3 admin = dataSnapshot.getValue(Admin3.class);
                                if (admin != null) {
                                    // Handle the admin details
                                    String fullName = admin.getFullName();
                                    String imageUrl = admin.getImageUrl();
                                    String profession = ""; // You can set this to an appropriate value for admins
                                    String email = admin.getEmail();

                                    // Pass the user details to the saveMessage method
                                    saveMessage(fullName, messageText, imageUrl, profession, email, commentId,imageContent);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void saveMessage(String fullName, String messageText, String imageUrl, String profession, String email, String commentId,String imageContent) {
        // Create a new Comment object
        Comment comment = new Comment(fullName, messageText, imageUrl, profession, email,imageContent);

        // Save the comment to the "comments" database reference with the generated commentId
        commentsRef.child(commentId).setValue(comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Comment successfully added
                        commentEditText.setText(""); // Clear the input field
                        // Display a success toast message
                        Toast.makeText(CommentActivity2.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                        Toast.makeText(CommentActivity2.this, "Failed to add comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), announce_view2.class);
        startActivity(i);
        overridePendingTransition(0, 0);
        finish();
        super.onBackPressed();
    }
}
