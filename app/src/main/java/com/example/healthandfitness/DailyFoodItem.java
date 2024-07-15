package com.example.healthandfitness;

public class DailyFoodItem {
    private String name;
    private String calories;
    private String time;
    private String foodDocRef;

    public DailyFoodItem(){
    }

    public DailyFoodItem(String name, String calories, String time, String foodDocRef){
        this.name = name;
        this.calories = calories;
        this.time = time;
        this.foodDocRef = foodDocRef;
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

    public String getFoodDocRef() {
        return foodDocRef;
    }

    public void setFoodDocRef(String foodDocRef) {
        this.foodDocRef = foodDocRef;
    }
}
