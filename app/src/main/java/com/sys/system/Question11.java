package com.sys.system;

public class Question11 {
    private String mQuestionText;
    private String[] mAnswerOptions;
    private int mCorrectAnswerIndex;
    private String answer;

    public Question11(String questionText, String[] answerOptions, int correctAnswerIndex,String answer) {
        mQuestionText = questionText;
        mAnswerOptions = answerOptions;
        mCorrectAnswerIndex = correctAnswerIndex;
        this.answer = answer;
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
    public String getAnswer() {
        return answer;
    }
}
