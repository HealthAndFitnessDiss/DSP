package com.example.healthandfitness;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WorkoutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkoutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkoutFragment newInstance(String param1, String param2) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    WorkoutRVAdapter workoutRVAdapter;
    //Workout workoutItem;
    RecyclerView recyclerWorkout;
    ArrayList<Workout> workoutItemList = new ArrayList<>();

    Button btnAddExercise, btnAddWorkout;
    String currentDate;
    DateTime dateTime = new DateTime();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FirebaseAuth mAuth;
    FirebaseFirestore mDatabase;
    FirebaseUser mUser;
    String uid;
    CollectionReference cWorkoutRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser!=null){
            uid = mUser.getUid();
        }
        cWorkoutRef = mDatabase.collection("workout");
        currentDate = dateTime.getDateTime("date");

        recyclerWorkout = view.findViewById(R.id.recyclerDailyExercises);

        readWorkouts();




        btnAddExercise = view.findViewById(R.id.btnAddExercise);
        btnAddWorkout = view.findViewById(R.id.btnAddWorkout);

        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                AddExerciseDialogFragment dialogFragment = new AddExerciseDialogFragment();
                dialogFragment.show(fragmentManager, "AddExerciseDialogFragment");
            }
        });

        btnAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                AddWorkoutDialogFragment dialogFragment = new AddWorkoutDialogFragment();
                dialogFragment.show(fragmentManager, "AddWorkoutDialogFragment");
            }
        });






    return view;}

    private void readWorkouts(){
        cWorkoutRef
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", currentDate)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("FireStore", "Snapshot Listener Failed", error);
                    return;
                }
                if(value!=null && !value.isEmpty()){
                    List<String> listMuscleGroup = new ArrayList<>();
                    List<String> listExerciseName = new ArrayList<>();
                    List<String> listExerciseRef = new ArrayList<>();
                    List<String> listNumberSets = new ArrayList<>();
                    List<String> listExerciseSetsRef = new ArrayList<>();
                    Map<String, Object> listSetsMapList = new HashMap<>();
                    String workoutRef;
                    for(DocumentSnapshot documentSnapshot: value){
                        listMuscleGroup = (List<String>) documentSnapshot.get("muscleGroupList");
                        listExerciseName = (List<String>) documentSnapshot.get("exerciseNameList");
                        listExerciseRef = (List<String>) documentSnapshot.get("exerciseRefList");
                        listNumberSets = (List<String>) documentSnapshot.get("numberSetsList");
                        listExerciseSetsRef = (List<String>) documentSnapshot.get("exerciseSetsRefList");
                        listSetsMapList = (Map<String, Object>) documentSnapshot.get("exerciseSetsMLL");
                        workoutRef = documentSnapshot.getId();
                        Workout workoutItem = new Workout(uid, currentDate, listMuscleGroup, listExerciseName, listExerciseRef, listExerciseSetsRef, listSetsMapList, listNumberSets);
                        workoutItemList.add(workoutItem);
                        updateRecyclerView(workoutItem);
                    }
                }
            }
        });
    }


    //TODO: NEED to add parent recycler view and change how the updateRecyclerView method works
    // while also implementing 


    private void updateRecyclerView(Workout workoutItem){
        workoutRVAdapter = new WorkoutRVAdapter(workoutItem);
        recyclerWorkout.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerWorkout.setAdapter(workoutRVAdapter);
    }





}