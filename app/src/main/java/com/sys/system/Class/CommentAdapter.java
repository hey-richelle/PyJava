package com.sys.system.Class;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.sys.system.R;

import java.util.List;
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        // Load user image using Picasso
        Picasso.get()
                .load(comment.getImageUrl()) // Use getImageUrl() to get the image URL
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.userImageView);

        holder.userNameTextView.setText(comment.getFullName()); // Use getFullName() to get the user's full name

        // Check if the comment has text content
        if (!comment.getMessageText().isEmpty()) {
            holder.commentTextView.setVisibility(View.VISIBLE);
            holder.commentTextView.setText(comment.getMessageText()); // Use getMessageText() to get the comment text
        } else {
            holder.commentTextView.setVisibility(View.GONE);
        }

        // Check if the comment has image content
        if (comment.getImageContent() != null && !comment.getImageContent().isEmpty()) {
            holder.imageContentImageView.setVisibility(View.VISIBLE);
            holder.imageContentImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the click event here, open the WebView
                    openWebViewActivity(holder.itemView.getContext(), comment.getImageContent());
                }
            });

            // Load image content using Picasso
            Picasso.get()
                    .load(comment.getImageContent()) // Use getImageContent() to get the image content URL
                    .into(holder.imageContentImageView);
        } else {
            holder.imageContentImageView.setVisibility(View.GONE);
        }
    }

    private void openWebViewActivity(Context context, String imageUrl) {
        // Inflate the WebView layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View webViewLayout = inflater.inflate(R.layout.dialog_webview_modal, null);

        // Find the WebView and ImageButton in the inflated layout
        WebView webView = webViewLayout.findViewById(R.id.webView);
        ImageButton buttonExit = webViewLayout.findViewById(R.id.buttonExit);

        // Load the image URL into the WebView
        webView.loadUrl(imageUrl);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true); // Fit content to WebView's width
        webSettings.setUseWideViewPort(true); // Enable wide viewport
        webView.setInitialScale(1); // Set initial scale to 1 for better fit


        // Create a dialog to display the WebView
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(webViewLayout);
        final AlertDialog dialog = dialogBuilder.create();

        // Set a click listener for the exit button
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog when the exit button is clicked
                dialog.dismiss();
            }
        });

        // Show the dialog with the WebView
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void setComments(List<Comment> comments) {
        commentList = comments; // Update the commentList with new data
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView userImageView;
        TextView userNameTextView;
        TextView commentTextView;
        ImageView imageContentImageView; // Add ImageView for image content

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView = itemView.findViewById(R.id.userImageView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            imageContentImageView = itemView.findViewById(R.id.imageContent); // Initialize imageContentImageView
        }
    }
}
