package com.sys.system;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Normal_pythons extends RecyclerView.Adapter<Normal_pythons.QuizViewHolder> {
    private Context context;
    private List<DataSnapshot> quizDataSnapshots;

    public Normal_pythons(Context context, List<DataSnapshot> quizDataSnapshots) {
        this.context = context;
        this.quizDataSnapshots = quizDataSnapshots;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item_layout2, parent, false);
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

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            QuestionEditext = itemView.findViewById(R.id.et_question);
            answerTextView = itemView.findViewById(R.id.Answer); // Add the TextView for Answer

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
                                    Intent intent = new Intent(context, Python_normal1.class);
                                    intent.putExtra("questionId", questionId);
                                    intent.putExtra("question", dataSnapshot.child("question").getValue(String.class));
                                    intent.putExtra("answer", dataSnapshot.child("answer").getValue(String.class));
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
                                    DatabaseReference questionsRef = database.child("Java_normal").child(questionId);
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

            answerTextView.setText("Answer: " + answer); // Display the Answer
            QuestionEditext.setText("Question: " + question);
        }
    }
}
