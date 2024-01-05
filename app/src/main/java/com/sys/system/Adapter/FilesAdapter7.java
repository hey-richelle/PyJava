package com.sys.system.Adapter;


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
import com.sys.system.Class.FileModel;
import com.sys.system.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FilesAdapter7 extends RecyclerView.Adapter<FilesAdapter7.ViewHolder> {

    private List<FileModel> fileList;
    private Context context;
    private List<FileModel> filteredList;

    public FilesAdapter7(Context context, List<FileModel> fileList) {
        this.context = context;
        this.fileList = fileList;
        this.filteredList = new ArrayList<>(fileList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item5, parent, false);
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
                .load(R.drawable.placeholder)
                .into(holder.fileImageView);

        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Download File")
                    .setMessage("Do you want to download this file? "+fileModel.getTitle())
                    .setPositiveButton("Yes", (dialog, which) -> {
                        startDownload(fileModel);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }


    private void startDownload(final FileModel fileModel) {
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
    public void filterList(List<FileModel> filteredList) {
        this.fileList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView;
        TextView titleTextView;
        TextView dateTextView;
        TextView timeTextView;
        ImageView fileImageView;
        TextView sizeTextView; // Add TextView for file size

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
            titleTextView = itemView.findViewById(R.id.file_title);
            dateTextView = itemView.findViewById(R.id.file_date);
            timeTextView = itemView.findViewById(R.id.file_time);
            fileImageView = itemView.findViewById(R.id.file_image);
            sizeTextView = itemView.findViewById(R.id.file_size);
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
