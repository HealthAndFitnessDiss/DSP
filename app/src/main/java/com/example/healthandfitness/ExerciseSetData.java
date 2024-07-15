package com.example.healthandfitness;

public class ExerciseSetData {
    int setNumber;
    int reps;
    Double weight;

    public ExerciseSetData(int setNumber, int reps, Double weight) {
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
