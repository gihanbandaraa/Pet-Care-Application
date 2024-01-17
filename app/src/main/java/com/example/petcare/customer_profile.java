package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class customer_profile extends AppCompatActivity {

    private RecyclerView rcvViewCustFeedPro;
    private FirebaseFirestore db;
    private List<FeedbackModel> feedbackList;
    private FeedbackAdapter adapter;

    private EditText edtCustProName, edtCustProEmail, edtCustProPhone, edtCustProAddress;
    private String originalName, originalEmail, originalPhone, originalAddress;
    private boolean changesMade = false;

    private ImageView imvCustProImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        rcvViewCustFeedPro = findViewById(R.id.rcvViewCustFeedPro);
        rcvViewCustFeedPro.setLayoutManager(new LinearLayoutManager(this));
        edtCustProName = findViewById(R.id.edtCustProName);
        edtCustProEmail = findViewById(R.id.edtCustProEmail);
        edtCustProPhone = findViewById(R.id.edtCustProPhone);
        edtCustProAddress = findViewById(R.id.edtCustProAddress);
        imvCustProImg = findViewById(R.id.imvCustProImg);

        db = FirebaseFirestore.getInstance();
        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(this, feedbackList);
        rcvViewCustFeedPro.setAdapter(adapter);

        fetchUserData();
        fetchFeedbackData();

        edtCustProEmail.setEnabled(false);
        edtCustProEmail.setFocusable(false);
        edtCustProEmail.setFocusableInTouchMode(false);

        originalName = edtCustProName.getText().toString();
        originalPhone = edtCustProPhone.getText().toString();
        originalAddress = edtCustProAddress.getText().toString();

        setUpEditTextListeners();

        Button btnSaveChanges = findViewById(R.id.btnSaveChnagePro);
        btnSaveChanges.setOnClickListener(view -> {
            if (changesMade) {
                updateUserData();
                changesMade = false;
                Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show();
            }

        });

    }
    private void fetchUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("CustomerName");
                String userEmail = documentSnapshot.getString("CustomerEmail");
                String userPhone = documentSnapshot.getString("CustomerPhone");
                String userAddress = documentSnapshot.getString("CustomerAddress");

                String ImageUrl =documentSnapshot.getString("image_url");

                // Set the fetched data to the respective TextInputEditText fields
                TextInputEditText edtCustProName = findViewById(R.id.edtCustProName);
                TextInputEditText edtCustProEmail = findViewById(R.id.edtCustProEmail);
                TextInputEditText edtCustProPhone = findViewById(R.id.edtCustProPhone);
                TextInputEditText edtCustProAddress = findViewById(R.id.edtCustProAddress);

                edtCustProName.setText(userName);
                edtCustProEmail.setText(userEmail);
                edtCustProPhone.setText(userPhone);
                edtCustProAddress.setText(userAddress);

                Picasso.Builder builder = new Picasso.Builder(this);
                builder.listener((picasso, uri, exception) -> {
                    // Handling image load exceptions, if any
                    exception.printStackTrace();
                });
                Picasso picasso = builder.build();
                picasso.load(ImageUrl)
                        .into(imvCustProImg);
            } else {
                // Handle if the document doesn't exist
                Log.d("Firestore", "User document does not exist");
            }
        }).addOnFailureListener(e -> {
            // Handle failures while fetching data
            Log.e("Firestore", "Error fetching user data: " + e.getMessage());
        });
    }
    private void fetchFeedbackData() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Feedback")
                .document(userId)
                .collection("userFeedbacks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String feedbackBy = document.getString("feedbackBy");
                            String feedbackContent = document.getString("feedback");

                            FeedbackModel feedback = new FeedbackModel(feedbackBy, feedbackContent);
                            feedbackList.add(feedback);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }
    private void setUpEditTextListeners() {
        edtCustProName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkForChanges();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtCustProPhone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkForChanges();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtCustProAddress.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                checkForChanges();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // Add similar listeners for other EditText fields
    }
    private void checkForChanges() {
        String newName = edtCustProName.getText().toString();
        String newPhone = edtCustProPhone.getText().toString();
        String newAddress = edtCustProAddress.getText().toString();

        // Check if any values are different from the original
        if (!newName.equals(originalName)
                || !newPhone.equals(originalPhone)
                || !newAddress.equals(originalAddress)) {
            changesMade = true;
        } else {
            changesMade = false;
        }
    }

    private void updateUserData() {
        // Get the updated values
        String updatedName = edtCustProName.getText().toString();
        String updatedPhone = edtCustProPhone.getText().toString();
        String updatedAddress = edtCustProAddress.getText().toString();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(userId);

        userRef.update("CustomerName", updatedName,
                        "CustomerPhone", updatedPhone,
                        "CustomerAddress", updatedAddress)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated the user data
                    Toast.makeText(this, "User data updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to update user data
                    Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error updating user data", e);
                });
    }

    abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
