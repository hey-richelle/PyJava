package com.sys.system.Class;



public class Question3 {
    private String answer;
    private String image1Url;
    private String image2Url;
    private String image3Url;
    private String image4Url;

    public Question3() {
        // Default constructor required for Firebase Realtime Database
    }

    public Question3(String answer, String image1Url, String image2Url, String image3Url, String image4Url) {
        this.answer = answer;
        this.image1Url = image1Url;
        this.image2Url = image2Url;
        this.image3Url = image3Url;
        this.image4Url = image4Url;
    }

    public String getAnswer() {
        return answer;
    }

    public String getImage1Url() {
        return image1Url;
    }

    public String getImage2Url() {
        return image2Url;
    }

    public String getImage3Url() {
        return image3Url;
    }

    public String getImage4Url() {
        return image4Url;
    }
}
