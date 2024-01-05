package com.sys.system.Class;
public class Student {
    private String id;
    private String image;
    private String name;
    private int score;
    private int easyScore;
    private int normalScore;
    private int hardScore;
    private int pictureScore;
    private  int python_hard;// Added pictureScore property

    public Student() {
        // Empty constructor required for Firebase
    }

    public Student(String id, String image, String name, int score) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getEasyScore() {
        return easyScore;
    }

    public void setEasyScore(int easyScore) {
        this.easyScore = easyScore;
    }

    public int getNormalScore() {
        return normalScore;
    }

    public void setNormalScore(int normalScore) {
        this.normalScore = normalScore;
    }

    public int getHardScore() {
        return hardScore;
    }

    public void setHardScore(int hardScore) {
        this.hardScore = hardScore;
    }

    public int getPython_hard() {
        return python_hard;
    }

    public void setPython_hard(int python_hard) {
        this.python_hard = python_hard;
    }

    public int getPictureScore() {
        return pictureScore;
    }

    public void setPictureScore(int pictureScore) {
        this.pictureScore = pictureScore;
    }

    @Override
    public String toString() {
        return name;
    }
}
