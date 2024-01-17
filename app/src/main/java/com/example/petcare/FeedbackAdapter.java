package com.example.petcare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private Context context;
    private List<FeedbackModel> feedbackList;

    public FeedbackAdapter(Context context, List<FeedbackModel> feedbackList) {
        this.context = context;
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feedbackitem, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackModel feedback = feedbackList.get(position);

        holder.txvCareFeedbackName.setText(feedback.getFeedbackBy());
        holder.txvCareFeedback.setText(feedback.getFeedback());
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView txvCareFeedbackName, txvCareFeedback;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            txvCareFeedbackName = itemView.findViewById(R.id.txvCareFeedbackName);
            txvCareFeedback = itemView.findViewById(R.id.txvCareFeedback);
        }
    }
}
