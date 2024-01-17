package com.example.petcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class find_job_care extends AppCompatActivity {
    private RecyclerView recyclerView;
    private JobsAdapter jobsAdapter;
    private List<Jobs> jobsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_job_care);

        recyclerView = findViewById(R.id.rcvJobs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        jobsList = new ArrayList<>();


        jobsAdapter = new JobsAdapter(find_job_care.this, jobsList);
        recyclerView.setAdapter(jobsAdapter);

        EditText searchEditText = findViewById(R.id.editTextSearch);

        String defaultText = "D";
        searchEditText.setText(defaultText);
        searchEditText.setSelection(defaultText.length());
        int delayMillis = 100;

        new Handler().postDelayed(() -> {
            searchEditText.setText("");
        }, delayMillis);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                jobsAdapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fetchJobsFromFirestore();
    }

    private void fetchJobsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Orders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        jobsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Jobs job = document.toObject(Jobs.class);
                            jobsList.add(job);
                        }
                        jobsAdapter.notifyDataSetChanged();
                    } else {
                        // Handle failures
                    }
                });
    }

}