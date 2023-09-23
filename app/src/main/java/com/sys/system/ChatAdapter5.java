package com.sys.system;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter5 extends RecyclerView.Adapter<ChatAdapter5.ChatViewHolder> {
    private ProgressDialog progressDialog;
    private Handler handler = new Handler();
    private FirebaseAuth mAuth;

    private List<ChatMessage> chatMessages;
    private String currentUserUid; // Store the UID of the current user
    private String chatRoomIdentifier;
    private ValueEventListener childEventListener;
    private String currentUserEmail;
    public ChatAdapter5(List<ChatMessage> chatMessages, String currentUserUid, String chatRoomIdentifier, String currentUserEmail) {
        this.chatMessages = chatMessages;
        this.currentUserUid = currentUserUid;
        this.chatRoomIdentifier = chatRoomIdentifier;
        this.currentUserEmail = currentUserEmail;
        mAuth = FirebaseAuth.getInstance();
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);


        return new ChatViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        // Check if the sender is the current user
        boolean isCurrentUserMessage = chatMessage.getUid().equals(currentUserUid);

        // If it's the current user's message, align the layout to right-to-left (RTL)
        if (isCurrentUserMessage) {
            holder.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            // If it's not the current user's message, align the layout to the default direction (left-to-right)
            holder.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        // Rest of your code for setting up the views
        holder.fullnam.setText(chatMessage.getFullName());

        // Check if the message is not null and not empty
        if (chatMessage.getMessage() != null && !chatMessage.getMessage().isEmpty()) {
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.messageTextView.setText(chatMessage.getMessage());
        } else {
            // If the message is null or empty, hide the message TextView
            holder.messageTextView.setVisibility(View.GONE);

            // Load the image content using Picasso if the URL is not empty or null
            if (chatMessage.getImageContent() != null && !chatMessage.getImageContent().isEmpty()) {
                holder.imageView.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(chatMessage.getImageContent())
                        .placeholder(R.drawable.ic_baseline_image_24)
                        .error(R.drawable.ic_baseline_image_24)
                        .into(holder.imageView);
            } else {
                // If there is no image content, hide the ImageView
                holder.imageView.setVisibility(View.GONE);
            }
        }

        holder.messageTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
        holder.timestampTextView.setText(formatTimestamp(chatMessage.getTimestamp()));
        holder.dateTextView.setText(chatMessage.getDate());

        // Load the user profile image using Picasso if the URL is not empty or null
        if (chatMessage.getImageUrl() != null && !chatMessage.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(chatMessage.getImageUrl())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(holder.imageProfile);
        } else {
            // If the imageUrl is empty or null, set a placeholder or default image
            holder.imageProfile.setImageResource(R.drawable.ic_baseline_person_24);
        }

        // Add long click listener for message deletion
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeleteMessageDialog(view, holder.getAdapterPosition());
                return true;
            }
        });
    }


    private void showDeleteMessageDialog(View itemView, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        // Check if the current user is the owner of the message
        if (mAuth.getCurrentUser() != null && chatMessage.getUid().equals(mAuth.getCurrentUser().getUid())) {
            // The current user is the owner, show the delete confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Message");
            builder.setItems(new CharSequence[]{"Permanent delete!", "Unsent message!"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            // Delete Message option selected
                            showDeleteConfirmation(itemView, position);
                            break;

                        case 1:
                            // Delete Message option selected
                            showDeleteConfirmation2(itemView, position);
                            break;
                    }
                }
            });
            builder.show();
        } else {
            // The current user is not the owner, show a message indicating they can't delete the message
            Toast.makeText(itemView.getContext(), "You can only delete or unsent your own messages.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showDeleteConfirmation(View itemView, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
        builder.setTitle("Delete message! ");
        builder.setMessage("Do you want to delete the message to all users?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Show progress dialog
                progressDialog = new ProgressDialog(itemView.getContext());
                progressDialog.setMessage("Deleting message...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Add a delay of 1 second using a Handler
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Delayed task to be executed after 1 second
                        deleteMessage(position, builder.getContext());
                        progressDialog.dismiss(); // Dismiss the progress dialog
                    }
                }, 1000); // 1000 milliseconds = 1 second
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }



    private void showDeleteConfirmation2(View itemView, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
        builder.setTitle("Unsent message!");
        builder.setMessage("Do you want to unsent the message to all users?");
        builder.setPositiveButton("Unsent", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Show progress dialog
                progressDialog = new ProgressDialog(itemView.getContext());
                progressDialog.setMessage("Unsending....");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // Add a delay of 1 second using a Handler
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Delayed task to be executed after 1 second
                        unsentMessage(position, itemView.getContext());
                        progressDialog.dismiss(); // Dismiss the progress dialog
                    }
                }, 1000); // 1000 milliseconds = 1 second
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void unsentMessage(int position, Context context) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("private_chats");
        String chatPath = "user1/" + chatRoomIdentifier; // Use the chat room identifier

        ChatMessage messageToUnsent = chatMessages.get(position);

        // Update the message status to "Unsent" in the database
        messageToUnsent.setMessage("Message Unsent!!");
        chatRef.child(chatPath).child(messageToUnsent.getMessageId()).setValue(messageToUnsent);

        // Update the message in the local list
        chatMessages.set(position, messageToUnsent);
        notifyItemChanged(position);

        // Show a toast message to indicate the successful unsent
        Toast.makeText(context, "Message Unsent", Toast.LENGTH_SHORT).show();
    }

    private void deleteMessage(int position, Context context) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("private_chats");
        String chatPath = "user1/" + chatRoomIdentifier; // Use the chat room identifier

        ChatMessage messageToDelete = chatMessages.get(position);

        // Remove the message from the database
        chatRef.child(chatPath).child(messageToDelete.getMessageId()).removeValue();

        // Remove the message from the local list
        chatMessages.remove(position);
        notifyItemRemoved(position);

        // Optionally, you may want to check if the chat room is empty and delete it entirely.
        if (chatMessages.isEmpty()) {
            deleteChatRoom();
        }

        // Show a toast message to indicate the successful deletion
        Toast.makeText(context, "Message Deleted", Toast.LENGTH_SHORT).show();
    }


    private void deleteChatRoom() {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("private_chats");
        String chatPath = "user1/" + chatRoomIdentifier; // Use the chat room identifier

        // Remove the entire chat room from the database
        chatRef.child(chatPath).removeValue();
    }


    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (chatMessage.getUid().equals(currentUserUid)) {
            return MessageType.CURRENT_USER;
        } else {
            return MessageType.OTHER_USER;
        }
    }

    // Define constants for message types
    public static class MessageType {
        public static final int CURRENT_USER = 1;
        public static final int OTHER_USER = 2;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        ImageView imageView;
        ImageView imageProfile; // Add ImageView for the user profile image
        TextView messageTextView;
        TextView timestampTextView;
        TextView fullnam;

        public ChatViewHolder(View itemView) {
            super(itemView);
            fullnam = itemView.findViewById(R.id.fullname);
            dateTextView = itemView.findViewById(R.id.date);
            imageView = itemView.findViewById(R.id.messageContent);
            imageProfile = itemView.findViewById(R.id.image_profile); // Initialize the ImageView
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the contentUrl for the clicked image
                    String contentUrl = chatMessages.get(getAdapterPosition()).getImageContent();

                    // Check if the contentUrl is not empty and then open the WebView modal
                    if (contentUrl != null && !contentUrl.isEmpty()) {
                        openWebViewModal(itemView.getContext(), contentUrl);
                    }
                }
            });


        }

    }


    private void openWebViewModal(Context context, String contentUrl) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // Create a custom layout for the dialog that includes the WebView
        View customLayout = LayoutInflater.from(context).inflate(R.layout.dialog_webview_modal, null);
        WebView webView = customLayout.findViewById(R.id.webView);
        ImageButton buttonExit = customLayout.findViewById(R.id.buttonExit);

        // Configure WebView settings and load the contentUrl
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true); // Fit content to WebView's width
        webSettings.setUseWideViewPort(true); // Enable wide viewport
        webView.setInitialScale(1); // Set initial scale to 1 for better fit
        webView.loadUrl(contentUrl);

        alertDialogBuilder.setView(customLayout);

        // Set an "Exit" button for canceling the WebView dialog
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog when the "Cancel" button is clicked
                dialog.dismiss();
            }
        });

        // Set a "Download" button as the positive button for downloading the image
        alertDialogBuilder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Download the image using DownloadManager
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(contentUrl));
                request.setDescription("Image download in progress");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.jpg");

                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                if (downloadManager != null) {
                    downloadManager.enqueue(request);
                }
            }
        });

        // Show the dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        // Set an "Exit" button click listener to dismiss the dialog when clicked
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


    private String formatTimestamp(long timestamp) {
        // Implement the formatting logic for the timestamp as per your preference
        // For example:
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
        return dateFormat.format(new Date(timestamp));
    }
}