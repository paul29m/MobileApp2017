package com.example.paulinho.wantedcars.model;

/**
 * Created by paulinho on 11/24/2017.
 */

public class UserCar {
    private String email;
    private Car car;
    private Integer id;

    public UserCar(Integer id,String email, Car car) {
        this.email = email;
        this.car = car;
        this.id=id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public String toString() {
        return "UserCar{" +
                "email='" + email + '\'' +
                ", car=" + car +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
