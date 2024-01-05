package com.sys.system.Class;

public class Admin2 {
    private String email;
    private String phone;

    public Admin2() {
        // Default constructor required for Firebase
    }

    public Admin2(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
