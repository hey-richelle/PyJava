package com.sys.system;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class admin_update extends AppCompatActivity {
    ImageView update_img;
    EditText email_update, phone_update,fullname;
    Button update, back;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private DatabaseReference mDatabase;
    private String mUserId;
    private String imageUrl;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update);
        back = findViewById(R.id.back);
        update_img = findViewById(R.id.update_img);
        email_update = findViewById(R.id.email);
        phone_update = findViewById(R.id.Phone);
        update = findViewById(R.id.update);
        fullname = findViewById(R.id.fullname);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get the current user ID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            mUserId = currentUser.getUid();
        } else {
            // Handle the case where the user is not authenticated
            // You can redirect the user to the login screen or display an error message
        }

        // Load the user's profile data
        loadUserProfile();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Admin_page.class);
                overridePendingTransition(0, 0); // Disable animation
                startActivity(intent);
                finish();
            }
        });

        update_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start intent to select an image from storage
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void loadUserProfile() {
        mDatabase.child("ADMIN").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the user's current profile data from the database
                User2 user = dataSnapshot.getValue(User2.class);

                // Populate the EditText fields with the user's current profile data
                email_update.setText(user.getEmail());
                phone_update.setText(user.getPhone());
                fullname.setText(user.getFullName());

                // Load the user's profile image using Picasso
                if (user.getImageUrl() != null) {
                    Picasso.get().load(user.getImageUrl()).into(update_img);
                } else {
                    // Set a default drawable image if the user has no image
                    update_img.setImageResource(R.drawable.ic_baseline_person_24);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database read error
            }
        });
    }

    private void updateProfile() {
        // Get the updated profile data from the EditText fields
        String email = email_update.getText().toString().trim();
        String phone = phone_update.getText().toString().trim();
        String fullName = fullname.getText().toString().trim();

        // Validate the fields
        if (TextUtils.isEmpty(email)) {
            email_update.setError("Please enter your email");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            phone_update.setError("Please enter your phone number");
            return;
        }
        if (TextUtils.isEmpty(fullName)) {
            fullname.setError("Please enter your Full Name or Nick name");
            return;
        }

        // Show a progress dialog
        progressDialog = new ProgressDialog(admin_update.this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Check if an image is selected
        if (selectedImageUri != null) {
            // Upload the image to storage
            // Perform the necessary operations to upload the image
            // ...

            // Example code for uploading the image using Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("Admin/" + mUserId + ".jpg");

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    // Get the downloaded image URL
                                    String imageUrl = downloadUri.toString();

                                    // Update the user's profile data in the database with the image URL
                                    User2 admin = new User2(email, phone, fullName, imageUrl);

                                    mDatabase.child("ADMIN").child(mUserId).setValue(admin)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Profile data and image URL updated successfully
                                                    progressDialog.dismiss();
                                                    Toast.makeText(admin_update.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Profile data and image URL update failed
                                                    progressDialog.dismiss();
                                                    Toast.makeText(admin_update.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Image upload failed
                            progressDialog.dismiss();
                            Toast.makeText(admin_update.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // Calculate the progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            // Update the progress dialog
                            progressDialog.setMessage("Uploading image: " + (int) progress + "%");
                        }
                    });
        } else {
            // No image selected
            progressDialog.dismiss();
            Toast.makeText(admin_update.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Set the selected image to the ImageView
            update_img.setImageURI(imageUri);

            // Compress and get the compressed image URI
            Uri compressedImageUri = compressImage(imageUri);
            // Use the compressedImageUri as needed
            selectedImageUri = compressedImageUri;
        } else {
            // No image selected, show a placeholder image
            update_img.setImageResource(R.drawable.placeholder_image);
            selectedImageUri = null;
        }
    }


    private Uri compressImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            // Define the desired image size in kilobytes
            long maxSizeBytes = 200 * 1024; // 200 KB (in bytes)

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


    private Uri getImageUriFromDrawable(Drawable drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();

        String tempFilePath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "temp_image", null);
        return Uri.parse(tempFilePath);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),Admin_page.class);
        startActivity(intent);
        overridePendingTransition(0, 0); // Disable animation
        super.onBackPressed();
    }

}
