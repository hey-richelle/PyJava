package com.sys.system.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sys.system.R;

import java.util.List;
public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {

    private Context context;
    private List<String> quizModes;

    public QuizAdapter(Context context, List<String> quizModes) {
        this.context = context;
        this.quizModes = quizModes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz_mode, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String quizModeName = quizModes.get(position);
        int quizNumber = position + 1; // Index starts from 1
        holder.tvQuizMode.setText(quizNumber + ". " + quizModeName);
    }

    @Override
    public int getItemCount() {
        return quizModes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizMode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizMode = itemView.findViewById(R.id.tvQuizMode);
        }
    }
}
