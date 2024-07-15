package com.example.healthandfitness;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddExerciseDialogFragment extends DialogFragment {

    FirebaseAuth mAuth;
    FirebaseFirestore mDatabase;
    FirebaseUser mUser;

    Button btnSaveExercise, btnAddSet;

    EditText edtName;

    AutoCompleteTextView drpMuscleGroup;

    ArrayAdapter<String> arrayAdapter;

    String uid;

    int layoutCounter;
    String exerciseDocREF;

    CollectionReference cMapExercise;

    LinearLayout layoutSet2, layoutSet3;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        layoutCounter = 0;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_exercise, null);

        builder.setView(view);

//        Dialog dialog = builder.create();
//        if (dialog.getWindow() != null) {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }




        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser != null){
            uid = mUser.getUid();
        }

        cMapExercise = mDatabase.collection("map exercise");

        btnSaveExercise = view.findViewById(R.id.btnSaveExercise);
        edtName = view.findViewById(R.id.edtExerciseName);


        drpMuscleGroup = view.findViewById(R.id.drpExerciseMuscleGroup);
        String[] arrayMuscleGroup = ArrayHandler.getArrayMuscleGroup();
        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_layout, arrayMuscleGroup);
        drpMuscleGroup.setAdapter(arrayAdapter);
        drpMuscleGroup.setText(arrayMuscleGroup[0], false);

        btnSaveExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This method saves the user created exercise to the exercise collection, it then
                //checks if they have added any sets for the exercise and if so saves that info to
                //exercise sets
                saveExercise(view);
            }
        });

        btnAddSet = view.findViewById(R.id.btnAddSet);
        layoutSet2 = view.findViewById(R.id.layoutSet2);
        layoutSet3 = view.findViewById(R.id.layoutSet3);

        btnAddSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutCounter == 0){
                    layoutCounter += 1;
                    layoutSet2.setVisibility(View.VISIBLE);
                } else if (layoutCounter == 1) {
                    layoutSet3.setVisibility(View.VISIBLE);
                    btnAddSet.setVisibility(View.GONE);
                }
            }
        });












        return builder.create();
    }

    public void saveExercise(View view){

        //TODO: Need to add an error catcher as to whether an exercise item has the same name

        String exerciseName = edtName.getText().toString();
        String muscleGroup = drpMuscleGroup.getText().toString();

        if (TextUtils.isEmpty(exerciseName)){
            edtName.setError("Exercise Name Required");
        }
        if (TextUtils.isEmpty(muscleGroup)){
            drpMuscleGroup.setError("Muscle Group Required");
        }

        else if (!TextUtils.isEmpty(exerciseName)){

            Map<String, Object> exercise = new HashMap<>();
            exercise.put("uid", uid);
            exercise.put("name", exerciseName);
            exercise.put("muscleGroup", muscleGroup);

            mDatabase.collection("exercise")
                    .add(exercise)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getActivity(), "Exercise Added Successfully", Toast.LENGTH_SHORT).show();
                            exerciseDocREF = documentReference.getId();
                            Exercise exerciseItem = new Exercise(exerciseName, muscleGroup, exerciseDocREF);
                            Map<String, Object> exerciseMap = mapExerciseItem(exerciseItem);
                            saveExerciseMap(exerciseMap);
                            saveExerciseSets(exerciseDocREF, view, exerciseName, muscleGroup);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error: Exercise item failed to save", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    public void saveExerciseMap(Map<String, Object> map){
        cMapExercise
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Log.i("FireStoreQuery", "Map Exercise query Successful");
                    QuerySnapshot querySnapshot = task.getResult();
                    // Debugging to check if any documents retrieved by the query
                    if (querySnapshot != null) {
                        Log.d("FireStoreQuery", "Number of Map Exercise documents found: " + querySnapshot.size());
                    } else {
                        Log.d("FireStoreQuery", "Map Exercise QuerySnapshot is null");
                    }
                    if (querySnapshot == null || querySnapshot.isEmpty()){
                        Log.d("FireStoreQuery", "Snapshot Empty : Map Exercise");
                        List<Map<String, Object>> exerciseMapList = new ArrayList<>();
                        exerciseMapList.add(map);
                        Map<String, Object> exerciseMap = new HashMap<>();
                        exerciseMap.put("uid", uid);
                        exerciseMap.put("exerciseMap", exerciseMapList);
                        cMapExercise
                                .add(exerciseMap)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i("FireStore", "Successfully created new Exercise Map document");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("FireStore", " Failed to create new Exercise Map document");
                            }
                        });
                    }else{
                        for(QueryDocumentSnapshot documentSnapshot : querySnapshot){
                            DocumentReference documentReference = documentSnapshot.getReference();
                            List<Map<String, Object>> documentMapList = (List<Map<String, Object>>) documentSnapshot.get("exerciseMap");
                            //Simple error catchers
                            if (documentMapList == null) {
                                Log.e("FireStore Query", "Daily Tracker Document Exists (" + documentReference.getId() + ") - But Daily Food Item Array does not");
                                documentMapList = new ArrayList<>();
                                Log.i("FireStore Query", "Creating New Empty Daily Food Item array");
                            }
                            documentMapList.add(map);
                            Map<String, Object> mapHolder = new HashMap<>();
                            mapHolder.put("exerciseMap", documentMapList);

                            documentReference
                                    .update(mapHolder)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.i("FireStore", "Map Exercise Document Successfully Updated");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("FireStore", "Failed to update Map Exercise Document");
                                }
                            });
                        }
                    }
                }else{
                    Log.e("FireStoreQuery", "Error Querying Daily Tracker Documents");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FireStoreQuery", "Query Failed", e);
            }
        });
    }

    public Map<String, Object> mapExerciseItem(Exercise exerciseItem){
        Map<String, Object> map = new HashMap<>();
        map.put("name", exerciseItem.getName());
        map.put("muscleGroup", exerciseItem.getMuscleGroup());
        map.put("exerciseRef", exerciseItem.getExerciseDocRef());
    return map;}

    public void saveExerciseSets(String exerciseDocREF, View view, String exerciseName, String muscleGroup){
//        mDatabase = FirebaseFirestore.getInstance();
//        mAuth = FirebaseAuth.getInstance();
//        mUser = mAuth.getCurrentUser();

        EditText edtSet1Weight = view.findViewById(R.id.edtWeightSetOne);
        EditText edtSet1Reps = view.findViewById(R.id.edtRepsSetOne);
        EditText edtSet2Weight = view.findViewById(R.id.edtWeightSetTwo);
        EditText edtSet2Reps = view.findViewById(R.id.edtRepsSetTwo);
        EditText edtSet3Weight = view.findViewById(R.id.edtWeightSetThree);
        EditText edtSet3Reps = view.findViewById(R.id.edtRepsSetThree);

        String set1Weight = edtSet1Weight.getText().toString();
        String set1Reps = edtSet1Reps.getText().toString();
        String set2Weight = edtSet2Weight.getText().toString();
        String set2Reps = edtSet2Reps.getText().toString();
        String set3Weight = edtSet3Weight.getText().toString();
        String set3Reps = edtSet3Reps.getText().toString();

        //TODO Finish implemneting the storing of exerciseSets, also figure out if theres a more effecient way to do this
        if(!TextUtils.isEmpty(set1Weight) || !TextUtils.isEmpty(set1Reps)){
            String uid = mUser.getUid();
            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
            String dateAndTime = sdf.format(date);
            Map<String, Object> exerciseSets = new HashMap<>();
            exerciseSets.put("uid", uid);
            exerciseSets.put("exerciseREF", exerciseDocREF);
            exerciseSets.put("name", exerciseName);
            exerciseSets.put("muscleGroup", muscleGroup);
            exerciseSets.put("dateTime", dateAndTime);
            exerciseSets.put("set1Weight", set1Weight);
            exerciseSets.put("set1Reps", set1Reps);

            if (!TextUtils.isEmpty(set2Weight) || !TextUtils.isEmpty(set2Reps)){
                exerciseSets.put("set2Weight", set2Weight);
                exerciseSets.put("set2Reps", set2Reps);
                if (!TextUtils.isEmpty(set3Weight) || !TextUtils.isEmpty(set3Reps)){
                    exerciseSets.put("set3Weight", set3Weight);
                    exerciseSets.put("set3Reps", set3Reps);
                }
                else{
                    dismiss();
                }
            }
            else{
                dismiss();
            }

            mDatabase.collection("exercise sets")
                    .add(exerciseSets)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(getActivity(), "Exercise Sets Added Successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Exercise Sets Failed to Save", Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            dismiss();
        }

    }
    private void setArrayAdapter(){
        //TODO maybe move arrayadapter code to its own method
    }
}
