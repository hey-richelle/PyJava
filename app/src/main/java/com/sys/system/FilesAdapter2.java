package com.sys.system;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FilesAdapter2 extends RecyclerView.Adapter<FilesAdapter2.ViewHolder> {

    private List<FileModel> fileList;
    private Context context;
    private List<FileModel> filteredList;

    public FilesAdapter2(Context context, List<FileModel> fileList) {
        this.context = context;
        this.fileList = fileList;
        this.filteredList = new ArrayList<>(fileList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item4, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FileModel fileModel = fileList.get(position);

        String uploaderText = "Admin Uploader: " + fileModel.getUploader();//admin name
        holder.fullNameTextView.setText(uploaderText);

        String title = "File Name: " + fileModel.getTitle();//file title
        holder.titleTextView.setText(title);

        String date = "Date: " + fileModel.getDate();// date
        holder.dateTextView.setText(date);

        String time = "Time: " + fileModel.getTime(); // time
        holder.timeTextView.setText(time);

        String size = "Size: " + formatFileSize(fileModel.getSize());
        holder.sizeTextView.setText(size);


        Glide.with(context)
                .load(R.drawable.placeholder2)
                .into(holder.fileImageView);



        holder.itemView.setOnLongClickListener(v -> {
            showDownloadOrArchiveDialog(fileModel);
            return true;
        });


    }
    private void showDownloadOrArchiveDialog(FileModel fileModel) {
        CharSequence[] options = new CharSequence[]{"Download", "Archive"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("File Options");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showDownloadConfirmation(fileModel);
                    break;
                case 1:
                    showArchiveConfirmation(fileModel);
                    break;
            }
        });
        builder.show();
    }

    private void showDownloadConfirmation(FileModel fileModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Download Confirmation");
        builder.setMessage("Are you sure you want to download the file? "+fileModel.getTitle());
        builder.setPositiveButton("Download", (dialog, which) -> startDownload(fileModel));
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }



    private void showArchiveConfirmation(FileModel fileModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to archive this file? "+fileModel.getTitle());
        builder.setPositiveButton("Yes", (dialog, which) -> showArchiveProgressDialog(fileModel));
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showArchiveProgressDialog(FileModel fileModel) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Archiving file..."+fileModel.getTitle());
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Add a delay of 1.5 seconds before archiving the file
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            archiveFile(fileModel, progressDialog);
        }, 1500);
    }

    private void archiveFile(FileModel fileModel, ProgressDialog progressDialog) {
        DatabaseReference sourceReference = FirebaseDatabase.getInstance().getReference("Java").child(fileModel.getId());

        sourceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the file data from the source node
                FileModel archivedFile = dataSnapshot.getValue(FileModel.class);

                if (archivedFile != null) {
                    // Remove the file from the source node
                    sourceReference.removeValue();

                    // Move the file to the archive node
                    DatabaseReference archiveReference = FirebaseDatabase.getInstance().getReference("Archive_modules").push();
                    archiveReference.setValue(archivedFile)
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                showToast("File archived successfully");
                                // You may choose to notify the user or update the UI as needed
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                showToast("Failed to archive file");
                                // Handle the failure, such as showing an error message to the user
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                showToast("Failed to archive file");
                // Handle the cancellation, such as showing an error message to the user
            }
        });
    }

    // Method to display a toast message
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }



    private void startDownload(FileModel fileModel) {
        // Create a progress dialog
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Downloading " + fileModel.getTitle());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Create the download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileModel.getUrl()));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setTitle(fileModel.getTitle())
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileModel.getTitle());

        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            // Enqueue the download request
            final long downloadId = downloadManager.enqueue(request);

            // Create a BroadcastReceiver to receive the download completion event
            BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (id == downloadId) {
                        progressDialog.dismiss(); // Dismiss the progress dialog

                        // Retrieve the downloaded file's URI
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(downloadId);
                        Cursor cursor = downloadManager.query(query);
                        if (cursor.moveToFirst()) {
                            int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            int titleIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
                            int status = cursor.getInt(statusIndex);
                            String title = cursor.getString(titleIndex);

                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                // Show a notification with the downloaded file's name
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                                        .setSmallIcon(R.drawable.meducate)
                                        .setContentTitle("Download Completed")
                                        .setContentText("File: " + title)
                                        .setAutoCancel(true);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                                notificationManager.notify(1, builder.build());

                                // Show a toast message with the downloaded file's name
                                Toast.makeText(context, title + " is downloaded", Toast.LENGTH_SHORT).show();
                            }
                        }
                        cursor.close();
                    }
                }
            };

            // Register the BroadcastReceiver to listen for download completion events
            context.registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }



    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void filterList(List<FileModel> filteredList) {
        this.fileList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView sizeTextView; // Add TextView for file size
        ImageView fileImageView;
        TextView fullNameTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
            titleTextView = itemView.findViewById(R.id.file_title);
            dateTextView = itemView.findViewById(R.id.file_date);
            timeTextView = itemView.findViewById(R.id.file_time);
            sizeTextView = itemView.findViewById(R.id.file_size); // Initialize the TextView for file size
            fileImageView = itemView.findViewById(R.id.file_image);
        }
    }

    private String formatFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

}