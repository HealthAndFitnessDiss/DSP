package com.example.healthandfitness;

public class Food {

    //TODO: Add all necessary data that's associated with food item
    String name;
    String date;
    String uid;
    String calories;
    String measurementValue, measurementUnit, protein, foodRef;



    //Empty constructor for FireStore
    public Food(){}
    public Food(String name, String date, String calories, String measurementValue, String measurementUnit, String foodRef) {
        this.name = name;
        this.date = date;
        this.calories = calories;

        this.measurementValue = measurementValue;
        this.measurementUnit = measurementUnit;
        this.foodRef = foodRef;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(String measurementValue) {
        this.measurementValue = measurementValue;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }
    public String getFoodRef() {
        return foodRef;
    }

    public void setFoodRef(String foodRef) {
        this.foodRef = foodRef;
    }
}
