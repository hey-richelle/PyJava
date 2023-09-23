package com.sys.system;

public class FirebaseData {
    private String userId;
    private String question;
    private String selectedOption;

    // Default constructor
    public FirebaseData() {
        // Default constructor required for Firebase
    }

    // Constructor with parameters
    public FirebaseData(String userId, String question, String selectedOption) {
        this.userId = userId;
        this.question = question;
        this.selectedOption = selectedOption;
    }

    // Getter for userId
    public String getUserId() {
        return userId;
    }

    // Setter for userId
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter for question
    public String getQuestion() {
        return question;
    }

    // Setter for question
    public void setQuestion(String question) {
        this.question = question;
    }

    // Getter for selectedOption
    public String getSelectedOption() {
        return selectedOption;
    }

    // Setter for selectedOption
    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
}
