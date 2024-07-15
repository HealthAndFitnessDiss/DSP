package com.example.healthandfitness;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CalorieFragment extends Fragment{
// implements AddFoodDialogFragment.OnDialogCloseListener

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    TextView txtDailyCalories;
    FirebaseFirestore mDatabase;
    CollectionReference cFoodRef, cDailyRef;
    ListenerRegistration listenerRegistration;


    Button btnAddFoodDialog, btnQuickAddCalorie, btnSearchFoodDialog;
    EditText edtQuickAddCalorie;

    TextView txtCurrentDate;



    String uid;


    ImageButton imgExpand;
    ConstraintLayout constraintFoodLog;


    RecyclerView recyclerView;


    String quickCalorieReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Below is a test for log out capabilities

        View view = inflater.inflate(R.layout.fragment_calorie, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser != null){
            uid = mUser.getUid();
        }
        mDatabase = FirebaseFirestore.getInstance();
        cFoodRef = mDatabase.collection("food");
        cDailyRef = mDatabase.collection("daily tracker");

        recyclerView = view.findViewById(R.id.recycleViewFood);






        setCurrentDate(view);
        setDailyCalories(view, uid);
        setDailyFoodLog(uid);
        String date = getCurrentDate();



        btnAddFoodDialog = view.findViewById(R.id.btnAddFoodDialog);
        btnAddFoodDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                AddFoodDialogFragment dialogFragment = new AddFoodDialogFragment();
                dialogFragment.show(fragmentManager, "AddFoodDialogFragment");

            }
        });

        btnSearchFoodDialog = view.findViewById(R.id.btnSearchFoodDialog);
        btnSearchFoodDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                SearchFoodDialogFragment dialogFragment = new SearchFoodDialogFragment();
                dialogFragment.show(fragmentManager, "SearchFoodDialogFragment");
            }
        });



        edtQuickAddCalorie = view.findViewById(R.id.edtQuickCalorie);
        btnQuickAddCalorie = view.findViewById(R.id.btnAddQuickCalorie);
        btnQuickAddCalorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sCalories = edtQuickAddCalorie.getText().toString();
                if (TextUtils.isEmpty(sCalories)){
                    edtQuickAddCalorie.setError("Caloric Value Required");
                }
                //TODO: Probs put all this in another separate function
                else{
                    String currentDate = getCurrentDate();

                    int iCalories = Integer.parseInt(sCalories);

                    long date = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                    String dateAndTime = sdf.format(date);
                    SimpleDateFormat sdfT = new SimpleDateFormat("HH:mm");
                    String sTime = sdfT.format(date);
                    Map<String, Object> quickCalorie = new HashMap<>();
                    quickCalorie.put("uid", uid);
                    quickCalorie.put("name", "Calories");
                    quickCalorie.put("dateTime", dateAndTime);
                    quickCalorie.put("calories", iCalories);
                    quickCalorie.put("measurementValue", "quickCalorie");

                    //TODO: Not bothered to retrieve the actual document reference for Quick Calories, may need to implement this in the future
                    DailyFoodItem dailyFoodItem = new DailyFoodItem("Calories", sCalories, sTime, "QuickCalorie");

                    cFoodRef
                            .add(quickCalorie)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.i("FireStore", "Successfully quick-added calories");
                                    quickCalorieReference = documentReference.getId();
                                    // If the quickCalorie FoodItem is successfully added, then it is set to be
                                    // appended onto the ArrayList dailyFoodItems -


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("FireStore", "Failure quick-adding calories");
                                }
                            });

                    cDailyRef
                            .whereEqualTo("uid", uid)
                            .whereEqualTo("date", currentDate)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        Log.d("FireStoreQuery", "Query Successful");
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
                                            foodRef.add(mapFood(dailyFoodItem));

                                            Map<String, Object> map = new HashMap<>();
                                            map.put("uid", uid);
                                            map.put("foodRef", foodRef);
                                            map.put("date", currentDate);
                                            map.put("calories", iCalories);

                                            cDailyRef
                                                    .add(map)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {

                                                            Log.i("FireStore", "Successfully created new Daily Tracker Document");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("FireStore", "Failed to create new Daily Tracker Document");
                                                        }
                                                    });
                                        }
                                        else{
                                            for(QueryDocumentSnapshot documentSnapshot: querySnapshot){
                                                DocumentReference documentReference = documentSnapshot.getReference();
                                                Log.d("FireStoreQuery", "Updating document ID: " + documentReference.getId());




                                                List<Map<String, Object>> foodRef = (List<Map<String, Object>>) documentSnapshot.get("foodRef");
                                                if (foodRef == null){
                                                    Log.e("FireStore Query", "Daily Tracker Document Exists (" + documentReference.getId() + ") - But Daily Food Item Array does not");
                                                    foodRef = new ArrayList<>();
                                                    Log.i("FireStore Query", "Creating New Empty Daily Food Item array");
                                                }
                                                foodRef.add(mapFood(dailyFoodItem));

                                                Map<String, Object> map = new HashMap<>();
                                                map.put("foodRef", foodRef);
                                                map.put("calories", FieldValue.increment(iCalories));

                                                documentReference
                                                        .update(map)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Log.i("FireStore", "Daily Tracker Document Updated: " + documentReference.getId());
                                                                edtQuickAddCalorie.setText("");
                                                                //TODO: add code to hide keyboard after the user has successfully added item
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.e("FireStore", "Failed to update Daily Tracker Document: " + documentReference.getId());
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                }
                            });
                }

            }
        });

        imgExpand = view.findViewById(R.id.imgExpand);
        constraintFoodLog = view.findViewById(R.id.constraintFoodLog);

        imgExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (constraintFoodLog.getVisibility()){
                    case View.GONE:
                        Log.i("Test", "Switch Case Accessed");
                        constraintFoodLog.setVisibility(View.VISIBLE);
                        break;
                    case View.VISIBLE:
                        constraintFoodLog.setVisibility(View.GONE);
                        break;
                    case View.INVISIBLE:
                        constraintFoodLog.setVisibility(View.VISIBLE);
                        break;
                }
                imgExpand.setRotation(imgExpand.getRotation() + 180);
            }
        });


        // Inflate the layout for this fragment
        return view;
    }



    // This method ensures proper cleanup of the Firestore listener when the fragments
    // view is destroyed. It checks as to whether the snapShot listener object is not null,
    // if so it removes the listener to stop Firestore from sending updates to the app once the
    // view is destroyed - prevents memory leaks, avoid unecessary data usage
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(listenerRegistration != null){
            listenerRegistration.remove();
        }
    }


    private String getCurrentDate(){
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String sDate = sdf.format(date);
    return sDate;}
    private void setCurrentDate(View view){
        txtCurrentDate = view.findViewById(R.id.txtCurrentDate);
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
        String dateString = sdf.format(date);
        txtCurrentDate.setText(dateString);
    }










    private void setDailyCalories(View view, String uid){

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        txtDailyCalories = view.findViewById(R.id.txtDailyCalories);


        String currentDate = getCurrentDate();



        cDailyRef
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", currentDate)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            Log.e("FireStore", "Snapshot Listener Failed", error);
                            return;
                        }
                        if (value != null && !value.isEmpty()){
                            for (DocumentSnapshot documentSnapshot: value){
                                if(documentSnapshot.exists()){
                                    //TODO: Possibly change this to an int, as rn it displays 750.0
                                    // kcal, the decimal is unnecessary
                                    Double calories = documentSnapshot.getDouble("calories");
                                    if (calories != null){
                                        updateDailyCalories(calories);
                                    }
                                }
                            }
                        } else{
                            Log.d("FireStore Query", "No Matching Documents Found");
                        }
                    }
                });



    }

    private void updateDailyCalories(Double calories){
        String sCalories;
        sCalories = calories.toString();
        sCalories = sCalories.concat(" kcal");
        txtDailyCalories.setText(sCalories);
    }

    //This type of function is repeated probs move to seperate class
    public Map<String, Object> mapFood(DailyFoodItem dailyFoodItem){
        Map<String, Object> map = new HashMap<>();
        map.put("name", dailyFoodItem.getName());
        map.put("calories", dailyFoodItem.getCalories());
        map.put("time", dailyFoodItem.getTime());
        map.put("foodDocRef", dailyFoodItem.getFoodDocRef());
        return map;}


    private void setDailyFoodLog(String uid){
        String currentDate = getCurrentDate();

        cDailyRef
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", currentDate)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("FireStore", "Snapshot Listener Failed", error);
                    return;
                }
                //TODO: SET ERROR CATCHES ONFAILURE
                if (value != null && !value.isEmpty()){
                    for (DocumentSnapshot documentSnapshot: value){
                        if(documentSnapshot.exists()){
                            List<Map<String, Object>> dailyFoodDocList = (List<Map<String, Object>>) documentSnapshot.get("foodRef");
                            //Map<String, Object> dailyFoodMap = (Map<String, Object>) documentSnapshot.get("foodRef");
                            if (dailyFoodDocList != null){
                                ArrayList<DailyFoodItem> dailyFoodItemArrayList;
                                dailyFoodItemArrayList = unMapFood(dailyFoodDocList);
                                updateRecyclerView(dailyFoodItemArrayList);
                            }
                        }
                    }
                }
            }
        });

    }

    private ArrayList<DailyFoodItem> unMapFood(List<Map<String, Object>> dailyFoodMapList){
        ArrayList<DailyFoodItem> dailyFoodList = new ArrayList<>();
        Map<String, Object> foodMapHolder;
        String name;
        String calories;
        String time;
        String foodDocRef;
        Log.i("TEST", "dailyFoodArray Retrieved");
        for (int i = 0; i < dailyFoodMapList.size(); i++){
            foodMapHolder = dailyFoodMapList.get(i);
            name = foodMapHolder.get("name").toString();
            calories = foodMapHolder.get("calories").toString();
            time = foodMapHolder.get("time").toString();
            foodDocRef = foodMapHolder.get("foodDocRef").toString();
            DailyFoodItem dailyFoodItem = new DailyFoodItem(name, calories, time, foodDocRef);
            dailyFoodList.add(dailyFoodItem);
        }
    return dailyFoodList;}

    private void updateRecyclerView(ArrayList<DailyFoodItem> dailyFoodItemArrayList){
        DailyFoodRVAdapter adapter = new DailyFoodRVAdapter(getActivity(), dailyFoodItemArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //Todo: Figure out how to set the height of recycler view to limit after 3 items
//        if (dailyFoodItemArrayList.size() == 3){
//            ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
//            int height = recyclerView.getHeight();
//            params.width = recyclerView.getWidth();
//            params.height = height;
//            Log.d("Layout", "Set Height of Recycler View: " + height);
//            recyclerView.setLayoutParams(params);
//        }
    }


}