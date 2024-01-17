package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class firstSplashScreen extends AppCompatActivity {

    TextView txvGetStartbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_splash_screen);

        txvGetStartbtn = findViewById(R.id.txvGetStartbtn);

        txvGetStartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), choose_login.class));
            }
        });
    }
}