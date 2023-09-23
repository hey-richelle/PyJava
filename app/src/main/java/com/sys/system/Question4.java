package com.sys.system;

public class Question4 {
    public String question;
    public String option1;
    public String option2;
    public String option3;
    public String selectedOption;
    public String answer;
    public boolean isCorrect;

    public Question4() {
        // Default constructor required for calls to DataSnapshot.getValue(Question2.class)
    }

    public Question4(String question, String option1, String option2, String option3, String selectedOption, String answer, boolean isCorrect) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.selectedOption = selectedOption;
        this.answer = answer;
        this.isCorrect = isCorrect;
    }
}
