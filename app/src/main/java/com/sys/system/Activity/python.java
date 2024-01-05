package com.sys.system.Activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sys.system.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class python extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 0;
    TextView save_File;
    TextView upload_File;
    Uri fileUri; // Uri for the selected file
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    TextView titleEditText;
    private FirebaseStorage storage;
    private FirebaseDatabase database;

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
                        Intent intent = new Intent(python.this, Admin_page.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0); // Disable animation
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Python");


        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);

// Set its color to black
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

// Set the modified drawable as the home button icon
        getSupportActionBar().setHomeAsUpIndicator(homeButton);

// Enable the home button on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setContentView(R.layout.activity_python);
        storage = FirebaseStorage.getInstance(); //return an object of Firebase Storage
        database = FirebaseDatabase.getInstance(); //return an object of Firebase Database
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));


        titleEditText = findViewById(R.id.title2);
        upload_File = findViewById(R.id.file_name);
        save_File = findViewById(R.id.upload_btn);
        // Get a reference to the Firebase Storage and Realtime Database instances
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get a reference to the "Upload_Breeding" node in the database
        databaseReference = database.getReference("Python");

        // Get a reference to the "Upload_Breeding" node in the storage
        storageReference = storage.getReference("Python_module");

        // Get a reference to the UI elements
        titleEditText = findViewById(R.id.title2);
        TextView uploadFileButton = findViewById(R.id.file_name);
        Button saveFileButton = findViewById(R.id.upload_btn);
        // Set up the click listeners for the buttons
        uploadFileButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(python.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //
                selectFile();
            } else {
                ActivityCompat.requestPermissions(python.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 23);
            }
        });


        saveFileButton.setOnClickListener(v -> {
            if (fileUri != null) {
                retrieveFullNameAndSaveFile();
            } else {
                Toast.makeText(python.this, "Select a File", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void retrieveFullNameAndSaveFile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(python.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String uploaderId = currentUser.getUid();

        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("ADMIN");

        adminRef.child(uploaderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    if (fullName != null) {
                        saveFile(fileUri, fullName);
                    } else {
                        Toast.makeText(python.this, "Failed to retrieve full name", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(python.this, "Uploader not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(python.this, "Failed to retrieve uploader details", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            fileUri = data.getData();
            String fileName = getFileName(fileUri);
            if (titleEditText != null) {
                titleEditText.setText(fileName);
            }
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void saveFile(Uri fileUri, String fullName) {
        ProgressDialog progressDialog = new ProgressDialog(python.this);
        progressDialog.setTitle("Uploading File");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String fileName = System.currentTimeMillis() + "";

        String[] filePathColumn = {MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(fileUri, filePathColumn, null, null, null);
        if (cursor == null) {
            Toast.makeText(python.this, "Cursor is null", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            fileName = cursor.getString(columnIndex);
        }
        cursor.close();

        if (fileName == null) {
            Toast.makeText(python.this, "Unable to get file name", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileStorageReference = storageReference.child(fileName);

        String title = titleEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            title = fileName;
        }

        String finalTitle = title;

        boolean isImage = isImageFile(fileUri);

        if (isImage) {
            // Compress the image if it's an image file
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

                while (byteArrayOutputStream.toByteArray().length / 1024 > 1024) {
                    byteArrayOutputStream.reset();
                    quality -= 10;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                }

                File compressedImageFile = new File(getCacheDir(), "compressed_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(compressedImageFile);
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
                fileOutputStream.flush();
                fileOutputStream.close();

                fileUri = Uri.fromFile(compressedImageFile);
            } catch (IOException e) {
                Toast.makeText(python.this, "Failed to compress image", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
        }

        UploadTask uploadTask = fileStorageReference.putFile(fileUri);
        uploadTask.addOnProgressListener(taskSnapshot -> {
            // Calculate the progress percentage
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

            // Update the progress dialog
            progressDialog.setProgress((int) progress);
            progressDialog.setMessage("Uploading: " + (int) progress + "%");
        }).addOnSuccessListener(taskSnapshot -> {
            fileStorageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();

                DatabaseReference fileReference = databaseReference.push();
                String fileId = fileReference.getKey();
                if (fileId == null) {
                    Toast.makeText(python.this, "Failed to generate key for file", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                // Get the current date and time
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                String currentDate = dateFormat.format(new Date());
                String currentTime = timeFormat.format(new Date());

                // Get the file size
                long fileSize = taskSnapshot.getTotalByteCount();

                // Add the file data to the fileData Map
                Map<String, Object> fileData = new HashMap<>();
                fileData.put("id", fileId);
                fileData.put("title", finalTitle);
                fileData.put("url", url);
                fileData.put("date", currentDate);
                fileData.put("time", currentTime);
                fileData.put("size", fileSize); // Add the file size
                fileData.put("uploader", fullName);

                fileReference.setValue(fileData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(python.this, "File uploaded and saved to database successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(python.this, "Failed to save file details to database", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(python.this, "Failed to upload file", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }

    private boolean isImageFile(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        String mimeType = contentResolver.getType(fileUri);

        if (mimeType != null) {
            return mimeType.startsWith("image/");
        }

        // If the MIME type is not available, fallback to file extension
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString());
        if (fileExtension != null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
            if (mimeType != null) {
                return mimeType.startsWith("image/");
            }
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),Admin_page.class);
        startActivity(intent);
        overridePendingTransition(0, 0); // Disable animation
        super.onBackPressed();
    }
}
