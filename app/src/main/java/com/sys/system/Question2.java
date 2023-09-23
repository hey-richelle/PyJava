package com.sys.system;

public class Question2 {
    public String question;
    public String option1;
    public String option2;
    public String option3;
    public String selectedOption;
    public String answer;

    public Question2() {
        // Default constructor required for calls to DataSnapshot.getValue(Question.class)
    }

    public Question2(String question, String option1, String option2, String option3, String selectedOption, String answer) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.selectedOption = selectedOption;
        this.answer = answer;
    }
}
