package com.example.healthandfitness;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DailyFoodRVAdapter extends RecyclerView.Adapter<DailyFoodRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<DailyFoodItem> dailyFoodItemArrayList;

    public DailyFoodRVAdapter(Context context, ArrayList<DailyFoodItem> dailyFoodItemArrayList) {
        this.context = context;
        this.dailyFoodItemArrayList = dailyFoodItemArrayList;
    }

    @NonNull
    @Override
    public DailyFoodRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //OnCreateView obviously inflates the layout, and gives the look to each row of the recycler view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycleview_row, parent, false);
        return new DailyFoodRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyFoodRVAdapter.MyViewHolder holder, int position) {
        // This is where the value of each view in the recyclerView xml layout is assigned,
        // dependant on the position on the recycler view
        holder.txtName.setText(dailyFoodItemArrayList.get(position).getName());
        holder.txtCalories.setText(dailyFoodItemArrayList.get(position).getCalories());
        holder.txtTime.setText(dailyFoodItemArrayList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        // item count communicates the number of items to be displayed

        return dailyFoodItemArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // Acts similarly to the onCreate method from Activity's, retrives the views from the xml
        // layout folder

        TextView txtName, txtCalories, txtTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtFoodName);
            txtCalories = itemView.findViewById(R.id.txtFoodCalories);
            txtTime = itemView.findViewById(R.id.txtFoodTime);
        }
    }

}
