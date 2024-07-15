package com.example.healthandfitness;

import java.util.ArrayList;

public class FoodArrayHandler {
    public static ArrayList<Food> arrayFood = new ArrayList<>();

    public ArrayList<Food> getArrayFood() {
        return arrayFood;
    }

    public void setArrayFood(Food foodItem ) {
        arrayFood.add(foodItem);
    }
}
