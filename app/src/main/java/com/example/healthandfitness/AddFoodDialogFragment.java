package com.example.healthandfitness;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFoodDialogFragment extends DialogFragment {

    EditText edtName, edtBrandName, edtCalories, edtProtein, edtFats, edtSaturates, edtCarbohydrate, edtSugars,
            edtFibre, edtSalt, edtMeasurementValue, edtNotes, edtNumServings;
    Button btnSave, btnSaveAdd;
    AutoCompleteTextView drpMeasurementUnit;
    ArrayAdapter arrayAdapter;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore mDatabase;

    String uid;
    CollectionReference cFoodRef, cDailyRef, cMapFoodRef;
    //DocumentReference dFoodRef;




    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_food, null);
        builder.setView(view);

        // Code necessary to remove unwanted shading behind corner radius
        // of the dialog card
        Dialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null){
            uid = user.getUid();
        }
        cDailyRef = mDatabase.collection("daily tracker");
        cFoodRef = mDatabase.collection("food");
        cMapFoodRef = mDatabase.collection("map food");

        // Setting the AutoCompleteTextView array adapter, so the dropdown menu can provide the options
        drpMeasurementUnit = view.findViewById(R.id.drpFoodMeasurementUnit);
        String[] arrayMeasurementUnit = ArrayHandler.getArrayFoodMeasurementUnit();
        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_layout, arrayMeasurementUnit);
        drpMeasurementUnit.setAdapter((arrayAdapter));
        drpMeasurementUnit.setText(arrayMeasurementUnit[0], false);

        edtName = view.findViewById(R.id.edtFoodName);
        edtBrandName = view.findViewById(R.id.edtFoodBrandName);
        edtCalories = view.findViewById(R.id.edtFoodCalories);
        edtProtein = view.findViewById(R.id.edtFoodProtein);
        edtFats = view.findViewById(R.id.edtFoodFats);
        edtSaturates = view.findViewById(R.id.edtFoodSaturates);
        edtCarbohydrate = view.findViewById(R.id.edtFoodCarbohydrate);
        edtSugars = view.findViewById(R.id.edtFoodSugars);
        edtFibre = view.findViewById(R.id.edtFoodFibre);
        edtSalt = view.findViewById(R.id.edtFoodSalt);
        edtMeasurementValue = view.findViewById(R.id.edtFoodMeasurementValue);
        edtNumServings = view.findViewById(R.id.edtNumServings);

        //edtNotes = view.findViewById(R.id.edtFoodNotes);
        btnSave = view.findViewById(R.id.btnFoodSave);
        btnSaveAdd = view.findViewById(R.id.btnFoodSaveAdd);



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                saveFood(view, i);
            }
        });
        btnSaveAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 1;
                saveFood(view, i);

            }
        });





        return dialog;
    }


    DailyFoodArrayHandler dailyFoodArrayHandler = new DailyFoodArrayHandler();

    // Method to save a food item to the database, but for the user not to log it as an eaten food
    public void saveFood(View view, int i){

        String name = edtName.getText().toString();
        String calories = edtCalories.getText().toString();
        String measurementValue = edtMeasurementValue.getText().toString();
        String measurementUnit = drpMeasurementUnit.getText().toString();

        String brandName = edtBrandName.getText().toString();
        String protein = edtProtein.getText().toString();
        String fats = edtFats.getText().toString();
        String saturates = edtSaturates.getText().toString();
        String carbohydrate = edtCarbohydrate.getText().toString();
        String sugar = edtSugars.getText().toString();
        String fibre = edtFibre.getText().toString();
        String salt = edtSalt.getText().toString();

        //TODO: Need to add more error catchers - i.e. if user creates a food item with the same name as one before

        if (TextUtils.isEmpty(name)){
            edtName.setError("Food Name Required");
        }
        if (TextUtils.isEmpty(calories)){
            edtCalories.setError("Food Calories Required");
        }
        if (TextUtils.isEmpty(measurementValue)){
            edtMeasurementValue.setError("Value Required");
        }

        else{
            //TODO: Maybe create a class function to retrieve the time, to clean up code
            long date = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
            String dateAndTime = sdf.format(date);
            Map<String, Object> food = new HashMap<>();
            food.put("uid", uid);
            food.put("name", name);
            food.put("calories", calories);
            food.put("measurementValue", measurementValue);
            food.put("measurementUnit", measurementUnit);
            food.put("dateTime", dateAndTime);
            //TODO: May be better to put in a switch case and not store the following values if they're empty
            food.put("brandName", brandName);
            food.put("protein", protein);
            food.put("fats", fats);
            food.put("saturates", saturates);
            food.put("carbohydrate", carbohydrate);
            food.put("sugar", sugar);
            food.put("fibre", fibre);
            food.put("salt", salt);

            cFoodRef
                    .add(food)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getActivity(), "Food added successfully", Toast.LENGTH_SHORT).show();
                            Log.i("FireStore", "Successfully created new Food Document: " + documentReference.getId());
                            //Probably not the best way to achieve this but it works
                            //Re uses the code but calls another method when needed
                            if (i == 1){

                                saveAddFood(view, documentReference, name, calories, protein, measurementValue, measurementUnit);
                            }
                            else{
                                // TODO: Need to save the food to the mapFood collection
                            }
                            dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Error: Food item failed to save", Toast.LENGTH_SHORT).show();
                            Log.e("FireStore", "Failure to create new Food Document");
                        }
                    });

        }
    }
    public void saveAddFood(View view, DocumentReference dFoodRef, String sName, String sServingCalories, String sProtein, String sMeasurementValue, String sMeasurementUnit){
        String numServings = edtNumServings.getText().toString();
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat sdfT = new SimpleDateFormat("HH:mm");
        String sDate = sdf.format(date);
        String sTime = sdfT.format(date);

        double calories = (Double.parseDouble(edtNumServings.getText().toString())) * (Double.parseDouble(edtCalories.getText().toString()));
        String sFoodRef = dFoodRef.getId();
        String sCalories = Double.toString(calories);

        DailyFoodItem dailyFoodItem = new DailyFoodItem(sName, sCalories, sTime, sFoodRef);
        Food foodItem = new Food(sName, sDate, sServingCalories, sMeasurementValue, sMeasurementUnit, sFoodRef);

        cMapFoodRef
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Log.d("FireStoreQuery", "Map Food Collection Query Successful");
                    QuerySnapshot querySnapshot = task.getResult();
                    // Debugging to check if any documents retrieved by the query
                    if (querySnapshot != null) {
                        Log.d("FireStoreQuery", "Number of documents found: " + querySnapshot.size());
                    } else {
                        Log.d("FireStoreQuery", "QuerySnapshot is null");
                    }

                    if(querySnapshot == null || querySnapshot.isEmpty()){
                        Log.d("FireStoreQuery", "Snapshot Empty : Food Map");
                        List<Map<String, Object>> foodMap = new ArrayList<>();
                        foodMap.add(mapFood(foodItem));
                        Map<String, Object> mapHolder = new HashMap<>();
                        mapHolder.put("uid", uid);
                        mapHolder.put("foodMap", foodMap);
                        cMapFoodRef
                                .add(mapHolder)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i("FireStore", "Successfully created new Food Map Document");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("FireStore", "Failed to create new Food Map Document");
                            }
                        });
                    }else{
                        for(QueryDocumentSnapshot documentSnapshot: querySnapshot){
                            DocumentReference documentReference = documentSnapshot.getReference();
                            List<Map<String, Object>> foodMap = (List<Map<String, Object>>) documentSnapshot.get("foodMap");
                            if (foodMap == null){
                                Log.e("FireStore Query", "Daily Tracker Document Exists (" + documentReference.getId() + ") - But Daily Food Item Array does not");
                                foodMap = new ArrayList<>();
                                Log.i("FireStore Query", "Creating New Empty Daily Food Item array");
                            }
                            foodMap.add(mapFood(foodItem));
                            Map<String, Object> map = new HashMap<>();
                            map.put("foodMap", foodMap);

                            Log.d("FireStoreQuery", "Updating document ID: " + documentReference.getId());
                            documentReference
                                    .update(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.i("FireStoreQuery", "Successfully updated document ID : " + documentReference.getId());
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("FireStoreQuery", "Unsuccessful in updating document ID : " + documentReference.getId());
                                        }
                                    });

                        }
                    }

                }else{
                    Log.e("FireStoreQuery", "Error Querying Food Map Document");
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FireStoreQuery", "Query Failed", e);
            }
        });

        cDailyRef
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", sDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Log.d("FireStoreQuery", "Query successful");
                            QuerySnapshot querySnapshot = task.getResult();

                            // Debugging to check if any documents retrieved by the query
                            if (querySnapshot != null) {
                                Log.d("FireStoreQuery", "Number of documents found: " + querySnapshot.size());
                            } else {
                                Log.d("FireStoreQuery", "QuerySnapshot is null");
                            }

                            if(querySnapshot == null || querySnapshot.isEmpty()){
                                Log.d("FireStoreQuery", "Snapshot Empty : Daily Tracker");


                                List<Map<String, Object>> foodRef = new ArrayList<>();
                                foodRef.add(mapFoodDaily(dailyFoodItem));

                                Map<String, Object> daily = new HashMap<>();
                                daily.put("uid", uid);
                                daily.put("foodRef", foodRef);
                                daily.put("date", sDate);
                                daily.put("calories", calories);

                                cDailyRef
                                        .add(daily)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.i("FireStore", "Successfully created new Daily Tracker Document");
                                        //First I empty the contents of the array as if a new daily tracker is
                                        //Being created the arrayDailyFood should not hold any data
                                        dailyFoodArrayHandler.emptyArrayDailyFood();
                                        dailyFoodArrayHandler.setArrayDailyFood(dailyFoodItem);
                                        //setDailyFoodAdapter(dailyFoodArrayHandler);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("FireStore", "Failed to create new Daily Tracker Document");
                                    }
                                });
                            }else{
                                for(QueryDocumentSnapshot documentSnapshot: querySnapshot){
                                    DocumentReference documentReference = documentSnapshot.getReference();

                                    if (documentSnapshot.contains("foodRef")) {
                                        Log.d("FireStore Query", "Document contains foodRef field");
                                    } else {
                                        Log.e("FireStore Query", "Document does not contain foodRef field");
                                    }


                                    List<Map<String, Object>> foodRef = (List<Map<String, Object>>) documentSnapshot.get("foodRef");
                                    if (foodRef == null){
                                        Log.e("FireStore Query", "Daily Tracker Document Exists (" + documentReference.getId() + ") - But Daily Food Item Array does not");
                                        foodRef = new ArrayList<>();
                                        Log.i("FireStore Query", "Creating New Empty Daily Food Item array");
                                    }
                                    foodRef.add(mapFoodDaily(dailyFoodItem));

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("foodRef", foodRef);
                                    map.put("calories", FieldValue.increment(calories));
                                    Log.d("DeBug", "Calories Variable Value ---->  " + calories);


                                    Log.d("FireStoreQuery", "Updating document ID: " + documentReference.getId());
                                    documentReference
                                            .update(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.i("FireStore", "Daily Tracker Document Updated: " + documentReference.getId());
                                                    showToast("Food Item Saved");
                                                    dailyFoodArrayHandler.setArrayDailyFood(dailyFoodItem);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("FireStore", "Failed to update Daily Tracker Document: " + documentReference.getId());
                                                    showToast("Food Item failed to Save");
                                                }
                                            });

                                }
                            }
                        } else {
                            Log.e("FireStoreQuery", "Error Querying Documents");
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Failed");
                        Log.e("FireStoreQuery", "Query Failed", e);
                    }
                });


    }

    public Map<String, Object> mapFoodDaily(DailyFoodItem dailyFoodItem){
        Map<String, Object> map = new HashMap<>();
        map.put("name", dailyFoodItem.getName());
        map.put("calories", dailyFoodItem.getCalories());
        map.put("time", dailyFoodItem.getTime());
        map.put("foodDocRef", dailyFoodItem.getFoodDocRef());
    return map;}

    public Map<String, Object> mapFood(Food foodItem){
        Map<String, Object> map = new HashMap<>();
        map.put("name", foodItem.getName());
        map.put("date", foodItem.getDate());
        map.put("calories", foodItem.getCalories());
        map.put("measurementValue", foodItem.getMeasurementValue());
        map.put("measurementUnit", foodItem.getMeasurementUnit());
        map.put("foodRef", foodItem.getFoodRef());
    return map;}

    // Null Pointer Exception catch for displaying Toasts
    private void showToast(String message) {
        if (isAdded()) { // Ensure the fragment is attached to the activity
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else {
            Log.w("FragmentLifecycle", "Fragment is not attached to an activity. Cannot show Toast.");
        }
    }

    public interface OnDialogCloseListener{
        void onDialogClose();
    }
    private OnDialogCloseListener listener;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            listener = (OnDialogCloseListener) getTargetFragment();
        } catch (ClassCastException e){
            throw new ClassCastException("Calling fragment must implement OnDialogCloseListener");
        }
    }
    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        if (listener != null){
            listener.onDialogClose();;
        }
    }



}
