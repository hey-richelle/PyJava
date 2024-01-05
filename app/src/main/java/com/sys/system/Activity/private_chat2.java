package com.sys.system.Activity;


import android.app.AlertDialog;
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
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import com.sys.system.Adapter.ChatAdapter5;
import com.sys.system.Class.ChatMessage;
import com.sys.system.Class.CircleTransform;
import com.sys.system.Class.DBMember;
import com.sys.system.Class.Message;
import com.sys.system.R;
import com.sys.system.Class.User3;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class private_chat2 extends AppCompatActivity {
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
                        Intent intent = new Intent(getApplicationContext(), chat_view2.class);
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






    private RecyclerView chatRecyclerView;
    private List<ChatMessage> chatMessages;
    private ChatAdapter5 chatAdapter;
    private DatabaseReference adminLoginRef;
    private DatabaseReference dbMemberRef;
    private String otherUserIdentifier; // The identifier (email or profession) of the other user
    private String currentUserUid; // Store the UID of the current user
    private String chatRoomIdentifier;
    private String currentUserEmail;
    ImageButton imageButton;
    private Uri selectedImageUri;
    private List<Message> messageList;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat2);


        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
// Set its color to black
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
// Set the modified drawable as the home button icon
        getSupportActionBar().setHomeAsUpIndicator(homeButton);

        // Set the title of the action bar
        getSupportActionBar().setTitle("Chat box");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));


        // Retrieve the other user's identifier from the intent
        Intent intent = getIntent();
        if (intent != null) {
            otherUserIdentifier = intent.getStringExtra("chat_identifier");
            // Create the chat room identifier by sorting and concatenating the emails
            List<String> emails = Arrays.asList(FirebaseAuth.getInstance().getCurrentUser().getEmail(), otherUserIdentifier);
            Collections.sort(emails);
            chatRoomIdentifier = encodeIdentifier(TextUtils.join("_", emails));
            fetchOtherUserFullname();
        }
        // Get the UID and email of the current user from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserUid = currentUser.getUid();
            currentUserEmail = currentUser.getEmail();
        }

// Initialize RecyclerView and list to store chat messages
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatMessages = new ArrayList<>();
// Assuming you have already set the value of 'currentUserUid' and 'chatRoomIdentifier'
        chatAdapter = new ChatAdapter5(chatMessages, currentUserUid, chatRoomIdentifier, currentUserEmail);


        // Set up the RecyclerView with the adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        // Fetch and display chat messages between the current user and the other user
        retrieveChatMessages();


        adminLoginRef = FirebaseDatabase.getInstance().getReference("ADMIN");
        dbMemberRef = FirebaseDatabase.getInstance().getReference("Student");


        imageButton = findViewById(R.id.buttonImage);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(private_chat2.this);
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

        ImageButton sendMessageButton = findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText messageEditText = findViewById(R.id.messageEditText);
                String messageText = messageEditText.getText().toString().trim();

                // Check if both message and image are empty
                if (TextUtils.isEmpty(messageText) && selectedImageUri == null) {
                    // Show a toast message asking the user to enter a message or select an image
                    Toast.makeText(getApplicationContext(), "Please enter a message or select an image before sending.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the message contains only image (no text message)
                if (TextUtils.isEmpty(messageText) && selectedImageUri != null) {
                    // Upload the image to Firebase Storage and send the image message
                    uploadImageToFirebaseStorage();
                } else {
                    // Send the text message
                    sendMessage(messageText, null);

                    messageEditText.setText("");
                }
            }
        });

    }

    private void fetchOtherUserFullname() {
        DatabaseReference dbMemberRef = FirebaseDatabase.getInstance().getReference("Student");
        dbMemberRef.orderByChild("email").equalTo(otherUserIdentifier).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        DBMember dbMember = userSnapshot.getValue(DBMember.class);
                        if (dbMember != null) {
                            String name = dbMember.getName();
                            String image = dbMember.getImage(); // Get the imageUrl
                            setActionBarTitle(name, image); // Set the other user's full name and imageUrl as ActionBar title
                            return; // Exit the loop since we found the user
                        }
                    }
                }

                // If the other user's full name was not found in DB_Member, check Admin_login
                fetchOtherUserFullnameFromAdminLogin();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if necessary
            }
        });
    }

    private void fetchOtherUserFullnameFromAdminLogin() {
        DatabaseReference adminLoginRef = FirebaseDatabase.getInstance().getReference("ADMIN");
        adminLoginRef.orderByChild("email").equalTo(otherUserIdentifier).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        User3 user = userSnapshot.getValue(User3.class);
                        if (user != null) {
                            String fullName = user.getFullName();
                            String imageUrl = user.getImageUrl(); // Get the imageUrl
                            setActionBarTitle(fullName, imageUrl); // Set the other user's full name and imageUrl as ActionBar title
                            return; // Exit the loop since we found the user
                        }
                    }
                }

                // If the other user's full name was not found in Admin_login as well, you can handle it here.
                // For example, you can set a default title or show an error message.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error if necessary
            }
        });
    }

    private void setActionBarTitle(String fullName, String imageUrl) {
        ActionBar actionBar = getSupportActionBar();
        // Inflate the custom layout for the ActionBar
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar_layout);

        // Get references to the views in the custom layout
        View customActionBarView = actionBar.getCustomView();
        ImageView avatarImageView = customActionBarView.findViewById(R.id.avatarImageView);
        TextView fullNameTextView = customActionBarView.findViewById(R.id.fullNameTextView);
        ImageView backView = customActionBarView.findViewById(R.id.backButton);

        // Load the user's image into the circular ImageView using Picasso
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .transform(new CircleTransform()) // Custom transformation to make the image circular
                    .placeholder(R.drawable.ic_baseline_person_24) // Placeholder drawable for loading state
                    .error(R.drawable.ic_baseline_person_24) // Error drawable if image loading fails
                    .into(avatarImageView);
        }

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(private_chat2.this); // Use private_chat.this as the context
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to go back to the menu?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), chat_view2.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });

        // Set the user's full name in the TextView
        fullNameTextView.setText(fullName);

        // Show the custom layout in the ActionBar
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }



    private void retrieveChatMessages() {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("private_chats");
        String chatPath = "user1/" + chatRoomIdentifier; // Use the chat room identifier

        chatRef.child(chatPath).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot messageSnapshot, @Nullable String previousChildName) {
                // Deserialize the data from Firebase to the ChatMessage class
                ChatMessage chatMessage = messageSnapshot.getValue(ChatMessage.class);
                if (chatMessage != null) {
                    chatMessages.add(chatMessage);
                    chatAdapter.notifyDataSetChanged();

                    // Smooth scroll to the newly added message
                    chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot messageSnapshot, @Nullable String previousChildName) {
                // Handle any changes to the existing chat messages if needed
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot messageSnapshot) {
                // Handle message removal
                ChatMessage removedChatMessage = messageSnapshot.getValue(ChatMessage.class);
                if (removedChatMessage != null) {
                    // Find the position of the removed message in the chatMessages list
                    int removedIndex = -1;
                    for (int i = 0; i < chatMessages.size(); i++) {
                        if (chatMessages.get(i).getMessageId().equals(removedChatMessage.getMessageId())) {
                            removedIndex = i;
                            break;
                        }
                    }

                    // Remove the message from the list if found
                    if (removedIndex != -1) {
                        chatMessages.remove(removedIndex);
                        chatAdapter.notifyItemRemoved(removedIndex);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot messageSnapshot, @Nullable String previousChildName) {
                // Handle message movement if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
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
            imageButton.setImageURI(imageUri);

            // Compress and get the compressed image URI
            Uri compressedImageUri = compressImage(imageUri);
            // Use the compressedImageUri as needed
            selectedImageUri = compressedImageUri;

            // Upload the compressed image to Firebase Storage
            uploadImageToFirebaseStorage();
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


    private void uploadImageToFirebaseStorage() {
        if (selectedImageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("images3/" + UUID.randomUUID().toString());

            ProgressDialog progressDialog = new ProgressDialog(private_chat2.this);
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
                            sendMessage("", imageContent);

                            // Show the image drawable again
                            imageButton.setImageResource(R.drawable.ic_baseline_image_24);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(private_chat2.this, "Failed to retrieve image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(private_chat2.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void sendMessage(String messageText, String imageContent) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("private_chats");
        String chatPath = "user1/" + encodeIdentifier(chatRoomIdentifier); // Encode the chat room identifier
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date()); // Get the formatted date
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userRef = adminLoginRef.child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User3 user = dataSnapshot.getValue(User3.class);
                        if (user != null) {
                            String senderFullName = user.getFullName();
                            String senderImageUrl = user.getImageUrl();
                            String senderProfession = "";

                            // Save the message with the sender's information and formatted date
                            ChatMessage chatMessage = new ChatMessage(
                                    null,
                                    "Admin: " + senderFullName,
                                    messageText,
                                    System.currentTimeMillis(),
                                    currentUser.getUid(),
                                    senderFullName,
                                    senderImageUrl,
                                    currentUser.getEmail(),
                                    imageContent,
                                    formattedDate // Add formatted date here
                            );

                            String messageId = chatRef.child(chatPath).push().getKey();
                            chatMessage.setMessageId(messageId);
                            chatRef.child(chatPath).child(messageId).setValue(chatMessage);

                            // Clear the message input field after sending the message
                            EditText messageEditText = findViewById(R.id.messageEditText);
                            messageEditText.setText("");
                            selectedImageUri = null; // Reset the selected image URI after sending
                            imageButton.setImageResource(R.drawable.ic_baseline_image_24); // Reset the image button
                        }
                    } else {
                        DatabaseReference dbMemberUserRef = dbMemberRef.child(uid);
                        dbMemberUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    DBMember dbMember = dataSnapshot.getValue(DBMember.class);
                                    if (dbMember != null) {
                                        String senderFullName = dbMember.getName();
                                        String senderImageUrl = dbMember.getImage();
                                        String senderProfession = dbMember.getProfession();

                                        // Save the message with the sender's information and formatted date
                                        ChatMessage chatMessage = new ChatMessage(
                                                null,
                                                senderFullName,
                                                messageText,
                                                System.currentTimeMillis(),
                                                currentUser.getUid(),
                                                senderFullName,
                                                senderImageUrl,
                                                currentUser.getEmail(),
                                                imageContent,
                                                formattedDate // Add formatted date here
                                        );

                                        String messageId = chatRef.child(chatPath).push().getKey();
                                        chatMessage.setMessageId(messageId);
                                        chatRef.child(chatPath).child(messageId).setValue(chatMessage);

                                        // Clear the message input field after sending the message
                                        selectedImageUri = null; // Reset the selected image URI after sending
                                        imageButton.setImageResource(R.drawable.ic_baseline_image_24); // Reset the image button
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(private_chat2.this, "Failed to retrieve full name: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(private_chat2.this, "Failed to retrieve full name: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(private_chat2.this, "Full name retrieval failed", Toast.LENGTH_SHORT).show();
        }
    }



    //Encode the identifier to replace invalid characters
    private String encodeIdentifier(String identifier) {
        return identifier.replace(".", "_dot_").replace("@", "_at_");
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(private_chat2.this, chat_view2.class);
        startActivity(intent);
        overridePendingTransition(0, 0); // Disable animation
        super.onBackPressed();
    }
}
