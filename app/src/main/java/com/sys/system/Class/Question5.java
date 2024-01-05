package com.sys.system.Class;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question5 {
    private String mQuestionText;
    private int mCorrectAnswerIndex;
    private Map<String, String> mAnswerOptions;

    public Question5() {
        // Default constructor required for calls to DataSnapshot.getValue(Question.class)
    }


    public Question5(String questionText, List<String> answerOptions, int correctAnswerIndex) {
        mQuestionText = questionText;
        mCorrectAnswerIndex = correctAnswerIndex;

        // Convert answer options to a map
        mAnswerOptions = new HashMap<>();
        for (int i = 0; i < answerOptions.size(); i++) {
            mAnswerOptions.put("option" + (i + 1), answerOptions.get(i));
        }
    }

    public String getQuestionText() {
        return mQuestionText;
    }

    public int getCorrectAnswerIndex() {
        return mCorrectAnswerIndex;
    }

    public Map<String, String> getAnswerOptions() {
        return mAnswerOptions;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("questionText", mQuestionText);
        map.put("correctAnswerIndex", mCorrectAnswerIndex);
        map.put("answerOptions", mAnswerOptions);
        return map;
    }
}
