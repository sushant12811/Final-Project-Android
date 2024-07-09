package com.example.restauranthub;

import java.io.Serializable;

public class RestaurantModel implements Serializable {
    private String restaurantName;
    private String restaurantLocation;
    private String restaurantImage;
    private String id;
    private String restaurantDetails;
    private int restaurantImageResourceId;
    private boolean isFavorite;

    public RestaurantModel() {
    }



    public RestaurantModel(String restaurantName, String location, String restaurantImage,
                           String details, String id, int restaurantImageResourceId, boolean isFavorite) {
        this.restaurantName = restaurantName;
        this.restaurantLocation = location;
        this.restaurantImage = restaurantImage;
        this.restaurantDetails = details;
        this.restaurantImageResourceId = restaurantImageResourceId;
        this.id = id;
        this.isFavorite = isFavorite;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantLocation() {
        return restaurantLocation;
    }

    public void setRestaurantLocation(String restaurantLocation) {
        this.restaurantLocation = restaurantLocation;
    }

    public String getRestaurantImage() {
        return restaurantImage;
    }

    public void setRestaurantImage(String restaurantImage) {
        this.restaurantImage = restaurantImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantDetails() {
        return restaurantDetails;
    }

    public void setRestaurantDetails(String restaurantDetails) {
        this.restaurantDetails = restaurantDetails;
    }

    public int getRestaurantImageResourceId() {
        return restaurantImageResourceId;
    }

    public void setRestaurantImageResourceId(int restaurantImageResourceId) {
        this.restaurantImageResourceId = restaurantImageResourceId;
    }
    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }


}
