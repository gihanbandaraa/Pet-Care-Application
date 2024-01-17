package com.example.petcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class JobsAdapter extends RecyclerView.Adapter<JobsAdapter.JobsViewHolder>  {

    private List<Jobs> jobsList;
    private Context context;
    private List<Jobs> filteredList;

    public JobsAdapter(find_job_care findJobCare, List<Jobs> jobsList) {
        this.context = findJobCare; // Set the context here
        this.jobsList = jobsList;
        this.filteredList = new ArrayList<>(jobsList);
        Log.d("JobsAdapter", "Context: " + context);
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchText = charSequence.toString().toLowerCase().trim();
                FilterResults results = new FilterResults();

                if (searchText.isEmpty()) {
                    results.values = jobsList;
                } else {
                    List<Jobs> filtered = new ArrayList<>();
                    for (Jobs job : jobsList) {

                        if (job.getPetType().toLowerCase().contains(searchText)
                                || job.getLocation().toLowerCase().contains(searchText)) {
                            filtered.add(job);
                        }
                    }
                    results.values = filtered;
                }

                results.count = ((List<Jobs>) results.values).size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (List<Jobs>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public JobsAdapter(Context context, List<Jobs> jobsList) {
        this.context = context;
        this.jobsList = jobsList;
        this.filteredList = new ArrayList<>(jobsList);
        Log.d("JobsAdapter", "Context: " + context);
    }

    public JobsAdapter(List<Jobs> jobsList) {
        this.jobsList = jobsList;
        this.filteredList = new ArrayList<>(jobsList);
    }

    public static class JobsViewHolder extends RecyclerView.ViewHolder {
        TextView petType, location, price, duration;
        ImageView imageView;
        Button btnGetJob;

        public JobsViewHolder(View itemView) {
            super(itemView);
            petType = itemView.findViewById(R.id.txvPetType);
            location = itemView.findViewById(R.id.txvLocation);
            price = itemView.findViewById(R.id.txvPrice);
            duration = itemView.findViewById(R.id.txvDuration);
            imageView = itemView.findViewById(R.id.imvpetImageJob);
            btnGetJob =itemView.findViewById(R.id.btnGetJob);
        }
    }

    @NonNull
    @Override
    public JobsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("JobsAdapter", "Context in onCreateViewHolder: " + parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jobitem, parent, false);
        return new JobsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobsViewHolder holder, int position) {
        Log.d("JobsAdapter", "Context in hasActiveJob: " + context);
        Jobs job = filteredList.get(position);


        if (job != null) {
            holder.petType.setText(job.PetType);
            holder.location.setText(job.Location);
            holder.price.setText(job.Price);
            holder.duration.setText(job.Duration);


            Picasso.Builder builder = new Picasso.Builder(holder.imageView.getContext());
            builder.listener((picasso, uri, exception) -> {
                exception.printStackTrace();
            });
            Picasso picasso = builder.build();

            picasso.load(job.getImageUrl()).into(holder.imageView);
        }
        holder.btnGetJob.setOnClickListener(view -> {


            if (!hasActiveJob(context)) {

                String petType = job.getPetType();
                String location = job.getLocation();
                String price = job.getPrice();
                String duration = job.getDuration();
                String imageUrl = job.imageUrl;
                String userId = job.getUserId();

                fetchUserDetails(userId, petType, location, price, duration, imageUrl);

            } else {

                 Toast.makeText(context, "You already have an active job", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return filteredList != null ? filteredList.size() : 0; // Return size of filteredList if it's not null
    }
    public void fetchUserDetails(String userId, String petType, String location, String price, String duration, String imageUrl){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("Orders").document(userId);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String petName = documentSnapshot.getString("PetName");
                        String Instruction = documentSnapshot.getString("Instructions");
                        String Sex = documentSnapshot.getString("Sex");
                        String PetAge = documentSnapshot.getString("PetAge");
                        String Status = documentSnapshot.getString("Status");

                        // Retrieve customer's name and email using the userId
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        DocumentReference userDocRef = db.collection("Users").document(userId);
                        userDocRef.get().addOnSuccessListener(userSnapshot -> {
                                    if (userSnapshot.exists()) {
                                        String CustomerName = userSnapshot.getString("CustomerName");
                                        String CustomerEmail = userSnapshot.getString("CustomerEmail");
                                        String CustomerPhone = userSnapshot.getString("CustomerPhone");

                                        // Save all details to SharedPreferences and start activity
                                        saveJobDetails(context, petType, location, price, duration, imageUrl, userId, petName, Instruction, Sex, PetAge, CustomerName, CustomerEmail, CustomerPhone,Status);
                                        String currentUserId = currentUser.getUid();

                                        DocumentReference caregiverRef = db.collection("Users").document(currentUserId);
                                        caregiverRef.get().addOnSuccessListener(caregiverSnapshot -> {
                                            if (caregiverSnapshot.exists()) {
                                                String caregiverName = caregiverSnapshot.getString("CustomerName");
                                                String caregiverEmail = caregiverSnapshot.getString("CustomerEmail");
                                                String caregiverPhone = caregiverSnapshot.getString("CustomerPhone");

                                                String caregiverID =String.valueOf(currentUserId);
                                                DocumentReference orderRef = db.collection("Orders").document(userId);
                                                orderRef
                                                        .update(
                                                                "CustomerName", caregiverName,
                                                                "CustomerEmail", caregiverEmail,
                                                                "Status", "onGoing","CustomerPhone",caregiverPhone,"CaregiverID",caregiverID
                                                        )
                                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "Orders collection updated successfully"))
                                                        .addOnFailureListener(e -> Log.e("Firestore", "Error updating Orders collection: " + e.getMessage()));
                                            } else {

                                                Log.d("Firestore", "User document does not exist");
                                            }
                                        }).addOnFailureListener(e -> {

                                            Log.e("Firestore", "Error fetching user details: " + e.getMessage());
                                        });
                                    } else {

                                    }
                                })
                                .addOnFailureListener(e -> {

                                });
                    }
                });
    }

    private void saveJobDetails(Context context, String petType, String location, String price, String duration,
                                String imageUrl, String userId, String petName,
                                String Instruction,String Sex,String PetAge,String CustomerName,String CustomerEmail,String CustomerPhone ,String Status) {
        SharedPreferences preferences = context.getSharedPreferences("JobDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("PET_TYPE", petType);
        editor.putString("PET_NAME", petName);
        editor.putString("Sex", Sex);
        editor.putString("PetAge", PetAge);
        editor.putString("INSTRUCTION", Instruction);
        editor.putString("LOCATION", location);
        editor.putString("PRICE", price);
        editor.putString("DURATION", duration);
        editor.putString("IMAGE_URL", imageUrl);
        editor.putString("USER_ID", userId);
        editor.putString("CUSTOMER_NAME", CustomerName);
        editor.putString("CUSTOMER_EMAIL", CustomerEmail);
        editor.putString("CUSTOMER_PHONE", CustomerPhone);
        editor.putString("Status", Status);


        editor.apply();

        // Now that the job details are saved, proceed to start the view_job_process activity
        Intent intent = new Intent(context, view_job_process.class);
        intent.putExtra("PET_NAME", petName);
        intent.putExtra("Sex", Sex);
        intent.putExtra("PetAge", PetAge);
        intent.putExtra("INSTRUCTION", Instruction);
        intent.putExtra("PET_TYPE", petType);
        intent.putExtra("LOCATION", location);
        intent.putExtra("PRICE", price);
        intent.putExtra("DURATION", duration);
        intent.putExtra("IMAGE_URL", imageUrl);
        intent.putExtra("USER_ID", userId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    private boolean hasActiveJob(Context context) {
        Log.d("JobsAdapter", "Context in hasActiveJob: " + context);
        if (context != null) {
            SharedPreferences preferences = context.getSharedPreferences("JobDetails", Context.MODE_PRIVATE);
            return preferences.contains("USER_ID");
        } else {
            Log.e("JobsAdapter", "Context is null!");
        }
        return false;
    }
}

