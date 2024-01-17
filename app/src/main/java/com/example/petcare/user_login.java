package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class user_login extends AppCompatActivity {

    EditText edtLogEmailCust,edtLogPasCust;

    TextView txvClickHereReg;
    Button btnLoginCust;
    ProgressBar pBLoginCust;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        edtLogEmailCust =findViewById(R.id.edtLogEmailCust);
        edtLogPasCust=findViewById(R.id.edtLogPassCust);

        btnLoginCust =findViewById(R.id.btnLoginCust);
        pBLoginCust =findViewById(R.id.pBLoginCust);

        txvClickHereReg =findViewById(R.id.txvClickHereReg);

        fstore =FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        txvClickHereReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),choose_login.class));
            }
        });


        btnLoginCust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =edtLogEmailCust.getText().toString().trim();
                String password =edtLogPasCust.getText().toString().trim();


                if (TextUtils.isEmpty(email)){
                    edtLogEmailCust.setError("Email Address is Required");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    edtLogPasCust.setError("Password is Required");
                    return;
                }

                if (password.length() >6){
                    edtLogPasCust.setError("Enter 6 Digit Password");
                    return;
                }

                pBLoginCust.setVisibility(View.VISIBLE);

                //User Authenticator

                fAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(user_login.this, "User Logged Successfully", Toast.LENGTH_SHORT).show();

                        checkUserRole(authResult.getUser().getUid());

                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(user_login.this, "Invalid Email Or Password " , Toast.LENGTH_SHORT).show();
                    pBLoginCust.setVisibility(View.INVISIBLE);
                });
            }
        });

    }

    private void checkUserRole(String uid) {
        DocumentReference df =fstore.collection("Users").document(uid);

        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Log.d("TAG","onSuccess: "+documentSnapshot.getData());

                if (documentSnapshot.getString("isCustomer")!=null){
                    startActivity(new Intent(getApplicationContext(),customer_home.class));
                    finish();
                }else if(documentSnapshot.getString("isCaregiver")!=null){
                    startActivity(new Intent(getApplicationContext(),caregiver_home.class));
                    finish();
                }

            }
        });
    }
}