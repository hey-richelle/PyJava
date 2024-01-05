package com.sys.system.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.sys.system.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class picturegame extends AppCompatActivity {
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
                        Intent intent = new Intent(getApplicationContext(), create_quiz.class);
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
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private ImageView[] questionImages;
    private EditText userInput;
    private Button submitBtn;

    private static final int PICK_IMAGE_REQUEST = 1;
    private int imageUploadCount = 0;
    private ProgressDialog progressDialog;
    private List<Uri> imageUris;
    private String answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picturegame);
        // Get the home button drawable
        getSupportActionBar().setTitle("Set picture game");
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#606C5D")));
        // Initialize Firebase Realtime Database reference
        databaseRef = FirebaseDatabase.getInstance().getReference().child("4pics");

        // Initialize Firebase Storage reference
        storageRef = FirebaseStorage.getInstance().getReference().child("4pics");

        // Initialize views
        questionImages = new ImageView[4];
        questionImages[0] = findViewById(R.id.image1);
        questionImages[1] = findViewById(R.id.image2);
        questionImages[2] = findViewById(R.id.image3);
        questionImages[3] = findViewById(R.id.image4);
        userInput = findViewById(R.id.userInput);
        submitBtn = findViewById(R.id.submitBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Images...");
        progressDialog.setCancelable(false);

        imageUris = new ArrayList<>();

        // Set the images onClick listeners
        for (int i = 0; i < questionImages.length; i++) {
            final int index = i;
            questionImages[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImagePicker(index);
                }
            });
        }

        // Submit button click listener
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = userInput.getText().toString().trim();

                if (TextUtils.isEmpty(answer) || imageUploadCount < 4) {
                    // Notify user to input an answer or upload all images
                    Toast.makeText(picturegame.this, "Please input the answer or upload all images", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save the user's answer and image URIs to Firebase Realtime Database
                saveQuestion(answer);

                userInput.setText(""); // Clear the input field after submission

                // Clear the images
                for (ImageView questionImage : questionImages) {
                    questionImage.setImageResource(R.drawable.ic_baseline_person_24);
                }

                // Reset image upload count and URI list
                imageUploadCount = 0;
                imageUris.clear();

                // Notify the user that the answer has been submitted and proceed to the next question
                Toast.makeText(picturegame.this, "Answer submitted. Proceed to the next question.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker(int imageIndex) {
        // Launch image picker intent
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), imageIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode >= 0 && requestCode < questionImages.length && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the selected image URI
            Uri imageUri = data.getData();

            // Increment the image upload count
            imageUploadCount++;

            // Save the image URI to the list
            imageUris.add(imageUri);

            // Load the image into the correspondingImageView using Picasso
            Picasso.get()
                    .load(imageUri)
                    .placeholder(R.drawable.placeholder) // Placeholder image while loading
                    .into(questionImages[requestCode]);
        }
    }

    private void saveQuestion(String answer) {
        // Create a new question node in the database
        DatabaseReference newQuestionRef = databaseRef.push();

        // Save the answer to the new question node
        newQuestionRef.child("answer").setValue(answer);

        // Save the image URLs to the new question node
        for (int i = 0; i < imageUris.size(); i++) {
            Uri imageUri = imageUris.get(i);
            final int imageIndex = i + 1;

            // Compress and upload the image to Firebase Storage
            uploadImage(imageUri, newQuestionRef, imageIndex);
        }
    }

    private void uploadImage(Uri imageUri, final DatabaseReference questionRef, final int imageIndex) {
        try {
            // Get the Bitmap from the selected image URI
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            // Compress the image to reduce file size
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Adjust the quality and compression format as needed
            byte[] imageData = baos.toByteArray();

            // Create a storage reference with a unique filename
            StorageReference imageRef = storageRef.child(questionRef.getKey() + "/image" + imageIndex + ".jpg");

            // Upload the compressed image to Firebase Storage
            UploadTask uploadTask = imageRef.putBytes(imageData);
            progressDialog.show();

            // Listen for the upload success/failure
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        // Image uploaded successfully
                        // Get the download URL of the uploaded image
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Save the download URL to the question node
                                questionRef.child("image" + imageIndex + "_url").setValue(uri.toString());

                                if (imageIndex == 4) {
                                    // All images are uploaded, show submit button
                                    submitBtn.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } else {
                        // Image upload failed
                        Toast.makeText(picturegame.this, "Failed to upload image. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
