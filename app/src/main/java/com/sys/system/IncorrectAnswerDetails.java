package com.sys.system;

public class IncorrectAnswerDetails {
    private String questionText;
    private String correctAnswer;
    private String incorrectAnswer;

    public IncorrectAnswerDetails() {
        // Default constructor required for Firebase
    }

    public IncorrectAnswerDetails(String questionText, String correctAnswer, String incorrectAnswer) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswer = incorrectAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getIncorrectAnswer() {
        return incorrectAnswer;
    }
}
