package com.sys.system;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends ArrayAdapter<Student> implements Filterable {

    private Context context;
    private List<Student> studentList;
    private List<Student> filteredStudentList;
    private DatabaseReference easyScoreRef;
    private DatabaseReference normalScoreRef;
    private DatabaseReference hardScoreRef;// Added pictureScoreRef

    public StudentAdapter(Context context, List<Student> studentList) {
        super(context, 0, studentList);
        this.context = context;
        this.studentList = studentList;
        this.filteredStudentList = studentList;

        // Initialize Firebase database references
        easyScoreRef = FirebaseDatabase.getInstance().getReference("Java_Easy_scores");
        normalScoreRef = FirebaseDatabase.getInstance().getReference("Java_normal_score");
        hardScoreRef = FirebaseDatabase.getInstance().getReference("Java_hard_score");

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_student, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.studentImageView);
            viewHolder.nameTextView = convertView.findViewById(R.id.studentNameTextView);
            viewHolder.easyScoreTextView = convertView.findViewById(R.id.studentEasyScoreTextView);
            viewHolder.normalScoreTextView = convertView.findViewById(R.id.studentNormalScoreTextView);
            viewHolder.hardScoreTextView = convertView.findViewById(R.id.studentHardScoreTextView);// Added pictureScoreTextView
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Student student = filteredStudentList.get(position);
        viewHolder.nameTextView.setText(student.getName());
        viewHolder.easyScoreTextView.setText("Java Easy Score: " + student.getEasyScore());
        viewHolder.normalScoreTextView.setText("Java Normal Score: " + student.getNormalScore());
        viewHolder.hardScoreTextView.setText("Java Hard Score: " + student.getHardScore());// Set the picture score

        // Load student image using Picasso
        // Load student image using Picasso
        if (student.getImage() != null && !student.getImage().isEmpty()) {
            Picasso.get().load(student.getImage()).into(viewHolder.imageView);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.ic_baseline_person_24); // Replace "default_image" with the resource ID of your default image
        }

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showConfirmationDialog(student);
                return true;
            }
        });

        return convertView;
    }

    private void showConfirmationDialog(final Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.confirmation_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button btnResetEasyScore = dialogView.findViewById(R.id.btnResetEasyScore);
        Button btnResetNormalScore = dialogView.findViewById(R.id.btnResetNormalScore);
        Button btnResetHardScore = dialogView.findViewById(R.id.btnResetHardScore);
        Button btnResetAll = dialogView.findViewById(R.id.btnResetAll);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);



        btnResetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetConfirmationDialog(student);
                dialog.dismiss();
            }
        });

        btnResetEasyScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationForReset(student, "Java Easy");
                dialog.dismiss();
            }
        });

        btnResetNormalScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationForReset(student, "Java normal");
                dialog.dismiss();
            }
        });

        btnResetHardScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationForReset(student, "Java hard");
                dialog.dismiss();
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showResetConfirmationDialog(final Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Reset Scores");
        builder.setMessage("Are you sure you want to reset all of the scores for " + student.getName() + "?");
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteScores(student);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showConfirmationForReset(final Student student, final String scoreType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Reset");
        builder.setMessage("Are you sure you want to reset the " + scoreType.toUpperCase() + " score for " + student.getName() + "?");

        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetScore(student, scoreType);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void resetScore(Student student, String scoreType) {
        switch (scoreType) {
            case "Java Easy":
                // Remove the score from Firebase database
                easyScoreRef.child(student.getId()).removeValue();
                // Update the score locally
                student.setEasyScore(0);
                break;
            case "Java normal":
                // Remove the score from Firebase database
                normalScoreRef.child(student.getId()).removeValue();
                // Update the score locally
                student.setNormalScore(0);
                break;
            case "Java hard":
                // Remove the score from Firebase database
                hardScoreRef.child(student.getId()).removeValue();
                // Update the score locally
                student.setHardScore(0);
                break;
            default:
                break;
        }

        // Notify the adapter about the data change
        notifyDataSetChanged();

        // Show a toast to inform the user about the score reset
        Toast.makeText(context, scoreType.toUpperCase() + " mode score reset for " + student.getName(), Toast.LENGTH_SHORT).show();
    }


    private void deleteScores(Student student) {
        // Remove the scores from Firebase database
        easyScoreRef.child(student.getId()).removeValue();
        normalScoreRef.child(student.getId()).removeValue();
        hardScoreRef.child(student.getId()).removeValue();// Remove picture score
        // Update the scores locally
        student.setEasyScore(0);
        student.setNormalScore(0);
        student.setHardScore(0);

        notifyDataSetChanged();

        Toast.makeText(context, "Scores reset for " + student.getName(), Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Student> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    // No filter applied, return the original student list
                    filteredList.addAll(studentList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Student student : studentList) {
                        // Add students that match the search query to the filtered list
                        if (student.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(student);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredStudentList = (List<Student>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    private static class ViewHolder {
        public View deleteButton;
        TextView python_score;
        ImageView imageView;
        TextView nameTextView;
        TextView easyScoreTextView;
        TextView normalScoreTextView;
        TextView hardScoreTextView;// Added pictureScoreTextView
    }
}
