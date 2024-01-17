package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class active_booking extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ImageView imvPetImageAc;
    TextView txvPetNameAc, txvPetAgeAc, txvPetInstructionsAc, txvPetStatusAc,
            txvCareNameAc, txvCarePhoneAc, txvCareLocationAc,txvPetPriceAc;
    Button btnBookCancel, btnContactCaregiver;
    LinearLayout linAcBook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_booking);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        imvPetImageAc = findViewById(R.id.imvPetImageAc);
        txvPetNameAc = findViewById(R.id.txvPetNameAc);
        txvPetAgeAc = findViewById(R.id.txvPetAgeAc);
        txvPetInstructionsAc = findViewById(R.id.txvPetInstructionsAc);
        txvPetStatusAc = findViewById(R.id.txvPetStatusAc);
        txvCareNameAc = findViewById(R.id.txvCareNameAc);
        txvCarePhoneAc = findViewById(R.id.txvCarePhoneAc);
        txvPetPriceAc = findViewById(R.id.txvPetPriceAc);
        txvCareLocationAc = findViewById(R.id.txvCareEmailAc);
        btnBookCancel = findViewById(R.id.btnBookCancel);
        btnContactCaregiver = findViewById(R.id.btnContactCaregiver);
        linAcBook =findViewById(R.id.linAcBook);

        if (currentUser != null) {
            String userId = currentUser.getUid();

            DocumentReference docRef = db.collection("Orders").document(userId);
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {

                    String petName = documentSnapshot.getString("PetName");
                    String petAge = documentSnapshot.getString("PetAge");
                    String instructions = documentSnapshot.getString("Instructions");
                    String status = documentSnapshot.getString("Status");
                    String price = documentSnapshot.getString("Price");

                    if (status != null && status.equals("onGoing")) {
                        findViewById(R.id.laCareDetails).setVisibility(View.VISIBLE);
                    } else if (status != null && status.equals("Completed")) {
                        findViewById(R.id.feedbackAddLay).setVisibility(View.VISIBLE);
                    }

                    txvPetPriceAc.setText("Price -"+price );
                    txvPetNameAc.setText("Pet Name -"+petName);
                    txvPetAgeAc.setText("Age -"+petAge+" years");
                    txvPetInstructionsAc.setText("Instruction -"+instructions);
                    txvPetStatusAc.setText("Status -"+status);

                    String petImageUrl = documentSnapshot.getString("imageUrl");
                    btnBookCancel.setOnClickListener(view -> {
                        deleteOrder(currentUser.getUid(),petImageUrl);
                    });

                    ImageView imvPetImageAc = findViewById(R.id.imvPetImageAc);

                        if (petImageUrl != null && !petImageUrl.isEmpty()) {
                        Picasso.Builder builderPet = new Picasso.Builder(this);
                        builderPet.listener((picasso, uri, exception) -> {
                            exception.printStackTrace();
                        });
                        Picasso picassoPet = builderPet.build();

                        try {
                            Uri uriPet = Uri.parse(petImageUrl);
                            picassoPet.load(uriPet).into(imvPetImageAc);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load pet image.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    linAcBook.setVisibility(View.VISIBLE);

                    String caregiverName = documentSnapshot.getString("CustomerName");
                    String caregiverPhone = documentSnapshot.getString("CustomerPhone");
                    String caregiverEmail = documentSnapshot.getString("CustomerEmail");
                    String caregiverID = documentSnapshot.getString("CaregiverID");

                    Button btnGiveFeedback = findViewById(R.id.btnLoadFeedback);
                    String CurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DocumentReference orderRef = db.collection("Users").document(CurrentUserID);


                    btnGiveFeedback.setOnClickListener(v -> {

                        Dialog feedbackDialog = new Dialog(this);
                        feedbackDialog.setContentView(R.layout.feedback_form);
                        EditText feedbackEditText = feedbackDialog.findViewById(R.id.feedbackEditText);
                        Button submitFeedbackButton = feedbackDialog.findViewById(R.id.submitFeedbackButton);

                        orderRef.get().addOnSuccessListener(caregiverSnapshot -> {
                            if (caregiverSnapshot.exists()) {
                                String CustomerName = caregiverSnapshot.getString("CustomerName");
                        submitFeedbackButton.setOnClickListener(submitView -> {
                            String feedback = feedbackEditText.getText().toString();
                            String collectionPath = "Feedback";
                            String userDocumentPath = caregiverID;
                            Map<String, Object> feedbackData = new HashMap<>();
                            feedbackData.put("feedback", feedback);
                            feedbackData.put("feedbackBy",CustomerName);

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
                            deleteOrder(currentUser.getUid(),petImageUrl);
                          Toast.makeText(getApplicationContext(), "Feedback Added Successfully", Toast.LENGTH_SHORT).show();
                        });
                            } else {
                                // Document for user doesn't exist
                                Log.d("Firestore", "User document does not exist");
                            }
                        });

                        feedbackDialog.show();
                    });

                    TextView txvCareNameAc = findViewById(R.id.txvCareNameAc);
                    txvCareNameAc.setText(caregiverName);
                    TextView txvCarePhoneAc = findViewById(R.id.txvCarePhoneAc);
                    txvCarePhoneAc.setText(caregiverPhone);
                    TextView txvCareLocationAc = findViewById(R.id.txvCareEmailAc);
                    txvCareLocationAc.setText(caregiverEmail);

                }else{
                    Toast.makeText(this, "No active bookings found.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to get details.", Toast.LENGTH_SHORT).show();
            });
        }
    }
    private void deleteOrder(String orderId, String imageUrl) {
        DocumentReference orderRef = db.collection("Orders").document(orderId);
        orderRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Order canceled successfully.", Toast.LENGTH_SHORT).show();
                    linAcBook.setVisibility(View.GONE);
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        deleteImage(imageUrl);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to cancel order.", Toast.LENGTH_SHORT).show();
                });
    }
    private void deleteImage(String imageUrl) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        storageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Image deleted successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete image.", Toast.LENGTH_SHORT).show();
                });
    }
}