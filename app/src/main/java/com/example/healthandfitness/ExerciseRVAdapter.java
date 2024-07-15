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

public class ExerciseRVAdapter extends RecyclerView.Adapter<ExerciseRVAdapter.MyViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<Exercise> exerciseItemArrayList;
    ArrayList<Exercise> originalExerciseItemList;

    public ExerciseRVAdapter(Context context, ArrayList<Exercise> exerciseItemArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.recyclerViewInterface = recyclerViewInterface;
        this.context = context;
        this.exerciseItemArrayList = exerciseItemArrayList;
        this.originalExerciseItemList = new ArrayList<>(exerciseItemArrayList);
    }
    public void setFilteredList(ArrayList<Exercise> filteredList){
        this.exerciseItemArrayList = filteredList;
        notifyDataSetChanged();
    }
    public void resetList(){
        this.exerciseItemArrayList = new ArrayList<>(originalExerciseItemList);
        notifyDataSetChanged();
    }
    public Exercise getExerciseItemAt(int position){return exerciseItemArrayList.get(position);}

    @NonNull
    @Override
    public ExerciseRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_exercise_search, parent, false);
        return new ExerciseRVAdapter.MyViewHolder(view, recyclerViewInterface);

    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseRVAdapter.MyViewHolder holder, int position) {
        holder.txtName.setText(exerciseItemArrayList.get(position).getName());
        holder.txtMuscleGroup.setText(exerciseItemArrayList.get(position).getMuscleGroup());
    }

    @Override
    public int getItemCount() {
        Log.d("RecyclerView", "List Size: " + exerciseItemArrayList.size());
        return exerciseItemArrayList.size();

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtName, txtMuscleGroup;
        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface){
            super(itemView);

            txtName = itemView.findViewById(R.id.txtExerciseName);
            txtMuscleGroup = itemView.findViewById(R.id.txtExerciseMuscleGroup);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null){
                        int position = getAdapterPosition();
                        Log.d("Adapter", "Position: " + position);
                        if (position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClicked(position);
                        }
                    }
                }
            });
        }
    }
}
