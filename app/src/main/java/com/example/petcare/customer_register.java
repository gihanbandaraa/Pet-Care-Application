package com.example.petcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.Map;

public class customer_register extends AppCompatActivity {

    EditText edtNameRegCust,edtEmailRegCust,edtPassRegCust,edtCPassRegCust,edtAddressRegCust,edtPhoneRegCust;

    Button btnRegCust;
    ImageView imvCustImage;

    ImageButton imbCustImage;
    ProgressBar pBCustReg;
    FirebaseAuth firebaseAuth;

    FirebaseFirestore database;

    private static final int REQUEST_IMAGE_GALLERY = 2;


    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);


        edtNameRegCust =findViewById(R.id.edtNameRegCust);
        edtEmailRegCust=findViewById(R.id.edtEmailRegCust);
        edtPassRegCust=findViewById(R.id.edtPassRegCust);
        edtCPassRegCust =findViewById(R.id.edtCPassRegCust);
        edtAddressRegCust=findViewById(R.id.edtAddressRegCust);
        edtPhoneRegCust=findViewById(R.id.edtPhoneRegCust);


        btnRegCust = findViewById(R.id.btnRegCust);
        pBCustReg= findViewById(R.id.pBCustReg);
        imvCustImage =findViewById(R.id.imvCustImage);
        imbCustImage =findViewById(R.id.imbCustImage);


        firebaseAuth =FirebaseAuth.getInstance();

        database =FirebaseFirestore.getInstance();

        imbCustImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });



        btnRegCust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =edtEmailRegCust.getText().toString().trim();
                String name=edtNameRegCust.getText().toString().trim();
                String address=edtAddressRegCust.getText().toString().trim();
                String phone=edtPhoneRegCust.getText().toString().trim();
                String password =edtPassRegCust.getText().toString().trim();
                String cpassword =edtCPassRegCust.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    edtEmailRegCust.setError("Email Address is Required");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    edtPassRegCust.setError("Password is Required");
                    return;
                }

                if (TextUtils.isEmpty(cpassword)){
                    edtCPassRegCust.setError("Confirm password is Required");
                    return;
                }
                if (password.length() >6){
                    edtPassRegCust.setError("Enter 6 Digit Password");
                    return;
                }

                if (!password.equals(cpassword)){
                    edtPassRegCust.setError("Password is Not Matched");
                    edtCPassRegCust.setError("Password is Not Matched");
                    return;
                }

                pBCustReg.setVisibility(View.VISIBLE);

                //Register User

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser =firebaseAuth.getCurrentUser();

                            Toast.makeText(customer_register.this, "User Created Successfully", Toast.LENGTH_SHORT).show();

                            DocumentReference df =database.collection("Users").document(firebaseUser.getUid());

                            Map<String,Object> userInfo = new HashMap<>();

                            userInfo.put("CustomerName",name);
                            userInfo.put("CustomerEmail",email);
                            userInfo.put("CustomerAddress",address);
                            userInfo.put("CustomerPhone",phone);

                            userInfo.put("isCustomer","1");
                            if (selectedImage != null) {
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("customer_images");
                                StorageReference imageRef = storageRef.child("image_" + firebaseUser.getUid() + ".jpg");

                                imageRef.putFile(selectedImage)
                                        .addOnSuccessListener(taskSnapshot -> {
                                            // Image uploaded successfully, get the URL
                                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                                                String imageUrl = uri.toString();
                                                userInfo.put("image_url", imageUrl);

                                                df.set(userInfo)
                                                        .addOnSuccessListener(aVoid -> {
                                                            startActivity(new Intent(getApplicationContext(), customer_home.class));
                                                            finish();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(customer_register.this, "Error In Database", Toast.LENGTH_SHORT).show();
                                                        });
                                            });
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(customer_register.this, "Error can't upload Image", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                df.set(userInfo)
                                        .addOnSuccessListener(aVoid -> {
                                            // Data (excluding image URL) saved successfully
                                            startActivity(new Intent(getApplicationContext(), customer_home.class));
                                            finish();
                                        });
                            }
                        }
                        else{
                            Toast.makeText(customer_register.this, "Error Occurred"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
            imvCustImage.setImageURI(selectedImage);
        }
    }
}