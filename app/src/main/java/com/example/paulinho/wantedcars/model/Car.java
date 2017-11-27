package com.example.paulinho.wantedcars.model;

/**
 * Created by paulinho on 11/24/2017.
 */

public class Car {
    private int id;
    private String name;
    private String year;
    private String description;
    private String category;
    private byte[] image;

    public Car( int id,String name, String year, String description, String category, byte[] image) {
        this.name = name;
        this.year = year;
        this.image = image;
        this.id = id;
        this.description = description;
        this.category =category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Car:" +
                "Vehicle: " + name + '\n' +
                "year: " + year + '\n' +
                "description: " + description + '\n' +
                "category: " + category;
    }
}
