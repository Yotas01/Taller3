package com.taller3.Module;

public class User {
    private String name;
    private String lastName;
    private String photo;
    private String email;
    private String state;
    private Long lat;
    private Long longitude;

    public User() {
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setLongitude(Long longitue){this.longitude = longitue;}
    public Long getLongitude(){return this.longitude;}
    public void setLatitude(Long lat){this.lat = lat;}
    public Long getLatitude(){return this.lat;}

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", photo='" + photo + '\'' +
                ", email='" + email + '\'' +
                ", state='" + state + '\'' +
                ", lat=" + lat +
                ", longitude=" + longitude +
                '}';
    }
}
