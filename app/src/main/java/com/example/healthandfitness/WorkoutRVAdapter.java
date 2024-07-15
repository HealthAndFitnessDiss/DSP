package com.example.healthandfitness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkoutRVAdapter extends RecyclerView.Adapter<WorkoutRVAdapter.MyViewHolder> {

    private final Workout workoutItem;

    public WorkoutRVAdapter(Workout workoutItem){
        this.workoutItem = workoutItem;
    }

    @NonNull
    @Override
    public WorkoutRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_workout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutRVAdapter.MyViewHolder holder, int position) {
        String exerciseNumber = Integer.toString(position + 1);
        String exerciseName = workoutItem.getExerciseNameList().get(position);
        String exerciseSets = workoutItem.getNumberSetsList().get(position);
        String exerciseSetsNumber = exerciseSets.concat(" sets");
        holder.txtExerciseNumber.setText(exerciseNumber);
        holder.txtExerciseName.setText(exerciseName);
        holder.txtExerciseSets.setText(exerciseSetsNumber);
    }

    @Override
    public int getItemCount() {
        return workoutItem.getExerciseNameList().size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtExerciseName, txtExerciseNumber, txtExerciseSets;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            txtExerciseName = itemView.findViewById(R.id.txtExerciseName);
            txtExerciseNumber = itemView.findViewById(R.id.txtExerciseNumber);
            txtExerciseSets = itemView.findViewById(R.id.txtExerciseSetsNumber);
        }
    }
}
