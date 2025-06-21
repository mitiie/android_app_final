package com.example.androidappfinal.models;

public class Cart {
    private String id;
    private String name;
    private double price;
    private String size;
    private int quantity;
    private String imageUrl;
    private double rating;

    public Cart() {}

    public Cart(String id, String name, double price, String size, int quantity, String imageUrl, double rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.size = size;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.rating = rating;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}