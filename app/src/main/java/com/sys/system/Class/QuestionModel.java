package com.sys.system.Class;

public class QuestionModel {
    private String question;
    private String userId;

    public QuestionModel() {
        // Default constructor required for Firebase
    }

    public QuestionModel(String question, String userId) {
        this.question = question;
        this.userId = userId;
    }

    public String getQuestion() {
        return question;
    }

    public String getUserId() {
        return userId;
    }
}
