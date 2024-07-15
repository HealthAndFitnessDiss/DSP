package com.example.healthandfitness;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FoodRVAdapter extends RecyclerView.Adapter<FoodRVAdapter.MyViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<Food> foodItemArrayList;
    ArrayList<Food> originalFoodItemList; //Keeps track of the original

    public FoodRVAdapter(Context context, ArrayList<Food> foodItemArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.foodItemArrayList = foodItemArrayList;
        this.originalFoodItemList = new ArrayList<>(foodItemArrayList); //Copy the original into this array list
        this.recyclerViewInterface = recyclerViewInterface;
    }



    public void setFilteredList(ArrayList<Food> filteredList){
        this.foodItemArrayList = filteredList;
        notifyDataSetChanged();

    }

    public void resetList(){
        this.foodItemArrayList = new ArrayList<>(originalFoodItemList);
        notifyDataSetChanged();
    }

    public Food getFoodItemAt(int position){
        return foodItemArrayList.get(position);
    }

    @NonNull
    @Override
    public FoodRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //OnCreateView obviously inflates the layout, and gives the look to each row of the recycler view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycleview_row, parent, false);
        return new FoodRVAdapter.MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodRVAdapter.MyViewHolder holder, int position) {
        // This is where the value of each view in the recyclerView xml layout is assigned,
        // dependant on the position on the recycler view
        holder.txtName.setText(foodItemArrayList.get(position).getName());
        holder.txtCalories.setText(foodItemArrayList.get(position).getCalories());
        holder.txtServing.setText(foodItemArrayList.get(position).getMeasurementValue().concat(" ").concat(foodItemArrayList.get(position).getMeasurementUnit()));

    }

    @Override
    public int getItemCount() {
        // item count communicates the number of items to be displayed

        return foodItemArrayList.size();
    }

    //Class below must be static
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // Acts similarly to the onCreate method from Activity's, retrives the views from the xml
        // layout folder

        TextView txtName, txtCalories, txtServing;
        //Must pass through my recyclerViewInterface in the constructor of viewHolder, so the class can be kept static and so the interface can be accessed
        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtFoodName);
            txtCalories = itemView.findViewById(R.id.txtFoodCalories);
            txtServing = itemView.findViewById(R.id.txtFoodTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null){
                        //Grab the position from the adapter to communicate to the onClick Method in the interface
                        int position = getAdapterPosition();
                        Log.d("Adapter", "Position: " + position);
                        //Make sure the position retrieved is valid
                        if(position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClicked(position);
                        }
                    }
                }
            });
        }
    }

}
