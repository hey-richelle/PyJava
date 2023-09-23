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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Java_setb_setUp extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_logo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; show dialog to confirm going back to the menu
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to go back to the menu?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), java_set_b.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
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

    private EditText mQuestionTextEditText;
    private EditText mAnswerTextEditText;
    private Button mSaveQuestionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_setb_set_up);

        // Get the home button drawable
        getSupportActionBar().setTitle("Java Normal");
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));

        mQuestionTextEditText = findViewById(R.id.et_question);
        mAnswerTextEditText = findViewById(R.id.et_correct_answer);
        mSaveQuestionButton = findViewById(R.id.btn_save_question);
        mSaveQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get entered data
                String question = mQuestionTextEditText.getText().toString();
                String answer = mAnswerTextEditText.getText().toString();

                if (TextUtils.isEmpty(question) || TextUtils.isEmpty(answer)) {
                    // Show error message to the user
                    Toast.makeText(Java_setb_setUp.this, "Please fill in the question and the correct answer.", Toast.LENGTH_SHORT).show();
                } else {
                    // Store entered data in Firebase Realtime Database
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference questionsRef = database.child("Java_normal");
                    String questionId = questionsRef.push().getKey();
                    Question10 newQuestion = new Question10(question, answer);
                    questionsRef.child(questionId).setValue(newQuestion);

                    // Clear entered data
                    mQuestionTextEditText.setText("");
                    mAnswerTextEditText.setText("");

                    // Show success message to the user
                    Toast.makeText(Java_setb_setUp.this, "Question saved successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), java_set_b.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }
}
