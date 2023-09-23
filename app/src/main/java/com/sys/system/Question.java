package com.sys.system;

public class Question {
    private String mQuestionText;
    private String[] mAnswerOptions;
    private int mCorrectAnswerIndex;

    public Question(String questionText, String[] answerOptions, int correctAnswerIndex) {
        mQuestionText = questionText;
        mAnswerOptions = answerOptions;
        mCorrectAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() {
        return mQuestionText;
    }

    public String[] getAnswerOptions() {
        return mAnswerOptions;
    }

    public int getCorrectAnswerIndex() {
        return mCorrectAnswerIndex;
    }
}
