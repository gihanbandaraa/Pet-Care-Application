package com.example.petcare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class caregiver_home extends AppCompatActivity {

    TextView txvWelcomeCare,txvActiveJob,txvViewJobs;
    ImageView imvCareProfile;

    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_home);

        txvWelcomeCare =findViewById(R.id.txvWelcomeCare);
        txvActiveJob =findViewById(R.id.txvActiveJob);

        txvViewJobs =findViewById(R.id.txvViewJobs);
        imvCareProfile =findViewById(R.id.imvCareProfile);

        fauth =FirebaseAuth.getInstance();
        fstore =FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = fstore.collection("Users").document(currentUserId);

        imvCareProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), customer_profile.class));
            }
        });
        txvWelcomeCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), customer_profile.class));
            }
        });


        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("CustomerName");
                String imageUrl = documentSnapshot.getString("image_url");

                txvWelcomeCare.setText("Hi, " + userName);

                Picasso.Builder builder = new Picasso.Builder(this);
                builder.listener((picasso, uri, exception) -> {
                    exception.printStackTrace();
                });
                Picasso picasso = builder.build();

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    try {
                        Uri uri = Uri.parse(imageUrl);
                        picasso.load(uri).into(imvCareProfile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to retrieve user information.", Toast.LENGTH_SHORT).show();
        });
        txvViewJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), find_job_care.class));
            }
        });
        txvActiveJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), view_job_process.class));
            }
        });


    }
    public void logout(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Logout");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Logout", (dialog, which) -> {
            SharedPreferences preferences = getSharedPreferences("JobDetails", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), user_login.class));
            finish();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Do nothing or handle cancellation
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}