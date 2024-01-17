package com.example.petcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.Manifest;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class caregiver_register extends AppCompatActivity {

    EditText edtCareName,edtCareEmail,edtCarePhone,edtCareAddress,edtCarePassword,edtCareCPassword;

    Button btnCareReg;
    FirebaseAuth auth;
    FirebaseFirestore fstore;
    ProgressBar pbCareReg;

    CheckBox chkCareOffer1,chkCareOffer2,chkCareOffer3,chkCareOffer4,chkCareTypeDog,chkCareTypeCat;
    ImageButton imbCareImage;
    ImageView imvCareImage;

    private static final int REQUEST_IMAGE_GALLERY = 2;


    private Uri selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_register);

        edtCareName =findViewById(R.id.edtCareName);
        edtCarePhone =findViewById(R.id.edtCarePhone);
        edtCareAddress =findViewById(R.id.edtCareAddress);
        edtCareEmail=findViewById(R.id.edtCareEmail);
        edtCarePassword =findViewById(R.id.edtCarePassword);
        edtCareCPassword =findViewById(R.id.edtCareCPassword);


        btnCareReg =findViewById(R.id.btnCareReg);
        pbCareReg =findViewById(R.id.pbCareReg);

        auth =FirebaseAuth.getInstance();
        fstore =FirebaseFirestore.getInstance();

        chkCareOffer1 =findViewById(R.id.chkCareOffer1);
        chkCareOffer2 =findViewById(R.id.chkCareOffer2);
        chkCareOffer3=findViewById(R.id.chkCareOffer3);
        chkCareOffer4=findViewById(R.id.chkCareOffer4);

        chkCareTypeDog =findViewById(R.id.chkCareTypeDog);
        chkCareTypeCat =findViewById(R.id.chkCareTypeCat);

        imbCareImage = findViewById(R.id.imbCareImage);
        imvCareImage = findViewById(R.id.imvCareImage);

        imbCareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });


        //Experience Selection Part
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.actvYears);
        String[] yearsArray = getResources().getStringArray(R.array.years);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.custom_dropdown_item,
                yearsArray
        );

        autoCompleteTextView.setAdapter(adapter);

        final List<String> selectedYear = new ArrayList<>();

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            selectedYear.clear();
            selectedYear.add((String) parent.getItemAtPosition(position));
        });
        //End Of that part


        //Authentication and Store Data in Firestore

        btnCareReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =edtCareEmail.getText().toString().trim();
                String name=edtCareName.getText().toString().trim();
                String phone=edtCarePhone.getText().toString().trim();
                String password =edtCarePassword.getText().toString().trim();
                String cpassword =edtCareCPassword.getText().toString().trim();
                String address =edtCareAddress.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    edtCareEmail.setError("Email Address is Required");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    edtCarePassword.setError("Password is Required");
                    return;
                }

                if (TextUtils.isEmpty(cpassword)){
                    edtCareCPassword.setError("Confirm password is Required");
                    return;
                }
                if (password.length() >6){
                    edtCarePassword.setError("Enter 6 Digit Password");
                    return;
                }

                if (!password.equals(cpassword)){
                    edtCarePassword.setError("Password is Not Matched");
                    edtCareCPassword.setError("Password is Not Matched");
                    return;
                }

                pbCareReg.setVisibility(View.VISIBLE);

                List<String> selectedServices = new ArrayList<>();
                List<String> selectedTypes = new ArrayList<>();

                if (chkCareOffer1.isChecked()) {
                    selectedServices.add(chkCareOffer1.getText().toString());
                }
                if (chkCareOffer2.isChecked()) {
                    selectedServices.add(chkCareOffer2.getText().toString());
                }
                if (chkCareOffer3.isChecked()) {
                    selectedServices.add(chkCareOffer3.getText().toString());
                }
                if (chkCareOffer4.isChecked()) {
                    selectedServices.add(chkCareOffer4.getText().toString());
                }
                if (chkCareTypeDog.isChecked()) {
                    selectedTypes.add(chkCareTypeDog.getText().toString());
                }
                if (chkCareTypeCat.isChecked()) {
                    selectedTypes.add(chkCareTypeCat.getText().toString());
                }



                //Register User

                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser =auth.getCurrentUser();

                            Toast.makeText(caregiver_register.this, "User Created Successfully", Toast.LENGTH_SHORT).show();

                            DocumentReference df =fstore.collection("Users").document(firebaseUser.getUid());

                            Map<String,Object> userInfo = new HashMap<>();

                            userInfo.put("CustomerName",name);
                            userInfo.put("CustomerEmail",email);
                            userInfo.put("CustomerPhone",phone);
                            userInfo.put("CustomerAddress",address);
                            userInfo.put("Experience",selectedYear);
                            userInfo.put("Services", selectedServices);
                            userInfo.put("Type", selectedTypes);

                            userInfo.put("isCaregiver","1");

                            if (selectedImage != null) {
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("caregiver_images");
                                StorageReference imageRef = storageRef.child("image_" + firebaseUser.getUid() + ".jpg");

                                imageRef.putFile(selectedImage)
                                        .addOnSuccessListener(taskSnapshot -> {

                                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                                                String imageUrl = uri.toString();
                                                userInfo.put("image_url", imageUrl);

                                                df.set(userInfo)
                                                        .addOnSuccessListener(aVoid -> {
                                                            startActivity(new Intent(getApplicationContext(), caregiver_home.class));
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(caregiver_register.this, "Error In Database", Toast.LENGTH_SHORT).show();
                                                        });
                                            });
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(caregiver_register.this, "Error can't upload Image", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                df.set(userInfo)
                                        .addOnSuccessListener(aVoid -> {
                                            startActivity(new Intent(getApplicationContext(), caregiver_home.class));
                                            finish();
                                        });
                            }
                        }
                        else{
                            Toast.makeText(caregiver_register.this, "Error Occurred"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void showImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        getImageFromGallery();
                        break;
                }
            }
        });
        builder.show();
    }

    private void getImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_GALLERY && data != null) {
            selectedImage = data.getData();
            imvCareImage.setImageURI(selectedImage);
        }
    }
}