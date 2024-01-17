package com.example.petcare;

import com.google.firebase.firestore.PropertyName;

public class Jobs {

    String PetType;
    String Location;
    String Price;
    String Duration;

    String userId;


    String imageUrl;
    public Jobs() {
    }

    public Jobs(String petType, String location, String price, String duration, String imageUrl) {
        PetType = petType;
        Location = location;
        Price = price;
        Duration = duration;
        this.imageUrl = imageUrl;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPetType() {
        return PetType;
    }

    public void setPetType(String petType) {
        PetType = petType;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
    }


}
