package com.sys.system;

public class Question10 {
    private String question;
    private String answer;

    // Default constructor (empty)
    public Question10() {
        // Default constructor required for Firebase database operations.
    }

    // Constructor with question and answer
    public Question10(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    // Getter and setter methods for question
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    // Getter and setter methods for answer
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
