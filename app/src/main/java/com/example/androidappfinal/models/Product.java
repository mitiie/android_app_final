package com.example.androidappfinal.models;

import com.google.firebase.database.PropertyName;

import java.util.List;

public class Product {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private List<String> sizes;
    private boolean isFavorite;
    private double rating;

    public Product() {}

    public Product(String id, String name, String description, String imageUrl,
                   double price, List<String> sizes, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.sizes = sizes;
        this.isFavorite = isFavorite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    @PropertyName("isFavorite")
    public boolean isFavorite() {
        return isFavorite;
    }

    @PropertyName("isFavorite")
    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }
}