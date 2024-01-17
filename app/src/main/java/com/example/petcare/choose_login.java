package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class choose_login extends AppCompatActivity {

    Button btnLogAsCust,btnLogAsCare,btnRegAsCustomer,btnRegAsCaregiver;

    FirebaseAuth fauth ;

    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        if (fauth.getCurrentUser() != null) {
            String userId = fauth.getCurrentUser().getUid();
            checkUserRole(userId);
        }


        setContentView(R.layout.activity_choose_login);

        btnLogAsCust=findViewById(R.id.btnLogAsCust);
        btnLogAsCare=findViewById(R.id.btnLogAsCare);
        btnRegAsCustomer=findViewById(R.id.btnRegAsCustomer);
        btnRegAsCaregiver=findViewById(R.id.btnRegAsCaregiver);

        btnLogAsCust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(choose_login.this, user_login.class);
                startActivity(loginIntent);

            }
        });
        btnRegAsCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(choose_login.this, customer_register.class);
                startActivity(loginIntent);
            }
        });

        btnLogAsCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(choose_login.this, user_login.class);
                startActivity(loginIntent);
            }
        });

        btnRegAsCaregiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(choose_login.this, caregiver_register.class);
                startActivity(loginIntent);
            }
        });

    }
    private void checkUserRole(String uid) {
        DocumentReference df = fstore.collection("Users").document(uid);

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
                    }
                }
            }
        });
    }
}