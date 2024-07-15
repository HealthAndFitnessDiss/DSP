package com.example.healthandfitness;

public class Exercise {
    String name, muscleGroup, exerciseDocRef;

    public Exercise(String name, String muscleGroup, String exerciseDocRef) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.exerciseDocRef = exerciseDocRef;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getExerciseDocRef() {
        return exerciseDocRef;
    }

    public void setExerciseDocRef(String exerciseDocRef) {
        this.exerciseDocRef = exerciseDocRef;
    }
}
