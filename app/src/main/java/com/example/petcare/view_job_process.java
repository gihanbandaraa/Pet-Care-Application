package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class view_job_process extends AppCompatActivity {

TextView    txvPetnameAJ, txvLocationAJ, txvPetTypeAJ, txvPetAgeAJ, txvSexAJ,
            txvDurationAJ, txvPetInstructionsAJ, txvPriceAJ, txvCustName, txvCustPhone, txvCustEmail;
ImageView imvPetAJ;
Button btnCompleteOrder;
LinearLayout laorderActive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job_process);

        SharedPreferences preferences = getSharedPreferences("JobDetails", Context.MODE_PRIVATE);
        String userId = preferences.getString("USER_ID", "");
        if (userId.isEmpty()) {

            Toast.makeText(this, "No active job available", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            txvPetnameAJ = findViewById(R.id.txvPetnameAJ);
            txvLocationAJ = findViewById(R.id.txvLocationAJ);
            txvPetTypeAJ = findViewById(R.id.txvPetTypeAJ);
            txvPetAgeAJ = findViewById(R.id.txvPetAgeAJ);
            txvSexAJ = findViewById(R.id.txvSexAJ);
            txvDurationAJ = findViewById(R.id.txvDurationAJ);
            txvPetInstructionsAJ = findViewById(R.id.txvPetInstructionsAJ);
            txvPriceAJ = findViewById(R.id.txvPriceAJ);
            txvCustName = findViewById(R.id.txvCustName);
            txvCustPhone = findViewById(R.id.txvCustPhone);
            txvCustEmail = findViewById(R.id.txvCustEmail);
            imvPetAJ = findViewById(R.id.imvPetAJ);
            btnCompleteOrder = findViewById(R.id.btnCompleteOrder);
            laorderActive = findViewById(R.id.laorderActive);


            String petType = preferences.getString("PET_TYPE", "");
            String petName = preferences.getString("PET_NAME", "");
            String sex = preferences.getString("Sex", "");
            String instruction = preferences.getString("INSTRUCTION", "");
            String PetAge = preferences.getString("PetAge", "");
            String location = preferences.getString("LOCATION", "");
            String price = preferences.getString("PRICE", "");
            String duration = preferences.getString("DURATION", "");
            String imageUrl = preferences.getString("IMAGE_URL", "");
            String CustomerName = preferences.getString("CUSTOMER_NAME", "");
            String CustomerEmail = preferences.getString("CUSTOMER_EMAIL", "");
            String CustomerPhone = preferences.getString("CUSTOMER_PHONE", "");
            String Status = preferences.getString("Status", "");


            Picasso.Builder builder = new Picasso.Builder(this);
            builder.listener((picasso, uri, exception) -> {
                exception.printStackTrace();
            });
            Picasso picasso = builder.build();


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Orders").document(userId);
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String status = documentSnapshot.getString("Status");
                            runOnUiThread(() -> {
                                if ("Completed".equals(status)) {
                                    laorderActive.setVisibility(View.VISIBLE);
                                } else {
                                    laorderActive.setVisibility(View.GONE);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.d("StatusComparison", "Failed to fetch status: " + e.getMessage());
                    });

            if (!imageUrl.isEmpty()) {
                picasso.load(imageUrl).into(imvPetAJ);
            } else {
                imvPetAJ.setVisibility(View.GONE);
            }
            txvPetnameAJ.setText(petName);
            txvDurationAJ.setText(duration);
            txvPetTypeAJ.setText(petType);
            txvPetInstructionsAJ.setText(instruction);
            txvLocationAJ.setText(location);
            txvPriceAJ.setText(price);
            txvSexAJ.setText(sex);
            txvPetAgeAJ.setText(PetAge);
            txvCustName.setText(CustomerName);
            txvCustEmail.setText(CustomerEmail);
            txvCustPhone.setText(CustomerPhone);


            btnCompleteOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String userId = preferences.getString("USER_ID", "");


                    DocumentReference userRef = db.collection("Orders").document(userId);
                    userRef.update("Status", "Completed")
                            .addOnSuccessListener(aVoid -> {
                                Log.d("StatusUpdate", "Status updated to 'Completed' for user");
                                Toast.makeText(getApplicationContext(), "Status updated successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.w("StatusUpdate", "Error updating status", e);
                                Toast.makeText(getApplicationContext(), "Failed to update status", Toast.LENGTH_SHORT).show();
                            });
                }
            });

            Button btnGiveFeedback = findViewById(R.id.btnGiveFeedback);
            btnGiveFeedback.setOnClickListener(v -> {

                Dialog feedbackDialog = new Dialog(this);
                feedbackDialog.setContentView(R.layout.feedback_form); // Replace with your feedback form layout
                EditText feedbackEditText = feedbackDialog.findViewById(R.id.feedbackEditText);
                Button submitFeedbackButton = feedbackDialog.findViewById(R.id.submitFeedbackButton);

                String CurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference orderRef = db.collection("Users").document(CurrentUserID);

                orderRef.get().addOnSuccessListener(caregiverSnapshot -> {
                            if (caregiverSnapshot.exists()) {
                                String caregiverName = caregiverSnapshot.getString("CustomerName");

                                submitFeedbackButton.setOnClickListener(submitView -> {
                                    String feedback = feedbackEditText.getText().toString();
                                    String collectionPath = "Feedback";
                                    String userDocumentPath = userId;
                                    Map<String, Object> feedbackData = new HashMap<>();
                                    feedbackData.put("feedback", feedback);
                                    feedbackData.put("feedbackBy",caregiverName);

                                    db.collection(collectionPath)
                                            .document(userDocumentPath)
                                            .collection("userFeedbacks")
                                            .add(feedbackData)
                                            .addOnSuccessListener(documentReference -> {
                                                Log.d("Feedback", "Feedback added with ID: " + documentReference.getId());
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.w("Feedback", "Error adding feedback", e);
                                            });
                                    feedbackDialog.dismiss();
                                    clearJobDetails(getApplicationContext());
                                    Toast.makeText(getApplicationContext(), "Order Details Removed", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                // Document for user doesn't exist
                                Log.d("Firestore", "User document does not exist");
                            }
                        });
                feedbackDialog.show();
            });
        }
    }
    private void clearJobDetails(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("JobDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
    }
