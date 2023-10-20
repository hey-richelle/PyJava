package com.sys.system;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Python_quiz3 extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView mQuestionView,
            mScoreView;
    private RadioGroup mRadioGroup;
    private EditText mFillInTheBlankEditText;
    private RadioButton mRadioButton1, mRadioButton2, mRadioButton3;
    Button mNextButton;
    private TextView mTimeView, mQuestionNumberView;
    private CountDownTimer mCountDownTimer;
    private int mScore = 0;
    private int mCurrentQuestionIndex = 0;
    private List<Question11> mQuestionList;
    private DatabaseReference mScoreRef;
    private long interval = 1000;
    private long mTimeRemaining;
    private boolean isTimerRunning = false;
    private boolean isTimerPaused = false;

    private TextToSpeech mTTS;
    ImageButton speech;
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
                        // If the timer is running, cancel it
                        if (isTimerRunning) {
                            mCountDownTimer.cancel();
                            isTimerRunning = false;
                        }

                        Intent intent = new Intent(getApplicationContext(), easy3.class);
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
        setContentView(R.layout.activity_python_quiz3);
        getSupportActionBar().setTitle("Python Hard");
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
        homeButton.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(homeButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFillInTheBlankEditText = findViewById(R.id.fill_in_the_blank);
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

        speech = findViewById(R.id.sound);

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        // Enable the button to speak the question
                        speech.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Python_Hard");
        mScoreRef = FirebaseDatabase.getInstance().getReference().child("Python_hard_scores");

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
                        if (answerOptions[0] != null && answerOptions[0].equals(answer)) {
                            correctAnswerIndex = 0;
                        } else if (answerOptions[1] != null && answerOptions[1].equals(answer)) {
                            correctAnswerIndex = 1;
                        } else if (answerOptions[2] != null && answerOptions[2].equals(answer)) {
                            correctAnswerIndex = 2;
                        }

                        Question11 question = new Question11(questionText, answerOptions, correctAnswerIndex,answer);
                        mQuestionList.add(question);
                    }
                    Collections.shuffle(mQuestionList);
                    showQuestion();

                    // Start the countdown timer
                    startCountdownTimer();
                } else {
                    // Handle no questions found in the database
                    Log.e("DatabaseError", "No questions found in database");
                    Toast.makeText(Python_quiz3.this, "Quiz is already done", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), python_hard_score1.class);
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
        long totalTime = 5 * 60 * 1000; // 3 minutes
        mTimeRemaining = totalTime;

        mCountDownTimer = new CountDownTimer(mTimeRemaining, 1000) {
            public void onTick(long millisUntilFinished) {
                if (!isTimerPaused) {
                    mTimeRemaining = millisUntilFinished;

                    int hours = (int) (millisUntilFinished / (60 * 60 * 1000));
                    int minutes = (int) (millisUntilFinished / (60 * 1000)) % 60;
                    int seconds = (int) (millisUntilFinished / 1000) % 60;

                    String time = String.format("Time remaining: %02d:%02d:%02d", hours, minutes, seconds);
                    mTimeView.setText(time);
                }
            }

            public void onFinish() {
                if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                    pushToWrongAnswers("", "");
                    mCurrentQuestionIndex++;
                    if (mCurrentQuestionIndex < mQuestionList.size()) {
                        resetCountdownTimer();
                        showQuestion();
                    } else {
                        finishQuiz();
                    }
                } else {
                    checkAnswer();
                    mCurrentQuestionIndex++;
                    if (mCurrentQuestionIndex < mQuestionList.size()) {
                        resetCountdownTimer();
                        showQuestion();
                    } else {
                        finishQuiz();
                    }
                }
            }
        };

        if (!isTimerRunning) {
            // Check if the user has already scored
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("Python_hard_scores").child(userId);
            scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // User has not scored, start the countdown timer
                        mCountDownTimer.start();
                    } else {
                        // User has already scored, show a message
                        Toast.makeText(getApplicationContext(), "You have already completed the quiz", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
            isTimerRunning = true;
        }

        // Check if the user has already scored
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("Python_hard_scores").child(userId);
        scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // User has not scored, start the countdown timer
                    mCountDownTimer.start();
                } else {
                    // User has already scored, show a message
                    Toast.makeText(getApplicationContext(), "You have already completed the quiz", Toast.LENGTH_SHORT).show();
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
                // Check if the current question is a fill-in-the-blank question
                boolean isFillInTheBlank = mFillInTheBlankEditText.getVisibility() == View.VISIBLE;

                if (isFillInTheBlank && mFillInTheBlankEditText.getText().toString().trim().isEmpty()) {
                    // If it's a fill-in-the-blank question and the EditText is empty, show a message
                    Toast.makeText(getApplicationContext(), "Please answer the question", Toast.LENGTH_SHORT).show();
                } else if (!isFillInTheBlank && mRadioGroup.getCheckedRadioButtonId() == -1) {
                    // If it's a multiple-choice question and no option is selected, show a message
                    Toast.makeText(getApplicationContext(), "Please choose an answer", Toast.LENGTH_SHORT).show();
                } else {
                    // User has answered the question, check the answer
                    checkAnswer();

                    mCurrentQuestionIndex++;
                    if (mCurrentQuestionIndex < mQuestionList.size()) {
                        // Reset the EditText and radio group for the next question
                        mFillInTheBlankEditText.setText("");
                        mRadioGroup.clearCheck();

                        showQuestion();
                    } else {
                        // Finish the quiz
                        finishQuiz();
                    }
                }
            }
        });

        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakQuestion();
            }
        });
    }
    private void speakQuestion() {
        String questionText = mQuestionView.getText().toString();
        float pitch = 1.0f; // Adjust pitch as needed
        float speed = 1.0f; // Adjust speed as needed

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        mTTS.speak(questionText, TextToSpeech.QUEUE_FLUSH, null);
    }


    private void pushToWrongAnswers(String questionText, String selectedOption) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference wrongAnswersRef = FirebaseDatabase.getInstance().getReference().child("Python_hard_wrong").child(userId);

            // Create a unique key for each wrong answer
            String wrongAnswerKey = wrongAnswersRef.push().getKey();

            // Create a map to represent the wrong answer data
            Map<String, Object> wrongAnswerData = new HashMap<>();
            wrongAnswerData.put("question", questionText);
            wrongAnswerData.put("selectedOption", selectedOption);

            wrongAnswersRef.child(wrongAnswerKey).setValue(wrongAnswerData);
        }
    }

    private void resetCountdownTimer() {
        mCountDownTimer.cancel(); // Cancel the existing timer
        isTimerRunning = false; // Set the timer running flag to false
        startCountdownTimer(); // Start a new timer
    }
    private void showQuestion() {
        // Check if the user has a record of scores already in the "Java_Easy_scores" node
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("Python_hard_scores").child(userId);
        scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The user already has a record of scores, so ask if they want to retake the quiz
                    String message = "You have already completed the quiz. Your scores are: " + dataSnapshot.getValue() +
                            "\nDo you want to retake the quiz?";
                    AlertDialog.Builder builder = new AlertDialog.Builder(Python_quiz3.this);
                    builder.setTitle("Quiz already completed")
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton("Retake Quiz", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Remove the user's scores
                                    scoresRef.removeValue();

                                    // Resume the timer and start the quiz again
                                    isTimerPaused = false;
                                    Intent intent = new Intent(getApplicationContext(), Python_quiz3.class);
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                    finish();
                                    startQuiz();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Close the dialog and navigate to the activity you want to show
                                    Intent intent = new Intent(Python_quiz3.this, python_hard_2.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    // Pause the timer
                    isTimerPaused = true;
                } else {
                    // If the user doesn't have scores, start the quiz
                    startQuiz();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    }

    private void startQuiz() {
        // Shuffle the list of questions if this is the first question
        if (mCurrentQuestionIndex == 0) {
            Collections.shuffle(mQuestionList);
        }
        Question11 question = mQuestionList.get(mCurrentQuestionIndex);
        String questionText = (mCurrentQuestionIndex + 1) + ". " + question.getQuestionText();
        mQuestionView.setText(questionText);

        // Check if the question has three answer options
        if (question.getCorrectAnswerIndex() >= 0 && question.getCorrectAnswerIndex() < 3) {
            // This is a multiple-choice question, show radio buttons
            mRadioButton1.setVisibility(View.VISIBLE);
            mRadioButton2.setVisibility(View.VISIBLE);
            mRadioButton3.setVisibility(View.VISIBLE);
            mFillInTheBlankEditText.setVisibility(View.GONE);

            // Set the radio button texts
            mRadioButton1.setText(question.getAnswerOptions()[0]);
            mRadioButton2.setText(question.getAnswerOptions()[1]);
            mRadioButton3.setText(question.getAnswerOptions()[2]);

            // Clear the selected radio button
            mRadioGroup.clearCheck();
        } else {
            // This is a fill-in-the-blank question, show EditText
            mFillInTheBlankEditText.setVisibility(View.VISIBLE);
            mRadioButton1.setVisibility(View.GONE);
            mRadioButton2.setVisibility(View.GONE);
            mRadioButton3.setVisibility(View.GONE);

            // Clear the EditText
            mFillInTheBlankEditText.setText("");
        }

        // Show the current question number and the total number of questions
        String questionNumberText = (mCurrentQuestionIndex + 1) + "/" + mQuestionList.size();
        mQuestionNumberView.setText(questionNumberText);
    }

    private void checkAnswer() {
        if (mQuestionList == null || mCurrentQuestionIndex < 0 || mCurrentQuestionIndex >= mQuestionList.size()) {
            return;
        }
        Question11 question = mQuestionList.get(mCurrentQuestionIndex);
        if (question == null) {
            return;
        }

        // Check if it's a fill-in-the-blank question
        if (question.getCorrectAnswerIndex() < 0) {
            // Get the user's answer from the EditText
            String userAnswer = mFillInTheBlankEditText.getText().toString().trim();

            // Get the correct answer for the fill-in-the-blank question
            String correctAnswer = question.getAnswer(); // Assuming there's only one correct answer for fill-in-the-blank

            // Check if the user's answer matches the correct answer (case-insensitive)
            if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                mScore++;
            } else {
                pushToWrongAnswers(question.getQuestionText(), userAnswer);
            }
        } else {
            // Handle multiple-choice questions as before
            int selectedRadioButtonId = mRadioGroup.getCheckedRadioButtonId();
            if (selectedRadioButtonId == -1) {
                return; // No option selected
            }
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            if (selectedRadioButton == null) {
                return;
            }
            String selectedAnswer = selectedRadioButton.getText().toString();

            String[] answerOptions = question.getAnswerOptions();
            if (answerOptions == null || answerOptions.length <= question.getCorrectAnswerIndex() || question.getCorrectAnswerIndex() < 0) {
                return;
            }

            // Get the correct answer from the options
            String correctAnswer = answerOptions[question.getCorrectAnswerIndex()];

            if (selectedAnswer.equals(correctAnswer)) {
                mScore++;
            } else {
                pushToWrongAnswers(question.getQuestionText(), selectedAnswer);
            }
        }


    }

    private void updateScore() {
        mScoreView.setText("Score: " + mScore);
    }


    private void finishQuiz() {
        // Check if the user has already completed the quiz
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("Java_hard_score").child(userId);
        scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // User has not scored, save the score to the database
                    mScoreRef.child(userId).setValue(mScore);
                }
                // Show results
                Intent intent = new Intent(getApplicationContext(), python_hard_2.class);
                intent.putExtra("score", mScore);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), easy3.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
        isTimerPaused = true;
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTimerPaused) {
            // Resume the timer if it was paused
            startCountdownTimer();
            isTimerPaused = false;
        }
    }
}