package com.sys.system;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
public class QuizAdapter10 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_QUIZ_ITEM = 1;
    private static final int VIEW_TYPE_DELETE_ALL_QUIZZES = 2;
    private final DatabaseReference archiveDatabaseReference;

    private List<QuizItem> quizItems;
    private List<String> nodeNames; // To keep track of the node names
    private List<Integer> nodeCounts; // To keep track of the count of items in each node
    private DatabaseReference databaseReference;
    private Context context;

    public QuizAdapter10(Context context, List<QuizItem> quizItems, List<String> nodeNames, List<Integer> nodeCounts) {
        this.context = context;
        this.quizItems = quizItems;
        this.nodeNames = nodeNames;
        this.nodeCounts = nodeCounts;

        archiveDatabaseReference = FirebaseDatabase.getInstance().getReference("archive"); // Use your desired archive node name
        // Get the reference to your Firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int getItemViewType(int position) {
        // Determine the view type based on position
        if (position < nodeNames.size()) {
            return VIEW_TYPE_QUIZ_ITEM;
        } else {
            return VIEW_TYPE_DELETE_ALL_QUIZZES;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder instanceof QuizViewHolder && position < nodeNames.size() && position < nodeCounts.size()) {
            QuizViewHolder quizViewHolder = (QuizViewHolder) holder;
            String nodeName = nodeNames.get(position);
            int count = nodeCounts.get(position);
            quizViewHolder.countTextView.setText(nodeName + " = " + count);

            quizViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDeleteMessageDialog(view, position);
                    return true; // Consume the long click event
                }
            });
        }
    }



    private void showDeleteMessageDialog(View view,int position) {
        String nodeName = nodeNames.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Quiz");
        builder.setItems(new CharSequence[]{"Delete "+nodeName+"?" , "Delete all quiz","Archive Quiz"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Delete Message option selected
                        showDeleteConfirmation(position);
                        break;

                    case 1:
                        // Delete Message option selected
                        showDeleteAllQuizzesConfirmation();
                        break;

                    case 2:
                        showArchiveConfirmation(position);
                        break;

                }
            }
        });
        builder.show();
    }

    private void showArchiveConfirmation(int position) {
        String nodeName = nodeNames.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Archive quiz?");
        builder.setMessage("Are you sure you want to archive quizzes inside " + nodeName + "?");
        builder.setPositiveButton("Archive", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                archivequiz(position);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void archivequiz(int position) {
        String nodeName = nodeNames.get(position);
        DatabaseReference sourceNodeRef = databaseReference.child(nodeName);
        DatabaseReference targetNodeRef = archiveDatabaseReference.child(nodeName);

        // Remove the 'notification_seen' node
        DatabaseReference notificationSeenRef = databaseReference.child("notifications_seen");
        notificationSeenRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    // The 'notification_seen' node has been removed successfully
                    Toast.makeText(context, "All Quiz notification has been reset", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to remove notification_seen.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sourceNodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                targetNodeRef.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            // Archive successful, delete from the main database
                            deleteNodeCount2(position);

                            Toast.makeText(context, "Quiz archived successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to archive quiz.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed to archive quiz.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        // Add 1 to include the "Delete All Quizzes" item
        return nodeCounts.size();
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView countTextView;
        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            countTextView = itemView.findViewById(R.id.countTextView);
        }
    }

    private void showDeleteConfirmation(int position) {
        String nodeName = nodeNames.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Quiz");
        builder.setMessage("Are you sure you want to delete all the quizzes inside " + nodeName + "?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteNodeCount(position);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    public void deleteNodeCount2(int position) {
        if (position >= 0 && position < nodeCounts.size()) {
            // Set the node count to 0 in the local list
            nodeCounts.set(position, 0);
            // Update the node count in the Firebase database
            String nodeName = nodeNames.get(position);
            DatabaseReference nodeRef = databaseReference.child(nodeName);
            nodeRef.removeValue(); // Set the count to 0 in the database
            notifyDataSetChanged();

            Toast.makeText(context, "Archive in " + nodeName, Toast.LENGTH_SHORT).show();
            finishAndRecreate();
        }
    }

    public void deleteNodeCount(int position) {
        if (position >= 0 && position < nodeCounts.size()) {
            // Set the node count to 0 in the local list
            nodeCounts.set(position, 0);
            // Update the node count in the Firebase database
            String nodeName = nodeNames.get(position);
            DatabaseReference nodeRef = databaseReference.child(nodeName);
            nodeRef.removeValue(); // Set the count to 0 in the database
            notifyDataSetChanged();

            Toast.makeText(context, "Quiz deleted in " + nodeName, Toast.LENGTH_SHORT).show();
            finishAndRecreate();
        }
    }

    private void showDeleteAllQuizzesConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete All Quizzes");
        builder.setMessage("Are you sure you want to delete all quizzes?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Perform the deletion of all quizzes here
                // Update nodeCounts and call notifyDataSetChanged
                for (int j = 0; j < nodeCounts.size(); j++) {
                    nodeCounts.set(j, 0);
                    String nodeName = nodeNames.get(j);
                    DatabaseReference nodeRef = databaseReference.child(nodeName);
                    nodeRef.removeValue();
                }
                notifyDataSetChanged();

                Toast.makeText(context, "All quizzes deleted.", Toast.LENGTH_SHORT).show();
                finishAndRecreate();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void finishAndRecreate() {
        ((Activity) context).finish();
        Intent intent = ((Activity) context).getIntent();
        ((Activity) context).overridePendingTransition(0, 0);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        ((Activity) context).finish();
    }

}
