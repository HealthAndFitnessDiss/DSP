package com.example.healthandfitness;

public class DailyFoodModel {

    String name, calories, time;
    public DailyFoodModel() {
    }

    public DailyFoodModel(String name, String calories, String time) {
        this.name = name;
        this.calories = calories;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
