package com.example.healthandfitness;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterDetailsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore mDatabase;
    FirebaseUser user;
    Button btnContinue;
    EditText edtFirstName, edtSurname, edtAge, edtWeight;

    String[] arrayWeightUnit;
    ArrayAdapter<String> arrayAdapter;

    AutoCompleteTextView weightUnitPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_details);

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        btnContinue = findViewById(R.id.btnContinue);
        edtFirstName = findViewById(R.id.edtName);
        edtAge = findViewById(R.id.edtAge);
        edtWeight = findViewById(R.id.edtWeight);

        //TODO: Should probably look into moving the arrayadapters for my AutoCompleteTextView dropdowns into
        //TODO: their own class, will improve code readability and efficiency
        //TODO: As this dropdown options of kg/lbs may need to be called several times throughtout the app in seperate activities
        weightUnitPreference = findViewById(R.id.drpWeighMeasurementPreference);
        arrayWeightUnit = new String[] {getString(R.string.kg), getString(R.string.lbs)};
        arrayAdapter = new ArrayAdapter<>(RegisterDetailsActivity.this, R.layout.dropdown_layout, arrayWeightUnit);
        weightUnitPreference.setAdapter(arrayAdapter);
        weightUnitPreference.setText(getString(R.string.kg), false);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = user.getUid();
                String firstname = edtFirstName.getText().toString();
                String age = edtAge.getText().toString();
                String weight = edtWeight.getText().toString();
                String weightUnit = weightUnitPreference.getText().toString();

                if (TextUtils.isEmpty(firstname)){
                    edtFirstName.setError("Name Required");
                }
                if (TextUtils.isEmpty(age)){
                    edtAge.setError("Age Required");
                }
                if (TextUtils.isEmpty(weight)){
                    edtWeight.setError("Weight Required");
                }

                else{
                    Map<String, Object> details = new HashMap<>();
                    details.put("uid", uid);
                    details.put("firstname", firstname);
                    details.put("weight", weight);
                    details.put("weightUnit", weightUnit);

                    mDatabase.collection("user")
                            .add(details)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(RegisterDetailsActivity.this, "User details added successfully", Toast.LENGTH_SHORT).show();
                                    moveToMainActivity();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterDetailsActivity.this, "Error: User details failed to save", Toast.LENGTH_SHORT).show();
                                }
                            });
                }




            }
        });
    }

    private void moveToMainActivity(){
        Intent intent = new Intent(RegisterDetailsActivity.this, MainActivity.class);
        startActivity(intent);
        ((Activity) RegisterDetailsActivity.this).overridePendingTransition(0, 0);

    }




}
