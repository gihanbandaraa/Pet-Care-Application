package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class splash_screen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        mAuth = FirebaseAuth.getInstance();
        fstore =FirebaseFirestore.getInstance();
        redirectToAppropriateScreen();
    }

    private void redirectToAppropriateScreen() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            checkUserRole(userId);
        } else {
            // If the user is not authenticated, redirect to the login/choose_login activity
            Intent firstSplashIntent = new Intent(this, firstSplashScreen.class);
            startActivity(firstSplashIntent);
            finish();
        }
    }

    private void checkUserRole(String userId) {
        DocumentReference df = fstore.collection("Users").document(userId);

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.getString("isCustomer") != null) {
                        startActivity(new Intent(getApplicationContext(), customer_home.class));
                        finish();
                    } else if (documentSnapshot.getString("isCaregiver") != null) {
                        startActivity(new Intent(getApplicationContext(), caregiver_home.class));
                        finish();
                    } else {

                        startActivity(new Intent(getApplicationContext(), choose_login.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), choose_login.class));
                finish();
                }
            }
        });
    }

}