package com.example.petcare;

public class FeedbackModel {
    private String feedbackBy;
    private String feedback;

    public FeedbackModel() {
        // Default constructor required for Firestore
    }

    public FeedbackModel(String feedbackBy, String feedback) {
        this.feedbackBy = feedbackBy;
        this.feedback = feedback;
    }

    public String getFeedbackBy() {
        return feedbackBy;
    }

    public String getFeedback() {
        return feedback;
    }
}
