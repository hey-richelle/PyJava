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
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;
import com.sys.system.Class.Question3;
import com.sys.system.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class picture_quiz extends AppCompatActivity {
    private long mTimeRemaining;
    private boolean isTimerRunning = false;
    private CountDownTimer mCountDownTimer;
    private DatabaseReference mScoreRef;
    LinearLayout   answerButtonsLayout;
    Button resetButton;

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
    private int selectedCharacterIndex = 0;

    private ImageView questionImage1, questionImage2, questionImage3, questionImage4;
    private TextView answerInput;
    private Button submitAnswerBtn;
    private TextView getAnswerTextView;
    private TextView outoff,mTimeView;
    private DatabaseReference databaseReference;
    private List<Question3> questions;
    private int currentQuestionIndex;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_quiz);
        outoff = findViewById(R.id.outof_question);
        questionImage1 = findViewById(R.id.questionImage1);
        questionImage2 = findViewById(R.id.questionImage2);
        mTimeView = findViewById(R.id.time_view);
        resetButton = findViewById(R.id.resetButton);
        getAnswerTextView = findViewById(R.id.getAnswerTextView);
        questionImage3 = findViewById(R.id.questionImage3);
        questionImage4 = findViewById(R.id.questionImage4);
        answerInput = findViewById(R.id.answerInput);
        answerButtonsLayout = findViewById(R.id.answerButtonsLayout);
        submitAnswerBtn = findViewById(R.id.submitAnswerBtn);
        Drawable homeButton = getResources().getDrawable(R.drawable.ic_home);
// Set its color to black
        homeButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
// Set the modified drawable as the home button icon
        getSupportActionBar().setHomeAsUpIndicator(homeButton);
        getSupportActionBar().setTitle("Picture game");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#606C5D")));


        databaseReference = FirebaseDatabase.getInstance().getReference().child("4pics");

        // Read the data from the database and set the image URLs
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    questions = new ArrayList<>();
                    for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve the question data
                        String answer = questionSnapshot.child("answer").getValue(String.class);
                        String image1Url = questionSnapshot.child("image1_url").getValue(String.class);
                        String image2Url = questionSnapshot.child("image2_url").getValue(String.class);
                        String image3Url = questionSnapshot.child("image3_url").getValue(String.class);
                        String image4Url = questionSnapshot.child("image4_url").getValue(String.class);
                        Question3 question = new Question3(answer, image1Url, image2Url, image3Url, image4Url);
                        questions.add(question);
                    }

                    // Shuffle the questions
                    Collections.shuffle(questions);

                    // Start the quiz with the first question
                    currentQuestionIndex = 0;
                    score = 0;
                    displayQuestion();
                    startCountdownTimer();

                    // Start the countdown timer
                } else {
                    // Quiz data does not exist
                    // Check if the quiz is already completed
                    DatabaseReference completedQuizRef = FirebaseDatabase.getInstance().getReference().child("4pics");
                    completedQuizRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Quiz has already been completed
                                Intent intent = new Intent(getApplicationContext(), picture_end.class);
                                startActivity(intent);
                                finish(); // Close the activity or take other appropriate action
                            } else {
                                // Handle no questions found in the database
                                Log.e("DatabaseError", "No questions found in database");
                                Toast.makeText(picture_quiz.this, "Quiz is already done", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), picture_end.class);
                                startActivity(intent);
                                finish(); // Close the activity or take other appropriate action
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });


        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the answer input field
                answerInput.setText("");

                // Make all letter buttons visible again
                for (int i = 0; i < answerButtonsLayout.getChildCount(); i++) {
                    View child = answerButtonsLayout.getChildAt(i);
                    if (child instanceof Button) {
                        Button letterButton = (Button) child;
                        letterButton.setVisibility(View.VISIBLE);
                    }
                }
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
                String userAnswer = answerInput.getText().toString().trim();

                if (TextUtils.isEmpty(userAnswer)) {
                    // No answer is provided, proceed to the next question
                    currentQuestionIndex++; // Move to the next question
                    if (currentQuestionIndex < questions.size()) {
                        resetCountdownTimer(); // Reset the timer to 1 minute
                        displayQuestion();
                    } else {
                        finishQuiz();
                    }
                } else {
                    // An answer is provided, check the answer and proceed to the next question
                    checkAnswer(userAnswer);
                    currentQuestionIndex++; // Move to the next question
                    if (currentQuestionIndex < questions.size()) {
                        resetCountdownTimer(); // Reset the timer to 1 minute
                        displayQuestion();
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
    }

    private void finishQuiz() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Record the final score
            int finalScore = score;

            // Save the final score in the Firebase database under the user's ID
            DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("picture_score").child(userId);
            scoresRef.setValue(finalScore);

            // Navigate to a new activity to view the score
            Intent intent = new Intent(picture_quiz.this, picture_score.class);
            intent.putExtra("score", finalScore);
            startActivity(intent);
            finish();
        } else {
            // Handle the case where the user is not authenticated
            // You can show an error message or redirect the user to the login page.
            // For example, you can add the code to show a toast message:
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show();
        }
    }


    private void resetCountdownTimer() {
        mCountDownTimer.cancel(); // Cancel the existing timer
        isTimerRunning = false; // Set the timer running flag to false
        startCountdownTimer(); // Start a new timer
    }






    private void displayQuestion() {
        // Check if the user has a score in the "scores" node
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("picture_score").child(userId);
            scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // The user has a score, navigate to the score activity
                        int userScore = dataSnapshot.getValue(Integer.class);
                        Intent intent = new Intent(picture_quiz.this, picture_score.class);
                        intent.putExtra("score", userScore);
                        startActivity(intent);
                        finish();
                    } else {
                        // User has not completed the quiz, continue with displaying the next question
                        if (currentQuestionIndex < questions.size()) {
                            Question3 question = questions.get(currentQuestionIndex);

                            Picasso.get().load(question.getImage1Url()).into(questionImage1);
                            Picasso.get().load(question.getImage2Url()).into(questionImage2);
                            Picasso.get().load(question.getImage3Url()).into(questionImage3);
                            Picasso.get().load(question.getImage4Url()).into(questionImage4);

                            // Get the answer for the current question
                            String getAnswer = question.getAnswer();

                            // Set the answer to the getAnswerTextView
                            getAnswerTextView.setText(getAnswer);

                            // Clear the answerInput field
                            answerInput.setText("");

                            String outOfText = (currentQuestionIndex + 1) + "/" + questions.size();
                            outoff.setText(outOfText);

                            // Remove all the previous letter buttons before adding new ones
                            answerButtonsLayout.removeAllViews();

                            // Set an OnClickListener for the submitAnswerBtn
                            submitAnswerBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String userAnswer = answerInput.getText().toString().trim();
                                    if (TextUtils.isEmpty(userAnswer)) {
                                        Toast.makeText(picture_quiz.this, "Please enter your answer", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    // Check the user's answer
                                    checkAnswer(userAnswer);

                                    // Reset the countdown timer for the next question
                                    resetCountdownTimer();

                                    // Proceed to the next question
                                    currentQuestionIndex++;
                                    displayQuestion();
                                }
                            });

                            List<Character> answerLetters = new ArrayList<>();
                            for (char c : getAnswer.toCharArray()) {
                                answerLetters.add(c);
                            }

// Shuffle the answer letters to scramble the order
                            Collections.shuffle(answerLetters);
                            answerButtonsLayout.removeAllViews();
                            View.OnClickListener letterButtonClickListener = new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Button letterButton = (Button) v;
                                    String selectedLetter = letterButton.getText().toString();
                                    answerInput.append(selectedLetter);
                                    letterButton.setVisibility(View.GONE);
                                }
                            };

                            for (Character letter : answerLetters) {
                                final Button letterButton = new Button(picture_quiz.this);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        0, // Set the width to 0
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                layoutParams.weight = 1; // Set the weight to 1 for equal distribution
                                layoutParams.setMargins(5, 5, 5, 5);
                                letterButton.setLayoutParams(layoutParams);
                                letterButton.setText(String.valueOf(letter));
                                letterButton.setTextSize(15);
                                letterButton.setBackgroundResource(R.drawable.letter_button_background); // You can create a drawable for the button background
                                letterButton.setTextColor(Color.BLACK);
                                letterButton.setAllCaps(false);

                                // Set the common OnClickListener for all letterButtons
                                letterButton.setOnClickListener(letterButtonClickListener);

                                // Add the letterButton to the answerButtonsLayout
                                answerButtonsLayout.addView(letterButton);
                            }
                        } else {
                            // Quiz finished, record the score in the "scores" node
                            DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("picture_score").child(userId);
                            scoresRef.setValue(score);

                            // Navigate to a new activity to view the score
                            Intent intent = new Intent(picture_quiz.this, picture_score.class);
                            intent.putExtra("score", score);
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }
    }



    private void checkAnswer(String userAnswer) {
        Question3 question = questions.get(currentQuestionIndex);
        String correctAnswer = question.getAnswer();

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            score++;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel(); // Cancel the countdown timer in case the activity is destroyed
        }
    }
}
