package com.example.healthandfitness;

public class RetrieveRecentExercise {
    String name, exerciseREF, dateTime, uid, muscleGroup;

    //Empty constructor for Firestore
    public RetrieveRecentExercise(){}
    public RetrieveRecentExercise(String name, String exerciseREF, String dateTime, String uid, String muscleGroup) {
        this.name = name;
        this.exerciseREF = exerciseREF;
        this.dateTime = dateTime;
        this.uid = uid;
        this.muscleGroup = muscleGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExerciseREF() {
        return exerciseREF;
    }

    public void setExerciseREF(String exerciseREF) {
        this.exerciseREF = exerciseREF;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

}
