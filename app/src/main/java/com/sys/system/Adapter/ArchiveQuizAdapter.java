package com.sys.system.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sys.system.R;

import java.util.List;

public class ArchiveQuizAdapter extends RecyclerView.Adapter<ArchiveQuizAdapter.ArchiveQuizViewHolder> {
    private List<String> archivedQuizNames; // List of archived quiz names
    private DatabaseReference mainDatabaseReference; // Reference to the main database

    public ArchiveQuizAdapter(List<String> archivedQuizNames, DatabaseReference mainDatabaseReference) {
        this.archivedQuizNames = archivedQuizNames;
        this.mainDatabaseReference = mainDatabaseReference;
    }

    @NonNull
    @Override
    public ArchiveQuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_archived_quiz, parent, false);
        return new ArchiveQuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveQuizViewHolder holder, int position) {
        String archivedQuizName = archivedQuizNames.get(position);
        holder.archivedQuizNameTextView.setText(archivedQuizName);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showConfirmationDialog(view.getContext(), archivedQuizName);
                return true; // Consume the long click event
            }
        });
    }

    @Override
    public int getItemCount() {
        return archivedQuizNames.size();
    }

    public void setArchivedQuizNames(List<String> archivedQuizNames) {
        this.archivedQuizNames = archivedQuizNames;
    }

    public class ArchiveQuizViewHolder extends RecyclerView.ViewHolder {
        TextView archivedQuizNameTextView;

        public ArchiveQuizViewHolder(@NonNull View itemView) {
            super(itemView);
            archivedQuizNameTextView = itemView.findViewById(R.id.archivedQuizNameTextView);
        }
    }

    private void showConfirmationDialog(Context context, String quizName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Unarchive Quiz");
        builder.setMessage("Are you sure you want to unarchive the quiz '" + quizName + "'?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                unarchiveQuiz(quizName);
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void unarchiveQuiz(String quizName) {
        DatabaseReference archiveDatabaseReference = FirebaseDatabase.getInstance().getReference("archive");
        DatabaseReference sourceNodeRef = archiveDatabaseReference.child(quizName);
        DatabaseReference targetNodeRef = mainDatabaseReference.child(quizName);

        sourceNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                targetNodeRef.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            // Unarchive successful, delete from the archive
                            deleteArchivedQuiz(quizName);

                            // Update the adapter
                            notifyDataSetChanged();
                        } else {
                            // Handle error
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void deleteArchivedQuiz(String quizName) {
        DatabaseReference archiveDatabaseReference = FirebaseDatabase.getInstance().getReference("archive");
        archiveDatabaseReference.child(quizName).removeValue();
        archivedQuizNames.remove(quizName);
    }
}
