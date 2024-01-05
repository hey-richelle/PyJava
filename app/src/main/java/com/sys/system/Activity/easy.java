package com.sys.system.Activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sys.system.Class.Question;
import com.sys.system.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class easy extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView mQuestionView,
            mScoreView;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton1, mRadioButton2, mRadioButton3;
    Button mNextButton;
    private TextView mTimeView, mQuestionNumberView;
    private CountDownTimer mCountDownTimer;
    private int mScore = 0;
    private int mCurrentQuestionIndex = 0;
    private List<Question> mQuestionList;
    private DatabaseReference mScoreRef;
    private long interval = 1000;
    private long mTimeRemaining;
    private boolean isTimerRunning = false;
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
                        Intent intent = new Intent(getApplicationContext(), easy2.class);
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
        setContentView(R.layout.activity_easy);
        getSupportActionBar().setTitle("Java Easy Mode");
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D6B4FC")));
        mQuestionView = findViewById(R.id.question_view);
        mQuestionNumberView = findViewById(R.id.outof);
        mScoreView = findViewById(R.id.score_view);
        mRadioGroup = findViewById(R.id.radio_group);
        mRadioButton1 = findViewById(R.id.radio_button1);
        mRadioButton2 = findViewById(R.id.radio_button2);
        mRadioButton3 = findViewById(R.id.radio_button3);
        mTimeView = findViewById(R.id.time_view);
        mNextButton = findViewById(R.id.next_button);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Set_A");
        mScoreRef = FirebaseDatabase.getInstance().getReference().child("Java_score");

        mQuestionList = new ArrayList<>();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                        String questionText = questionSnapshot.child("question").getValue(String.class);
                        String option1 = questionSnapshot.child("option1").getValue(String.class);
                        String option2 = questionSnapshot.child("option2").getValue(String.class);
                        String option3 = questionSnapshot.child("option3").getValue(String.class);
                        String answer = questionSnapshot.child("answer").getValue(String.class);
                        String[] answerOptions = {option1, option2, option3};
                        int correctAnswerIndex = -1;
                        if (answerOptions[0].equals(answer)) {
                            correctAnswerIndex = 0;
                        } else if (answerOptions[1].equals(answer)) {
                            correctAnswerIndex = 1;
                        } else if (answerOptions[2].equals(answer)) {
                            correctAnswerIndex = 2;
                        }
                        Question question = new Question(questionText, answerOptions, correctAnswerIndex);
                        mQuestionList.add(question);
                    }
                    Collections.shuffle(mQuestionList);
                    showQuestion();

                    // Start the countdown timer
                    startCountdownTimer();
                } else {
                    // Handle no questions found in the database
                    Log.e("DatabaseError", "No questions found in database");
                    Toast.makeText(easy.this, "Quiz is already done", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), easy_end.class);
                    startActivity(intent);
                    finish(); // Close the activity or take other appropriate action
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur while reading from the database
            }
        });
    }

    private void startCountdownTimer() {
        long totalTime = 1 * 60 * 1000; // 1 minute
        mTimeRemaining = totalTime; // Initialize or reset the remaining time

        mCountDownTimer = new CountDownTimer(totalTime, 1000) {
            public void onTick(long millisUntilFinished) {
                mTimeRemaining = millisUntilFinished; // Update the remaining time

                // Convert milliseconds to hours, minutes, and seconds
                int hours = (int) (millisUntilFinished / (60 * 60 * 1000));
                int minutes = (int) (millisUntilFinished / (60 * 1000)) % 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                // Format the time as a string and display it
                String time = String.format("Time remaining: %02d:%02d:%02d", hours, minutes, seconds);
                mTimeView.setText(time);
            }

            public void onFinish() {
                // Code to execute when the time is up
                if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                    // No radio button is selected, push to hard quiz or proceed to next question
                    pushToHardQuiz(); // Call your pushToHardQuiz method here
                    mCurrentQuestionIndex++; // Move to the next question
                    if (mCurrentQuestionIndex < mQuestionList.size()) {
                        resetCountdownTimer(); // Reset the timer to 1 minute
                        showQuestion();
                    } else {
                        finishQuiz();
                    }
                } else {
                    // A radio button is selected, check the answer and proceed to the next question
                    checkAnswer();
                    mCurrentQuestionIndex++; // Move to the next question
                    if (mCurrentQuestionIndex < mQuestionList.size()) {
                        resetCountdownTimer(); // Reset the timer to 1 minute
                        showQuestion();
                    } else {
                        finishQuiz();
                    }
                }
            }

        };

        // Check if the timer is already running
        if (!isTimerRunning) {
            // User has not scored, start the countdown timer
            mCountDownTimer.start();
            isTimerRunning = true; // Set the timer running flag
        }
    



        // Check if the user has already scored
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("easy_score").child(userId);
        scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // User has not scored, start the countdown timer
                    mCountDownTimer.start();
                } else {
                    // User has already scored, show a message
                    Toast.makeText(easy.this, "You have already completed the quiz", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });



        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if a radio button is selected
                if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                    // Show a toast message prompting the user to choose an answer
                    Toast.makeText(getApplicationContext(), "Please choose an answer", Toast.LENGTH_SHORT).show();
                    return;
                }

                // A radio button is selected, proceed to the next question
                checkAnswer();
                mCurrentQuestionIndex++;
                if (mCurrentQuestionIndex < mQuestionList.size()) {
                    resetCountdownTimer(); // Reset the timer to 20 minutes
                    showQuestion();
                } else {
                    finishQuiz();
                }
            }
        });

    }

    private void pushToHardQuiz() {
        if (mCurrentQuestionIndex >= 0 && mCurrentQuestionIndex < mQuestionList.size()) {
            Question question = mQuestionList.get(mCurrentQuestionIndex);
            if (question == null) {
                return;
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                // Add the question to "Java_hard_quiz" section with user's ID
                DatabaseReference hardModeRef = FirebaseDatabase.getInstance().getReference().child("Java_hard_quiz").child(userId);
                String questionId = hardModeRef.push().getKey();

                // Create a map to represent the question data
                Map<String, Object> questionData = new HashMap<>();
                questionData.put("question", question.getQuestionText());
                String[] answerOptions = question.getAnswerOptions();
                if (answerOptions != null && question.getCorrectAnswerIndex() >= 0 && question.getCorrectAnswerIndex() < answerOptions.length) {
                    questionData.put("answer", answerOptions[question.getCorrectAnswerIndex()]);
                    questionData.put("correctAnswer", answerOptions[question.getCorrectAnswerIndex()]); // Store the correct answer
                }
                questionData.put("option1", answerOptions != null && answerOptions.length > 0 ? answerOptions[0] : "");
                questionData.put("option2", answerOptions != null && answerOptions.length > 1 ? answerOptions[1] : "");
                questionData.put("option3", answerOptions != null && answerOptions.length > 2 ? answerOptions[2] : "");
                questionData.put("selectedOption", "No option selected");

                hardModeRef.child(questionId).setValue(questionData);
            }
        }
    }


    private void resetCountdownTimer() {
        mCountDownTimer.cancel(); // Cancel the existing timer
        isTimerRunning = false; // Set the timer running flag to false
        startCountdownTimer(); // Start a new timer
    }


    private void showQuestion() {

        // Check if the user has a record of scores already in the "Breeding_scores" node
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("Java_score").child(userId);
        scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The user already has a record of scores, so show a pop-up message to inform the user that they have already completed the quiz
                    String message = "You have already completed the quiz. Your scores are: " + dataSnapshot.getValue();
                    AlertDialog.Builder builder = new AlertDialog.Builder(easy.this);
                    builder.setTitle("Quiz already completed")
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Close the dialog and navigate to the activity you want to show
                                    Intent intent = new Intent(easy.this, easy_score.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    // Hide the radio buttons, radio group, and question number
                    mQuestionView.setVisibility(View.GONE);
                    mRadioButton1.setVisibility(View.GONE);
                    mRadioButton2.setVisibility(View.GONE);
                    mRadioButton3.setVisibility(View.GONE);
                    mRadioGroup.setVisibility(View.GONE);
                    mQuestionNumberView.setVisibility(View.GONE);
                } else {
                    // Shuffle the list of questions if this is the first question
                    if (mCurrentQuestionIndex == 0) {
                        Collections.shuffle(mQuestionList);
                    }
                    Question question = mQuestionList.get(mCurrentQuestionIndex);
                    String questionText = (mCurrentQuestionIndex + 1) + ". " + question.getQuestionText();
                    mQuestionView.setText(questionText);
                    mRadioButton1.setText(question.getAnswerOptions()[0]);
                    mRadioButton2.setText(question.getAnswerOptions()[1]);
                    mRadioButton3.setText(question.getAnswerOptions()[2]);
                    mRadioGroup.clearCheck();

                    // Show the current question number and the total number of questions
                    String questionNumberText = (mCurrentQuestionIndex + 1) + "/" + mQuestionList.size();
                    mQuestionNumberView.setText(questionNumberText);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void checkAnswer() {
        if (mQuestionList == null || mCurrentQuestionIndex < 0 || mCurrentQuestionIndex >= mQuestionList.size()) {
            return;
        }
        Question question = mQuestionList.get(mCurrentQuestionIndex);
        if (question == null) {
            return;
        }
        if (mRadioGroup == null) {
            return;
        }
        int selectedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        if (selectedRadioButton == null) {
            return;
        }
        String[] answerOptions = question.getAnswerOptions();
        if (answerOptions == null || answerOptions.length <= question.getCorrectAnswerIndex() || question.getCorrectAnswerIndex() < 0 || answerOptions[question.getCorrectAnswerIndex()] == null) {
            return;
        }
        String selectedAnswer = selectedRadioButton.getText().toString();

        // Get the correct answer from the Easy_mode section in Firebase
        String correctAnswer = answerOptions[question.getCorrectAnswerIndex()];

        if (selectedAnswer.equals(correctAnswer)) {
            mScore++;
        } else {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                // User answered incorrectly, add the question to "Hard_mode" section with user's ID
                DatabaseReference hardModeRef = FirebaseDatabase.getInstance().getReference().child("Java_hard_quiz").child(userId);
                String questionId = hardModeRef.push().getKey();

                // Create a map to represent the question data
                Map<String, Object> questionData = new HashMap<>();
                questionData.put("question", question.getQuestionText());
                questionData.put("answer", correctAnswer);
                questionData.put("correctAnswer", correctAnswer); // Store the correct answer
                questionData.put("option1", answerOptions[0]);
                questionData.put("option2", answerOptions[1]);
                questionData.put("option3", answerOptions[2]);
                questionData.put("selectedOption", correctAnswer);

                hardModeRef.child(questionId).setValue(questionData);
            }
        }
    }


    private void updateScore() {
        mScoreView.setText("Score: " + mScore);
    }

    private void finishQuiz() {
        // Save score to database
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mScoreRef.child(userId).setValue(mScore);
        }

        // Show results
        Intent intent = new Intent(this,easy_score.class);
        intent.putExtra("score", mScore);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel(); // Cancel the countdown timer in case the activity is destroyed
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(easy.this, easy2.class);
        startActivity(intent);
        super.onBackPressed();
    }
}