package com.sys.system.Adapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sys.system.Class.ReferenceModel;
import com.sys.system.R;

import java.util.List;

public class ReferenceAdapter extends RecyclerView.Adapter<ReferenceAdapter.ReferenceViewHolder> {
    private List<ReferenceModel> referenceList;
    private Context context;

    public ReferenceAdapter(List<ReferenceModel> referenceList, Context context) {
        this.referenceList = referenceList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReferenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reference_item, parent, false);
        return new ReferenceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReferenceViewHolder holder, int position) {
        ReferenceModel reference = referenceList.get(position);
        holder.bind(reference);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the URL from the clicked item
                String url = reference.getLink();

                // Open the WebView modal
                openWebViewModal(url);
            }
        });
    }

    @Override
    public int getItemCount() {
        return referenceList.size();
    }

    static class ReferenceViewHolder extends RecyclerView.ViewHolder {
        private TextView linkTextView;

        ReferenceViewHolder(@NonNull View itemView) {
            super(itemView);
            linkTextView = itemView.findViewById(R.id.linkTextView);
        }

        void bind(ReferenceModel reference) {
            linkTextView.setText(reference.getLink());
        }
    }

    private void openWebViewModal(String url) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View customLayout = LayoutInflater.from(context).inflate(R.layout.dialog_webview_modal, null);
        WebView webView = customLayout.findViewById(R.id.webView);
        ImageButton buttonExit = customLayout.findViewById(R.id.buttonExit);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webView.setInitialScale(1);
        webView.loadUrl(url);

        alertDialogBuilder.setView(customLayout);
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}
