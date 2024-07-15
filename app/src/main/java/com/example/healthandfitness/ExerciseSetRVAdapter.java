package com.example.healthandfitness;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseSetRVAdapter extends RecyclerView.Adapter<ExerciseSetRVAdapter.MyViewHolder> {

    private List<ExerciseSetData> exerciseSetDataList;

    public ExerciseSetRVAdapter(List<ExerciseSetData> exerciseSetDataList){
        this.exerciseSetDataList = exerciseSetDataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_sets, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseSetRVAdapter.MyViewHolder holder, int position) {
        ExerciseSetData exerciseSetData = exerciseSetDataList.get(position);
        String setNumber = ("Set " + exerciseSetData.getSetNumber());
        holder.txtSetNumber.setText(setNumber);

        holder.edtWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()){
                    exerciseSetData.setWeight(Double.parseDouble(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });
        holder.edtReps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    exerciseSetData.setReps(Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //
            }
        });

    }

    @Override
    public int getItemCount() {
        return exerciseSetDataList.size();
    }

    public List<ExerciseSetData> getSetDataList(){

    return exerciseSetDataList;}

    public static class  MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtSetNumber;
        EditText edtWeight, edtReps;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            txtSetNumber = itemView.findViewById(R.id.txtSetNumber);
            edtWeight = itemView.findViewById(R.id.edtWeight);
            edtReps = itemView.findViewById(R.id.edtReps);
        }
    }

}
