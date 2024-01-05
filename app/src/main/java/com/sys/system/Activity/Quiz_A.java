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
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.Locale;
import java.util.Map;

public class Quiz_A extends AppCompatActivity {
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
    private boolean isTimerPaused = false;
    private boolean isTimerFrozen = false;
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
        setContentView(R.layout.activity_quiz);
        getSupportActionBar().setTitle("Java Easy Quiz");
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
        speech = findViewById(R.id.sound);
        mNextButton = findViewById(R.id.next_button);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Java_Easy");
        mScoreRef = FirebaseDatabase.getInstance().getReference().child("Java_Easy_scores");

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
                    Toast.makeText(Quiz_A.this, "Quiz is already done", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), set_A_end.class);
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
            DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("Java_Easy_scores").child(userId);
            scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        // User has not scored, start the countdown timer
                        mCountDownTimer.start();
                    } else {
                        // User has already scored, show a message
                        Toast.makeText(Quiz_A.this, "You have already completed the quiz", Toast.LENGTH_SHORT).show();
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
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("Java_Easy_scores").child(userId);
        scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // User has not scored, start the countdown timer
                    mCountDownTimer.start();
                } else {
                    // User has already scored, show a message
                    Toast.makeText(Quiz_A.this, "You have already completed the quiz", Toast.LENGTH_SHORT).show();
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
                if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getApplicationContext(), "Please choose an answer", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkAnswer();
                mCurrentQuestionIndex++;
                if (mCurrentQuestionIndex < mQuestionList.size()) {
                    resetCountdownTimer();
                    showQuestion();
                } else {
                    finishQuiz();
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
            DatabaseReference wrongAnswersRef = FirebaseDatabase.getInstance().getReference().child("Java_Easy_wrong").child(userId);

            // Create a unique key for each wrong answer
            String wrongAnswerKey = wrongAnswersRef.push().getKey();

            // Create a map to represent the wrong answer data
            Map<String, Object> wrongAnswerData = new HashMap<>();
            wrongAnswerData.put("question", questionText);
            wrongAnswerData.put("selectedOption", selectedOption);

            // Add the userId to the wrong answer data
            wrongAnswerData.put("userId", userId);

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
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("Java_Easy_scores").child(userId);
        scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The user already has a record of scores, so ask if they want to retake the quiz
                    String message = "You have already completed the quiz. Your scores are: " + dataSnapshot.getValue() +
                            "\nDo you want to retake the quiz?";
                    AlertDialog.Builder builder = new AlertDialog.Builder(Quiz_A.this);
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
                                    startCountdownTimer();
                                    Intent intent = new Intent(Quiz_A.this, Quiz_A.class);
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
                                    Intent intent = new Intent(Quiz_A.this, set_A_scores.class);
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

                // User answered incorrectly, add the wrong answer to "Wrong_Answers" section with user's ID
                DatabaseReference wrongAnswersRef = FirebaseDatabase.getInstance().getReference().child("Java_Easy_wrong").child(userId);
                String questionId = wrongAnswersRef.push().getKey();

                // Create a map to represent the wrong answer data
                Map<String, Object> wrongAnswerData = new HashMap<>();
                wrongAnswerData.put("question", question.getQuestionText());
                wrongAnswerData.put("selectedOption", selectedAnswer);

                // Add the userId to the wrong answer data
                wrongAnswerData.put("userId", userId);

                wrongAnswersRef.child(questionId).setValue(wrongAnswerData);
            }
        }
    }


    private void updateScore() {
        mScoreView.setText("Score: " + mScore);
    }

    private void finishQuiz() {
        // Check if the user has already completed the quiz
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("Java_Easy_scores").child(userId);
        scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // User has not scored, save the score to the database
                    mScoreRef.child(userId).setValue(mScore);
                }
                // Show results
                Intent intent = new Intent(getApplicationContext(), set_A_scores.class);
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
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            isTimerRunning = false;
            isTimerPaused = true;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Quiz_A.this, easy2.class);
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