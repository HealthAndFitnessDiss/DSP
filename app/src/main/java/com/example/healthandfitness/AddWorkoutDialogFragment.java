package com.example.healthandfitness;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddWorkoutDialogFragment extends DialogFragment implements RecyclerViewInterface{

    RecyclerView recyclerExerciseSets, recyclerSearchExercise, recyclerWorkout;
    ExerciseSetRVAdapter exerciseSetRVAdapter;
    List<ExerciseSetData> exerciseSetDataList;
    ImageButton btnBackToSearch;
    Button btnAddSet, btnSaveSets;
    SearchView searchExercise;
    ArrayList<Exercise> exerciseItemList;
    ExerciseRVAdapter exerciseRVAdapter;
    WorkoutRVAdapter workoutRVAdapter;
    CollectionReference cExerciseMapRef, cExerciseSetsRef, cWorkoutRef, cDailyWorkoutRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mDatabase;
    String uid;
    ConstraintLayout constraintSearchExercise, constraintSaveExercise;
    TextView txtExerciseName, txtExerciseMuscleGroup;
    DateTime dateHandler = new DateTime();
    //String exerciseSetsDocRef;

    Exercise clickedExerciseItem;

    int pos; // Variable so the position of the interface can be communicated

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_workout, null);
        builder.setView(view);
        // Code necessary to remove unwanted shading behind corner radius
        // of the dialog card
        Dialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser != null){
            uid = mUser.getUid();
        }
        cExerciseMapRef = mDatabase.collection("map exercise");
        cExerciseSetsRef = mDatabase.collection("exercise sets");
        cWorkoutRef = mDatabase.collection("workout");
        cDailyWorkoutRef = mDatabase.collection("daily workout");

        readExerciseItems();

        constraintSearchExercise = view.findViewById(R.id.constraintSearchExercise);
        constraintSaveExercise = view.findViewById(R.id.constraintSaveExercise);
        txtExerciseName = view.findViewById(R.id.txtExerciseName);
        txtExerciseMuscleGroup = view.findViewById(R.id.txtExerciseMuscleGroup);


        recyclerExerciseSets = view.findViewById(R.id.recyclerExerciseSets);
        btnAddSet = view.findViewById(R.id.btnAddSet);
        btnSaveSets = view.findViewById(R.id.btnSaveExerciseSets);

        updateExerciseSetRView();

        recyclerWorkout = view.findViewById(R.id.recyclerViewWorkout);

        btnAddSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int setNumber = exerciseSetDataList.size() + 1;
                exerciseSetDataList.add(new ExerciseSetData(setNumber, 0, 0.0));
                exerciseSetRVAdapter.notifyItemInserted(exerciseSetDataList.size() - 1);
            }
        });

       searchExercise = view.findViewById(R.id.searchExercise);
       recyclerSearchExercise = view.findViewById(R.id.recyclerViewSearchExercise);

       searchExercise.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String query) {
               return false;
           }

           @Override
           public boolean onQueryTextChange(String newText) {
               filterExerciseList(newText);
               return true;
           }
       });

       btnSaveSets.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               List<Map<String, Object>> exerciseSetsMapList = readInputSets();
               // If statement to make sure all fields have been filled before calling the
               // following methods
               if(exerciseSetsMapList != null){
                   String docRef = saveExerciseSets(exerciseSetsMapList);
                   Log.d("TEST", "String DocRef : " + docRef);
                   if(docRef != null){
                       Log.d("TEST", "saveWorkout Method Reached");
                       saveWorkout(docRef, exerciseSetsMapList);
                       backToSearch();
                       updateWorkoutRView(workoutItem);

                   }
               }

           }
       });

       btnBackToSearch = view.findViewById(R.id.btnBackToSearch);
       btnBackToSearch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               backToSearch();
           }
       });
       if(workoutItem!=null){
           updateWorkoutRView(workoutItem);
       }


    return dialog;}

    @Override
    public void onStart() {
        super.onStart();
        //Retrieve the dialog window
        Dialog dialog = getDialog();
        if(dialog!=null){
            int width = getResources().getDimensionPixelSize(R.dimen.dialog_workout_width);
            int height = getResources().getDimensionPixelSize(R.dimen.dialog_workout_height);
            dialog.getWindow().setLayout(width, height);
        }
        // Using this code and the code in onDismiss, the item workout isn't lost upon dialog dismissal
        // So if a user accidentally clicks off the dialog or goes to a different page before saving
        // an entire workout, they can still finish it when dialog is re-entered
        Workout w = new Workout();
        if(!w.getWorkoutHolder().isEmpty()){
            workoutItem = w.getWorkoutHolder().get(0);
            w.clearWorkoutHolder();
            //Clear so that the workoutItem doesn't store multiple times
        }
    }

    private void backToSearch(){
        searchExercise.setQuery("", false);
        searchExercise.clearFocus();
        constraintSaveExercise.setVisibility(View.GONE);
        constraintSearchExercise.setVisibility(View.VISIBLE);
        // Clear the data and reset the Recycler View for when the user clicks on another
        // Exercise Item
        updateExerciseSetRView();
    }

    private void readExerciseItems(){
        cExerciseMapRef
                .whereEqualTo("uid", uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("FireStore", "Snapshot Listener Failed", error);
                    return;
                }
                if (value != null && !value.isEmpty()){
                    for(DocumentSnapshot documentSnapshot : value){
                        List<Map<String, Object>> exerciseMapList = (List<Map<String, Object>>) documentSnapshot.get("exerciseMap");
                        if (exerciseMapList != null){
                            exerciseItemList = unMapExercise(exerciseMapList);
                            updateSearchExerciseRView(exerciseItemList);
                        }else{
                            Log.e("FireStoreQuery", "Map Exercise document contains no food map field");
                        }
                    }
                }
            }
        });
    }

    private ArrayList<Exercise> unMapExercise(List<Map<String, Object>> exerciseMapList){
        ArrayList<Exercise> exerciseList = new ArrayList<>();
        Map<String, Object> exerciseMapHolder;
        String name;
        String muscleGroup;
        String exerciseDocRef;
        for(int i = 0; i < exerciseMapList.size(); i++){
            exerciseMapHolder = exerciseMapList.get(i);
            name = exerciseMapHolder.get("name").toString();
            muscleGroup = exerciseMapHolder.get("muscleGroup").toString();
            exerciseDocRef = exerciseMapHolder.get("exerciseRef").toString();
            Exercise exerciseItem = new Exercise(name, muscleGroup, exerciseDocRef);
            exerciseList.add(exerciseItem);
        }
    return exerciseList;}
    public void filterExerciseList(String filter){
        ArrayList<Exercise> filteredExerciseList = new ArrayList<>();
        for(Exercise exerciseItem : exerciseItemList){
            if(exerciseItem.getName().toLowerCase().contains(filter.toLowerCase())){
                filteredExerciseList.add(exerciseItem);
            }
        }
        Log.d("SearchView", "Filtered list size: " + filteredExerciseList.size());
        if(filteredExerciseList.isEmpty()){
            //TODO: Implement code so that the recycler view empties when no results match
            // more intuitive for the user than leaving the search results and making a toast
            Toast.makeText(getActivity(), "No Created Food Items to Search Through", Toast.LENGTH_SHORT).show();
        }
        else{
            exerciseRVAdapter.setFilteredList(filteredExerciseList);
            Log.d("RecyclerView", "Filtered List set to RV Adapter");
        }
    }

    private void updateSearchExerciseRView(ArrayList<Exercise> exerciseItemList){
        // "this" is used to pass through the context of the recyclerViewInterface to the adapter's constructor
        if(exerciseRVAdapter == null){
            exerciseRVAdapter = new ExerciseRVAdapter(getActivity(), exerciseItemList, this);
            recyclerSearchExercise.setAdapter(exerciseRVAdapter);
            recyclerSearchExercise.setLayoutManager(new LinearLayoutManager(getActivity()));
        }else{
            exerciseRVAdapter.resetList();
        }
    }

    private void updateExerciseSetRView(){
        exerciseSetDataList = new ArrayList<>();
        exerciseSetRVAdapter = new ExerciseSetRVAdapter(exerciseSetDataList);
        recyclerExerciseSets.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerExerciseSets.setAdapter(exerciseSetRVAdapter);
    }

    private void updateWorkoutRView(Workout workoutItem){
        workoutRVAdapter = new WorkoutRVAdapter(workoutItem);
        recyclerWorkout.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerWorkout.setAdapter(workoutRVAdapter);
    }

    @Override
    public void onItemClicked(int position) {
        pos = position;
        Log.d("TEST", "Position: " + pos);

        clickedExerciseItem = exerciseRVAdapter.getExerciseItemAt(position);

        if(clickedExerciseItem != null){
            String name = clickedExerciseItem.getName();
            String muscleGroup = clickedExerciseItem.getMuscleGroup();
            txtExerciseName.setText(name);
            txtExerciseMuscleGroup.setText(muscleGroup);

            constraintSearchExercise.setVisibility(View.GONE);
            constraintSaveExercise.setVisibility(View.VISIBLE);
        }

    }

    private List<Map<String, Object>> readInputSets(){
        boolean empty = false;
        List<ExerciseSetData> exerciseSetDataList = exerciseSetRVAdapter.getSetDataList();
        List<Map<String, Object>> exerciseSetsMapList = new ArrayList<>();
        for(int i = 0; i < exerciseSetDataList.size(); i++){
            ExerciseSetData exerciseSetDataItem = exerciseSetDataList.get(i);
            if(exerciseSetDataItem.getReps() == 0 || exerciseSetDataItem.getWeight() == 0){
                Toast.makeText(getActivity(), "All Fields Must Be Filled", Toast.LENGTH_SHORT).show();
                exerciseSetsMapList = null;
                break;
            }
            else{
                Map<String, Object> mapHolder = new HashMap<>();
                String weight = exerciseSetDataItem.getWeight().toString();
                String reps = String.valueOf(exerciseSetDataItem.getReps());
                mapHolder.put("weight", weight);
                mapHolder.put("reps", reps);
                exerciseSetsMapList.add(mapHolder);
            }

        }
    return exerciseSetsMapList;}


    private String saveExerciseSets(List<Map<String, Object>> exerciseSetsMapList){
        //Made clickedExerciseItem a global variable, for easier reference, as the only
        //time this object should change is in the "itemOnClick" method, everywhere else
        //is the same reference
        String name = clickedExerciseItem.getName();
        String muscleGroup = clickedExerciseItem.getMuscleGroup();
        String exerciseDocRef = clickedExerciseItem.getExerciseDocRef();
        Map<String, Object> exerciseSetMap = new HashMap<>();
        exerciseSetMap.put("uid", uid);
        exerciseSetMap.put("exerciseSets", exerciseSetsMapList);
        exerciseSetMap.put("name", name);
        exerciseSetMap.put("muscleGroup", muscleGroup);
        exerciseSetMap.put("exerciseRef", exerciseDocRef);
        // As you can't reach the screen to save an exercise set without first creating at least
        // One exercise, I don't need to have an error catcher to create an exercise map document
        // onFailure, there will always be one if this method has been reached
        String exerciseSetsDocRef = cExerciseSetsRef.document().getId();
        cExerciseSetsRef.document(exerciseSetsDocRef)
                .set(exerciseSetMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.i("FireStore", "Successfully added new Exercise Sets Document ID : " + exerciseSetsDocRef);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FireStore", "Failure to create new Exercise Sets Document");
            }
        });
        Log.d("TEST", "DOCREF FOUND BEFORE RETURN STATEMENT : " + exerciseSetsDocRef);
        return exerciseSetsDocRef;}


    DocumentReference workoutDocRef = null;
    Workout workoutItem;
    private void saveWorkout(String exerciseSetsDocRef, List<Map<String, Object>> exerciseSetsMapList){
        //TODO: Lots of implementation need to figure out the best way to handle my data
        // across all the transactions
        //workoutDocRef = getCurrentWorkoutRef();
        List<String> listMuscleGroup = new ArrayList<>();
        List<String> listExerciseName = new ArrayList<>();
        List<String> listExerciseRef = new ArrayList<>();
        List<String> listExerciseSetsRef = new ArrayList<>();
        Map<String, Object> listSetsMapList = new HashMap<>();
        List<String> listNumberSets = new ArrayList<>();
        if(workoutDocRef == null){
            String date = dateHandler.getDateTime("date");
            listMuscleGroup.add(clickedExerciseItem.getMuscleGroup());
            listExerciseName.add(clickedExerciseItem.getName());
            listExerciseRef.add(clickedExerciseItem.getExerciseDocRef());
            listExerciseSetsRef.add(exerciseSetsDocRef);
            listSetsMapList.put("0", exerciseSetsMapList);
            listNumberSets.add(String.valueOf(exerciseSetsMapList.size()));
            Log.d("Test", "Number of Sets: " + exerciseSetsMapList.size());
            workoutItem = new Workout(uid, date, listMuscleGroup, listExerciseName, listExerciseRef, listExerciseSetsRef, listSetsMapList, listNumberSets);
            Map<String, Object> workoutMap = workoutItem.getWorkoutMap();
            cWorkoutRef
                    .add(workoutMap)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    workoutDocRef = documentReference;
                    Log.i("FireStore", "Successfully added new Workout Document");
                    Toast.makeText(getActivity(), "New Workout!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("FireStore", "Failure to add new Workout Document");
                }
            });
        }else{
            workoutItem.addWorkoutAttributes(clickedExerciseItem.getMuscleGroup(), clickedExerciseItem.getName(), clickedExerciseItem.getExerciseDocRef(), exerciseSetsDocRef, exerciseSetsMapList, String.valueOf(exerciseSetsMapList.size()));
            Map<String, Object> workoutMap = workoutItem.getWorkoutMap();
            workoutDocRef
                    .update(workoutMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.i("FireStore", "Successfully updated Workout Document ID : " + workoutDocRef.getId());
                    Toast.makeText(getActivity(), "Added to Workout!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("FireStore", "Failure updating Workout Document ID : " + workoutDocRef.getId());
                }
            });
        }
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Workout w = new Workout();
        w.storeWorkoutItem(workoutItem);
        super.onDismiss(dialog);
    }
}
