package com.example.petcare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class book_caregiver extends AppCompatActivity {


    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    TextView txvStartDate,txvEndDate;
    ImageView imvPetBook;
    TextInputEditText edtPetName,edtPetAge,edtSpeInstructions,edtLocationBook;

    RadioButton rabDog,rabCat,rabMale,rabFemale;
    Button btnBookCare;
    ProgressBar pbBookCare;
    private Calendar startCalendar, endCalendar;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    final double DOG_PRICE_PER_DAY = 1000.0;
    final double CAT_PRICE_PER_DAY = 800.0;



    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_caregiver);

        imvPetBook =findViewById(R.id.imvPetBook);
        edtPetName =findViewById(R.id.edtPetName);
        edtPetAge =findViewById(R.id.edtPetAge);
        edtSpeInstructions =findViewById(R.id.edtSpeInstructions);
        edtLocationBook =findViewById(R.id.edtLocationBook);
        rabDog =findViewById(R.id.rabDog);
        rabCat =findViewById(R.id.rabCat);
        rabMale =findViewById(R.id.rabMale);
        rabFemale =findViewById(R.id.rabFemale);

        btnBookCare =findViewById(R.id.btnBookCare);

        txvStartDate = findViewById(R.id.txvStartDate);
        txvEndDate = findViewById(R.id.txvEndDate);

        pbBookCare = findViewById(R.id.pbBookCare);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        fAuth =FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser =fAuth.getCurrentUser();

        imvPetBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog();
            }
        });

        //Calender Open Part
        txvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(true);
            }
        });

        txvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(false);
            }
        });
        //End Of calender Open Part

        btnBookCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name=edtPetName.getText().toString().trim();
                String age=edtPetAge.getText().toString().trim();
                String instructions=edtSpeInstructions.getText().toString().trim();
                String startDate = convertCalendarToString(startCalendar);
                String endDate = convertCalendarToString(endCalendar);
                String location = edtLocationBook.getText().toString().trim();
                String userId= fAuth.getCurrentUser().getUid();

                String Sex ="";
                String petType = "";

                if (rabDog.isChecked()) {
                    petType = "Dog";
                } else if (rabCat.isChecked()) {
                    petType = "Cat";
                }
                if (rabMale.isChecked()) {
                    Sex = "Male";
                } else if (rabFemale.isChecked()) {
                    Sex = "Female";
                }

               //Calculate Price According To Pet Type and The Days that Caregiver had to watch out the pet
                long startTimeInMillis = startCalendar.getTimeInMillis();
                long endTimeInMillis = endCalendar.getTimeInMillis();
                long durationInMillis = endTimeInMillis - startTimeInMillis;
                int durationInDays = (int) (durationInMillis / (1000 * 60 * 60 * 24));
                double totalPrice;

                if (petType.equals("Dog")) {
                    totalPrice = DOG_PRICE_PER_DAY * durationInDays;
                } else if (petType.equals("Cat")) {
                    totalPrice = CAT_PRICE_PER_DAY * durationInDays;
                } else {
                    totalPrice = 0.0;
                }
                //End Of Calculation Price

                if (TextUtils.isEmpty(name)){
                    edtPetName.setError("Pet Name is Required");
                    return;
                }

                if (TextUtils.isEmpty(age)){
                    edtPetAge.setError("Age is Required");
                    return;
                }

                if (TextUtils.isEmpty(instructions)){
                    edtSpeInstructions.setError("Instructions is Required");
                    return;
                }

                if (TextUtils.isEmpty(startDate)){
                    txvStartDate.setError("Start Date is Required");
                    return;
                }
                if (TextUtils.isEmpty(endDate)){
                    txvEndDate.setError("End Date is Required");
                    return;
                }
                pbBookCare.setVisibility(View.VISIBLE);



                DocumentReference df =fstore.collection("Orders").document(firebaseUser.getUid());
                Toast.makeText(book_caregiver.this, "Booking Created Successfully", Toast.LENGTH_SHORT).show();
                Map<String,Object> userInfo = new HashMap<>();

                userInfo.put("userId",userId);
                userInfo.put("PetName",name);
                userInfo.put("PetAge",age);
                userInfo.put("Instructions",instructions);
                userInfo.put("Location",location);
                userInfo.put("PetType",petType);
                userInfo.put("Sex",Sex);
                userInfo.put("StartDate",startDate);
                userInfo.put("EndDate",endDate);
                userInfo.put("Price","Rs."+totalPrice);
                userInfo.put("Status","Pending");
                userInfo.put("Duration",startDate+" to "+endDate);


                if (selectedImage != null) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("pet_images");
                    StorageReference imageRef = storageRef.child("image_" + firebaseUser.getUid() + ".jpg");

                    imageRef.putFile(selectedImage)
                            .addOnSuccessListener(taskSnapshot -> {

                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String imageUrl = uri.toString();
                                    userInfo.put("imageUrl", imageUrl);
                                    df.set(userInfo)
                                            .addOnSuccessListener(aVoid -> {
                                                startActivity(new Intent(getApplicationContext(), customer_home.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(book_caregiver.this, "Error In Database", Toast.LENGTH_SHORT).show();
                                            });
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(book_caregiver.this, "Error can't upload Image", Toast.LENGTH_SHORT).show();
                            });
                }
                else {
                    df.set(userInfo)
                            .addOnSuccessListener(aVoid -> {
                                // Data (excluding image URL) saved successfully
                                startActivity(new Intent(getApplicationContext(), customer_home.class));
                                finish();
                            });
                }
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
            imvPetBook.setImageURI(selectedImage);
        }
    }

    private void showDatePickerDialog(final boolean isStartDate) {
        Calendar calendar = isStartDate ? startCalendar : endCalendar;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (isStartDate) {
                            startCalendar.set(Calendar.YEAR, year);
                            startCalendar.set(Calendar.MONTH, monthOfYear);
                            startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            txvStartDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        } else {
                            endCalendar.set(Calendar.YEAR, year);
                            endCalendar.set(Calendar.MONTH, monthOfYear);
                            endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            txvEndDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        }
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }
    private String convertCalendarToString(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day + "/" + month + "/" + year;
    }
}
