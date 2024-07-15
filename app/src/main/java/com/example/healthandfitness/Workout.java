package com.example.healthandfitness;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Workout {
    String uid, date;
    List<String> exerciseSetsRefList, exerciseNameList, muscleGroupList, exerciseRefList;
    List<String> numberSetsList;

    Map<String, Object> exerciseSetsMLL;

    int counter;

    ArrayList<Workout> workoutItemHolder = new ArrayList<>();

    public Workout() {

    }

    public Workout(String uid, String date, List<String> muscleGroupList, List<String> exerciseNameList, List<String> exerciseRefList, List<String> exerciseSetsRefList, Map<String, Object> exerciseSetsMLL, List<String> numberSetsList) {
        this.counter = 1;
        this.uid = uid;
        this.date = date;
        this.exerciseSetsRefList = exerciseSetsRefList;
        this.muscleGroupList = muscleGroupList;
        this.exerciseNameList = exerciseNameList;
        this.exerciseRefList = exerciseRefList;
        this.exerciseSetsMLL = exerciseSetsMLL;
        this.numberSetsList = numberSetsList;
    }

    public void addWorkoutAttributes(String muscleGroup, String exerciseName, String exerciseRef, String exerciseSetsRef, List<Map<String, Object>> exerciseSetsMap, String numberSets){
        String mapName = Integer.toString(counter);
        this.muscleGroupList.add(muscleGroup);
        this.exerciseNameList.add(exerciseName);
        this.exerciseRefList.add(exerciseRef);
        this.exerciseSetsRefList.add(exerciseSetsRef);
        this.exerciseSetsMLL.put(mapName, exerciseSetsMap);
        this.numberSetsList.add(numberSets);
        this.counter += 1;
    }

    public void storeWorkoutItem(Workout workoutItem){
        workoutItemHolder.add(workoutItem);
    }

    public ArrayList<Workout> getWorkoutHolder(){
        return workoutItemHolder;
    }
    public void clearWorkoutHolder(){
        workoutItemHolder.clear();
    }

    public List<Map<String,Object>> getListExerciseSets(int position){
        String pos = Integer.toString(position);
        Log.d("Test", "Position for WorkoutMap: " + pos);
        return (List<Map<String, Object>>) this.exerciseSetsMLL.get(pos);
    }

    public int getSetsSize(int position){
        List<Map<String, Object>> listHolder = getListExerciseSets(position);
        if(listHolder != null){
            return listHolder.size();
        }
        else{
            return 0;
        }

    }

    public Map<String, Object> getWorkoutMap(){
        Map<String, Object> workoutMap = new HashMap<>();
        workoutMap.put("uid", this.uid);
        workoutMap.put("date", this.date);
        workoutMap.put("muscleGroupList", this.muscleGroupList);
        workoutMap.put("exerciseNameList", this.exerciseNameList);
        workoutMap.put("exerciseRefList", this.exerciseRefList);
        workoutMap.put("exerciseSetsRefList", this.exerciseRefList);
        workoutMap.put("exerciseSetsMLL", this.exerciseSetsMLL);
        workoutMap.put("numberSetsList", this.numberSetsList);
    return workoutMap;}


    public List<String> getNumberSetsList() {
        return numberSetsList;
    }

    public void setNumberSetsList(List<String> numberSetsList) {
        this.numberSetsList = numberSetsList;
    }

    public List<String> getExerciseRefList() {
        return exerciseRefList;
    }

    public void setExerciseRef(List<String> exerciseRefList) {
        this.exerciseRefList = exerciseRefList;
    }

    public List<String> getExerciseNameList() {
        return exerciseNameList;
    }

    public void setExerciseName(List<String> exerciseNameList) {
        this.exerciseNameList = exerciseNameList;
    }

    public List<String> getMuscleGroupList() {
        return muscleGroupList;
    }

    public void setMuscleGroupList(List<String> muscleGroupList) {
        this.muscleGroupList = muscleGroupList;
    }

    public Map<String, Object> getExerciseSetsMLL() {
        return exerciseSetsMLL;
    }

    public void setExerciseSetsMLL(Map<String, Object> exerciseSetsMLL) {
        this.exerciseSetsMLL = exerciseSetsMLL;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getExerciseSetsRefListList() {
        return exerciseSetsRefList;
    }

    public void setExerciseSetsRef(List<String> exerciseSetsRefList) {
        this.exerciseSetsRefList = exerciseSetsRefList;
    }

}
