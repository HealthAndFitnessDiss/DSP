package com.example.healthandfitness;

public class User {

    private String email;
    private String firstName;
    private String surname;
    private String dob;
    // String to store the users unique firebase uid, to allow for easy referencing within the database
    // This attribute is also made final as once an account is created their firebase uid will not change
    private final String fbUID;

    public User(String fbUID, String email){
        this.fbUID = fbUID;
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getFbUID() {
        return fbUID;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getDob() {
        return dob;
    }
}
