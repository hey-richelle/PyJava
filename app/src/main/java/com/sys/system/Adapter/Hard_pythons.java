package com.sys.system.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sys.system.Activity.Python_hard1;
import com.sys.system.Activity.Update_hard_python;
import com.sys.system.R;

import java.util.List;

public class Hard_pythons extends RecyclerView.Adapter<Hard_pythons.QuizViewHolder> {
    private Context context;
    private List<DataSnapshot> quizDataSnapshots;

    public Hard_pythons (Context context, List<DataSnapshot> quizDataSnapshots) {
        this.context = context;
        this.quizDataSnapshots = quizDataSnapshots;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item_layout, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        DataSnapshot dataSnapshot = quizDataSnapshots.get(position);
        holder.bind(dataSnapshot);
    }

    @Override
    public int getItemCount() {
        return quizDataSnapshots.size();
    }
    public class QuizViewHolder extends RecyclerView.ViewHolder {
        private TextView answerTextView;
        private TextView QuestionEditext;
        private TextView option1EditText;
        private TextView option2EditText;
        private TextView option3EditText;
        private RadioGroup optionsRadioGroup;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            QuestionEditext = itemView.findViewById(R.id.et_question);
            answerTextView = itemView.findViewById(R.id.Answer); // Add the TextView for Answer
            option1EditText = itemView.findViewById(R.id.et_option1);
            option2EditText = itemView.findViewById(R.id.et_option2);
            option3EditText = itemView.findViewById(R.id.et_option3);
            optionsRadioGroup = itemView.findViewById(R.id.options_radio_group);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        final DataSnapshot dataSnapshot = quizDataSnapshots.get(position);
                        final String questionId = dataSnapshot.getKey();

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Options");
                        builder.setItems(new CharSequence[]{"Update", "Delete"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                if (which == 0) {
                                    // Update option clicked
                                    Intent intent;
                                    String option1 = dataSnapshot.child("option1").getValue(String.class);
                                    String option2 = dataSnapshot.child("option2").getValue(String.class);
                                    String option3 = dataSnapshot.child("option3").getValue(String.class);

                                    // Check if all three options are null
                                    if (option1 == null && option2 == null && option3 == null) {
                                        intent = new Intent(context, Update_hard_python.class);
                                    } else {
                                        intent = new Intent(context, Python_hard1.class);
                                        intent.putExtra("option1", option1);
                                        intent.putExtra("option2", option2);
                                        intent.putExtra("option3", option3);
                                    }

                                    intent.putExtra("questionId", questionId);
                                    intent.putExtra("question", dataSnapshot.child("question").getValue(String.class));
                                    intent.putExtra("answer", dataSnapshot.child("answer").getValue(String.class));
                                    intent.putExtra("selectedOption", dataSnapshot.child("selectedOption").getValue(String.class));
                                    context.startActivity(intent);
                                    ((Activity) context).overridePendingTransition(0, 0); // Disable animation
                                    ((Activity) context).finish();

                                } else if (which == 1) {
                                    // Delete option clicked
                                    final ProgressDialog progressDialog = new ProgressDialog(context);
                                    progressDialog.setMessage("Deleting quiz question...");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();

                                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                    DatabaseReference questionsRef = database.child("Python_Hard").child(questionId);
                                    questionsRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss(); // Dismiss the progress dialog
                                            // Show success message to user
                                            Toast.makeText(context, "Quiz question deleted successfully!", Toast.LENGTH_SHORT).show();
                                            notifyItemRemoved(position);
                                        }
                                    });
                                }
                            }
                        });
                        builder.show();
                    }
                    return true;
                }
            });
        }


        public void bind(DataSnapshot dataSnapshot) {
            String question = dataSnapshot.child("question").getValue(String.class);
            String answer = dataSnapshot.child("answer").getValue(String.class);
            String option1 = dataSnapshot.child("option1").getValue(String.class);
            String option2 = dataSnapshot.child("option2").getValue(String.class);
            String option3 = dataSnapshot.child("option3").getValue(String.class);
            String selectedOption = dataSnapshot.child("selectedOption").getValue(String.class);

            answerTextView.setText("Answer: " + answer); // Display the Answer
            QuestionEditext.setText("Question " + question);

            // Check the appropriate radio button based on the selectedOption
            if (selectedOption != null) {
                if (selectedOption.equals(option1)) {
                    optionsRadioGroup.check(R.id.option1_radio_button);
                } else if (selectedOption.equals(option2)) {
                    optionsRadioGroup.check(R.id.option2_radio_button);
                } else if (selectedOption.equals(option3)) {
                    optionsRadioGroup.check(R.id.option3_radio_button);
                }
            }

            // Check if option1 is null and hide the TextView if it is
            if (option1 == null) {
                option1EditText.setVisibility(View.GONE);
            } else {
                option1EditText.setText("option1: " + option1);
                option1EditText.setVisibility(View.VISIBLE);
            }

            // Check if option2 is null and hide the TextView if it is
            if (option2 == null) {
                option2EditText.setVisibility(View.GONE);
            } else {
                option2EditText.setText("option2: " + option2);
                option2EditText.setVisibility(View.VISIBLE);
            }

            // Check if option3 is null and hide the TextView if it is
            if (option3 == null) {
                option3EditText.setVisibility(View.GONE);
            } else {
                option3EditText.setText("option3: " + option3);
                option3EditText.setVisibility(View.VISIBLE);
            }
        }

    }
}