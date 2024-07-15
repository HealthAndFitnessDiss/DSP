package com.example.healthandfitness;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DailyFoodArrayHandler {

    public static ArrayList<DailyFoodItem> arrayDailyFood = new ArrayList<>();

    public void setArrayDailyFood(DailyFoodItem food){
        arrayDailyFood.add(food);
    }
    //TODO: Maybe need to better figure out the process of this method and when to use
    // for example if the user has the ability to delete a Food Item the array also needs to be updated
    public void emptyArrayDailyFood(){
        arrayDailyFood.clear();
    }
    public ArrayList<DailyFoodItem> getArrayDailyFood(){return arrayDailyFood;}
}
