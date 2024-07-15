package com.example.healthandfitness;

import static java.lang.Math.round;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    Button btnLogOut;

    Button btnCalculate, btnSave;

    EditText edtWeight, edtHeight;
    TextView txtBMI;

    FirebaseAuth mAuth;
    FirebaseFirestore mDatabase;
    FirebaseUser mUser;
    String uid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseFirestore.getInstance();
        uid = mUser.getUid();


        btnLogOut = view.findViewById(R.id.btnLogOut);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                moveToLogInActivity();
            }
        });

        txtBMI = view.findViewById(R.id.txtBMI);
        edtWeight = view.findViewById(R.id.edtWeightBMI);
        edtHeight = view.findViewById(R.id.edtHeightBMI);
        btnSave = view.findViewById(R.id.btnSaveBMI);
        btnCalculate = view.findViewById(R.id.btnCalculate);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String weight = edtWeight.getText().toString();
                String height = edtHeight.getText().toString();

                if(TextUtils.isEmpty(weight)){
                    edtWeight.setError("Weight Required");

                }
                if(TextUtils.isEmpty(height)){
                    edtHeight.setError("Height Required");
                }
                else{
                    Log.d("FireStoreQuery", "Querying for UID: " + uid);
                    mDatabase.collection("user")
                            .whereEqualTo("uid", uid)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        Log.d("FireStoreQuery", "Query successful");
                                        QuerySnapshot querySnapshot = task.getResult();

                                        if(querySnapshot.isEmpty()){
                                            Log.d("FireStoreQuery", "No Matching Documents");
                                            Toast.makeText(getActivity(), "No matching User Profile", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        for(QueryDocumentSnapshot documentSnapshot: querySnapshot){

                                            DocumentReference documentReference = documentSnapshot.getReference();
                                            Map<String, Object> bmi = new HashMap<>();
                                            bmi.put("Weight1", weight);
                                            bmi.put("Height1", height);


                                            Log.d("FireStoreQuery", "Updating document ID: " + documentReference.getId());
                                            documentReference
                                                    .update(bmi)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        Log.d("FireStoreQuery", "Error getting Documents", task.getException());
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show();
                                    Log.d("FireStoreQuery", "Query Failed", e);
                                }
                            });


                }
            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.collection("user")
                        .whereEqualTo("uid", uid)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null){
                                    Log.e("FireStore Error", error.getMessage());
                                }
                                if (value != null && !value.isEmpty()){
                                    double bmi = 0;
                                    double weight = 0;
                                    double height = 0;
                                    for(QueryDocumentSnapshot documentSnapshot : value){
                                        String weightString = documentSnapshot.getString("Weight1");
                                        String heightString = documentSnapshot.getString("Height1");
                                        if (weightString != null && heightString != null){
                                            weight = Double.parseDouble(weightString);
                                            height = Double.parseDouble(heightString);
                                            bmi = (weight / (height * height));

                                        }
                                    }
                                    String bmiString = Double.toString(bmi);
                                    txtBMI.setText(bmiString);
                                }
                            }
                        });
            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    private void calculateBMI(){


    }
    private void moveToLogInActivity(){
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        startActivity(intent);
        ((Activity) getActivity()).overridePendingTransition(0, 0);

    }
}