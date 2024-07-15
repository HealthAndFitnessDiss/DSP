package com.example.healthandfitness;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import android.content.Context;
import android.widget.LinearLayout;

public class AdapterDailyFood {
    Context context;
    ArrayList<Food> dailyFoodItems;

    public AdapterDailyFood(Context context, ArrayList<Food> dailyFoodItems){
        this.context = context;
        this.dailyFoodItems = dailyFoodItems;
    }


}
