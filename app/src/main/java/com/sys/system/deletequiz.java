package com.sys.system;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.Nullable;

public class deletequiz extends AppCompatActivity {
    private EditText mQuestionIdEditText;
    private Button mDeleteQuestionButton;


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
                        Intent intent = new Intent(deletequiz.this, Admin_page.class);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deletequiz);
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);

// Set its color to black
        homeButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

// Set the modified drawable as the home button icon
        getSupportActionBar().setHomeAsUpIndicator(homeButton);

// Enable the home button on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the title of the action bar
        getSupportActionBar().setTitle("Delete Quiz");

// Set the color of the title to black
        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#606C5D")));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Initialize views
        mQuestionIdEditText = findViewById(R.id.et_question_id);
        mDeleteQuestionButton = findViewById(R.id.btn_delete_question);


        mDeleteQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get node name from EditText
                String nodeName = mQuestionIdEditText.getText().toString();

                if (TextUtils.isEmpty(nodeName)) {
                    // Show error message to user
                    Toast.makeText(deletequiz.this, "Please enter a node name to delete.", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(deletequiz.this);
                    builder.setTitle("Delete Node");
                    builder.setMessage("Are you sure you want to delete this node?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Delete node from Firebase Realtime Database
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference nodeRef = database.child(nodeName);
                            nodeRef.removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error == null) {
                                        // Show success message to user
                                        Toast.makeText(deletequiz.this, "Node deleted successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Show error message to user
                                        Toast.makeText(deletequiz.this, "Error deleting node: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            // Clear entered node name
                            mQuestionIdEditText.setText("");
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing, user canceled deletion
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}
