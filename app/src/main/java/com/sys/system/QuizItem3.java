package com.sys.system;

public class QuizItem3 {
    private String question;
    private String answer;
    private String option1;
    private String option2;
    private String option3;
    private String selectedOption;

    // Default constructor (required for Firebase)
    public QuizItem3() {
        // Default constructor is required for Firebase to deserialize the data
    }

    // Constructor to initialize the fields
    public QuizItem3(String question, String answer, String option1, String option2, String option3, String selectedOption) {
        this.question = question;
        this.answer = answer;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.selectedOption = selectedOption;
    }

    // Getters and setters (optional, but recommended for data encapsulation)
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
}
