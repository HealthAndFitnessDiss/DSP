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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFoodDialogFragment extends DialogFragment implements  RecyclerViewInterface{

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mDatabase;
    CollectionReference cFoodMapRef, cDailyRef;
    String uid;
    SearchView searchFood;
    RecyclerView recyclerView;
    ArrayList<Food> foodItemList;
    FoodRVAdapter foodRVAdapter;

    TextView txtSearchFoodName, txtSearchFoodCalories, txtSearchFoodServing;
    EditText edtSearchServings;
    ConstraintLayout constraintSearchFood, constraintSaveFood;

    ImageButton btnBackToSearch;
    Button btnFoodSaveAdd;

    DateTime dateHandler = new DateTime();

    int pos;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_search_food, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser!=null){
            uid = mUser.getUid();
        }
        cFoodMapRef = mDatabase.collection("map food");
        cDailyRef = mDatabase.collection("daily tracker");

        readFoodItems();

        searchFood = view.findViewById(R.id.searchFood);
        recyclerView = view.findViewById(R.id.recycleViewSearchFood);


        searchFood.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterFoodList(newText);
                return true;
            }
        });

        txtSearchFoodName = view.findViewById(R.id.txtSearchFoodName);
        txtSearchFoodCalories = view.findViewById(R.id.txtSearchFoodCalories);
        txtSearchFoodServing = view.findViewById(R.id.txtSearchFoodServing);
        edtSearchServings = view.findViewById(R.id.edtSearchFoodServings);
        constraintSearchFood = view.findViewById(R.id.constraintSearchFood);
        constraintSaveFood = view.findViewById(R.id.constraintSaveFood);
        btnBackToSearch = view.findViewById(R.id.btnBackToSearch);

        btnBackToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Moved the clearing of the SearchView until the user wants to go back to the search screen
                // To avoid issues in data transfer
                searchFood.setQuery("", false);
                searchFood.clearFocus();
                constraintSaveFood.setVisibility(View.GONE);
                constraintSearchFood.setVisibility(View.VISIBLE);
            }
        });

        btnFoodSaveAdd = view.findViewById(R.id.btnFoodSaveAdd);
        btnFoodSaveAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edtSearchServings.getText().toString())){
                    edtSearchServings.setError("Number of Servings Necessary");
                }
                else{
                    saveFood();
                }

            }
        });


        return dialog;
    }

    private void readFoodItems(){

        cFoodMapRef
                .whereEqualTo("uid", uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.e("FireStore", "Snapshot Listener Failed", error);
                            return;
                        }
                        if (value != null && !value.isEmpty()){
                            for(DocumentSnapshot documentSnapshot: value){
                                List<Map<String, Object>> foodMapList = (List<Map<String, Object>>) documentSnapshot.get("foodMap");
                                if(foodMapList != null){


                                    foodItemList = unMapFood(foodMapList);
                                    updateRecyclerView(foodItemList);
                                }
                                else{
                                    Log.e("FireStoreQuery", "Map Food document contains no food map field");
                                }

                            }
                        }
                    }
                });
    }

    private ArrayList<Food> unMapFood(List<Map<String, Object>> foodMapList){
        ArrayList<Food> foodList = new ArrayList<>();
        Map<String, Object> foodMapHolder;
        String name;
        String date;
        String calories;
        String protein;
        String measurementValue;
        String measurementUnit;
        String foodDocRef;
        Log.d("TEST", "foodMap array Retrieved");
        for (int i = 0; i < foodMapList.size(); i++){
            foodMapHolder = foodMapList.get(i);
            name = foodMapHolder.get("name").toString();
            date = foodMapHolder.get("date").toString();
            calories = foodMapHolder.get("calories").toString();
            //protein = foodMapHolder.get("protein").toString();
            measurementValue = foodMapHolder.get("measurementValue").toString();
            measurementUnit = foodMapHolder.get("measurementUnit").toString();
            foodDocRef = foodMapHolder.get("foodRef").toString();
            Food foodItem = new Food(name, date, calories, measurementValue, measurementUnit, foodDocRef);
            foodList.add(foodItem);
        }
        return foodList;}

    public void filterFoodList(String filter){
        ArrayList<Food> filteredFoodList = new ArrayList<>();
        for(Food foodItem : foodItemList){
            if (foodItem.getName().toLowerCase().contains(filter.toLowerCase())){
                filteredFoodList.add(foodItem);

            }
        }
        Log.d("SearchView", "Filtered list size: " + filteredFoodList.size());
        if (filteredFoodList.isEmpty()){
            Toast.makeText(getActivity(), "No Created Food Items to Search Through", Toast.LENGTH_SHORT).show();
            Log.e("SearchView", "Can't filter search for food items, no list / items");
        }else{
            foodRVAdapter.setFilteredList(filteredFoodList);
            Log.d("RecyclerView", "Filtered List set to RV Adapter");
            //updateRecyclerView(filteredFoodList);
        }
    }

    private Map<String, Object> mapFood(Double calories){
        Map<String, Object> map = new HashMap<>();
        map.put("name", foodItemList.get(pos).getName());
        map.put("calories", calories);
        map.put("time", dateHandler.getDateTime("time"));
        map.put("foodDocRef", foodItemList.get(pos).getFoodRef());
    return map;}



    private void updateRecyclerView(ArrayList<Food> foodItemList){
        // "this" is used to pass through the context of the recyclerViewInterface to the adapter's constructor
        if(foodRVAdapter == null){
            foodRVAdapter = new FoodRVAdapter(getActivity(), foodItemList, this);
            recyclerView.setAdapter(foodRVAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }else{
            foodRVAdapter.resetList();
        }


    }


    private void saveFood(){
        String sDate = dateHandler.getDateTime("date");
        double calories = (Double.parseDouble(edtSearchServings.getText().toString())) * (Double.parseDouble(txtSearchFoodCalories.getText().toString()));
        cDailyRef.whereEqualTo("uid", uid)
                .whereEqualTo("date", sDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i("FireStoreQuery", "Daily Tracker query Successful");
                            QuerySnapshot querySnapshot = task.getResult();
                            // Debugging to check if any documents retrieved by the query
                            if (querySnapshot != null) {
                                Log.d("FireStoreQuery", "Number of Daily Tracker documents found: " + querySnapshot.size());
                            } else {
                                Log.d("FireStoreQuery", "Daily Tracker QuerySnapshot is null");
                            }
                            if (querySnapshot == null || querySnapshot.isEmpty()) {
                                Log.d("FireStoreQuery", "Snapshot Empty : Daily Tracker");
                                List<Map<String, Object>> foodRef = new ArrayList<>();
                                foodRef.add(mapFood(calories));

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

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("FireStore", "Failed to create new Daily Tracker Document");
                                            }
                                        });
                            } else {
                                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                    DocumentReference documentReference = documentSnapshot.getReference();

                                    if (documentSnapshot.contains("foodRef")) {
                                        Log.d("FireStore Query", "Document contains foodRef field");
                                    } else {
                                        Log.e("FireStore Query", "Document does not contain foodRef field");
                                    }

                                    //TODO: Fix bug where if you add a new food item, straight after adding the first food item of the day
                                    // the app creates a separate new foodRef arrayMap, instead of adding to the original
                                    // probably need to implement transactions to manage this and improve the querying of this as its quite overloaded atm

                                    List<Map<String, Object>> foodRef = (List<Map<String, Object>>) documentSnapshot.get("foodRef");
                                    if (foodRef == null) {
                                        Log.e("FireStore Query", "Daily Tracker Document Exists (" + documentReference.getId() + ") - But Daily Food Item Array does not");
                                        foodRef = new ArrayList<>();
                                        Log.i("FireStore Query", "Creating New Empty Daily Food Item array");
                                    }
                                    foodRef.add(mapFood(calories));

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

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("FireStore", "Failed to update Daily Tracker Document: " + documentReference.getId());

                                                }
                                            });
                                }
                            }
                        } else {
                            Log.e("FireStoreQuery", "Error Querying Daily Tracker Documents");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FireStoreQuery", "Query Failed", e);
                    }
                });
        dismiss();
    }


    @Override
    public void onItemClicked(int position) {
        //Save the position to a public variable so that it can be accessed in the saveFood method
        pos = position;

        Log.d("Interface", "Position: " + position);

        Food clickedFoodItem = foodRVAdapter.getFoodItemAt(position);
        //Food clickedFoodItem = foodRVAdapter.foodItemArrayList.get(position);

        if (clickedFoodItem != null){

            //TODO: CHANGE THE BELOW CODE - So that when an item is clicked I use its .getFoodDocREF
            // to get the original Food Document from the Food collection, from here I can retrieve
            // all its macro details and present them

            String name = clickedFoodItem.getName();
            String calories = clickedFoodItem.getCalories();
            String servingSize = clickedFoodItem.getMeasurementValue().concat(" ").concat(clickedFoodItem.getMeasurementUnit());
            txtSearchFoodName.setText(name);
            txtSearchFoodCalories.setText(calories);
            txtSearchFoodServing.setText(servingSize);

            constraintSearchFood.setVisibility(View.GONE);
            constraintSaveFood.setVisibility(View.VISIBLE);
        }else{
            Log.e("RecyclerView", "Recycler View returning null on click");
        }


    }


}


