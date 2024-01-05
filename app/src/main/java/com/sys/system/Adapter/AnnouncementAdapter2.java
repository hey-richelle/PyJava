package com.sys.system.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.sys.system.Activity.CommentActivity2;
import com.sys.system.Class.Announcement;
import com.sys.system.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnouncementAdapter2 extends ArrayAdapter<Announcement> {

    private static class ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
        TextView timeTextView;
        TextView dateTextView;
        TextView fullNameTextView;
        ImageView imageView;
        Button commentBtn;
    }

    private Dialog webViewDialog;

    public AnnouncementAdapter2(Context context, List<Announcement> announcements) {
        super(context, 0, announcements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Announcement announcement = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_announcement2, parent, false);
            viewHolder.titleTextView = convertView.findViewById(R.id.titleTextView);
            viewHolder.contentTextView = convertView.findViewById(R.id.contentTextView);
            viewHolder.timeTextView = convertView.findViewById(R.id.timeTextView);
            viewHolder.dateTextView = convertView.findViewById(R.id.dateTextView);
            viewHolder.fullNameTextView = convertView.findViewById(R.id.fullNameTextView);
            viewHolder.imageView = convertView.findViewById(R.id.imageView);
            viewHolder.commentBtn = convertView.findViewById(R.id.Comment);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (announcement != null) {
            viewHolder.titleTextView.setText(announcement.getTitle());
            viewHolder.timeTextView.setText(announcement.getTime());
            viewHolder.dateTextView.setText(announcement.getDate());
            viewHolder.fullNameTextView.setText(announcement.getUploaderName());

            // Load and display the image using Picasso
            if (announcement.getImageUrl() != null) {
                Picasso.get()
                        .load(announcement.getImageUrl())
                        .placeholder(R.drawable.placeholder) // Set the placeholder image
                        .error(R.drawable.placeholder) // Set the error image if loading fails
                        .into(viewHolder.imageView);
            } else {
                // If no image is available, you can set a placeholder image or hide the imageView
                viewHolder.imageView.setImageResource(R.drawable.placeholder);
                // viewHolder.imageView.setVisibility(View.GONE);
            }

            // Set the content with clickable link
            SpannableString spannableString = new SpannableString(announcement.getContent());

// Pattern for matching URLs
            Pattern pattern = Patterns.WEB_URL;
            Matcher matcher = pattern.matcher(announcement.getContent());

            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();

                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        String url = announcement.getContent().substring(start, end);
                        showWebViewDialog(url);
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(true); // Add underline to the link
                        ds.setColor(Color.BLUE); // Set the link color
                    }
                };

                spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            viewHolder.contentTextView.setText(spannableString);
            viewHolder.contentTextView.setMovementMethod(LinkMovementMethod.getInstance());

            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showWebViewDialog(announcement.getImageUrl());
                }
            });

            viewHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to open the CommentActivity
                    Intent intent = new Intent(getContext(), CommentActivity2.class);
                    // Pass the announcement details as extras
                    intent.putExtra("announcementId", announcement.getId()); // Pass the announcement ID
                    intent.putExtra("title", announcement.getTitle());
                    intent.putExtra("content", announcement.getContent());
                    intent.putExtra("time", announcement.getTime());
                    intent.putExtra("date", announcement.getDate());
                    intent.putExtra("fullName", announcement.getUploaderName());
                    intent.putExtra("imageUrl", announcement.getImageUrl());
                    getContext().startActivity(intent);
                    ((Activity)getContext()).overridePendingTransition(0, 0); // Disable animation
                    ((Activity) getContext()).finish();
                }
            });

        }

        return convertView;
    }

    private void showWebViewDialog(String url) {
        if (webViewDialog == null) {
            webViewDialog = new Dialog(getContext());
            webViewDialog.setContentView(R.layout.dialog_webview);
            webViewDialog.setCancelable(true);

            final WebView webView = webViewDialog.findViewById(R.id.webView);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(url);

            // Enable zoom controls
            WebSettings webSettings = webView.getSettings();
            webSettings.setUseWideViewPort(true);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);

            // Handle dialog close event
            webViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    // Clear the WebView and release resources
                    webView.loadUrl("about:blank");
                    webView.destroy();
                    webViewDialog = null;
                }
            });

            // Exit button
            ImageButton exitButton = webViewDialog.findViewById(R.id.exitButton);
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webViewDialog.dismiss();
                }
            });
        }

        webViewDialog.show();
    }
}
