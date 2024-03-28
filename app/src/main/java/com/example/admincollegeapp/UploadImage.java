
package com.example.admincollegeapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class UploadImage extends AppCompatActivity {

    // UI Elements
    private ImageView galleryImageView;
    private Spinner imageCategory;
    private Button uploadImage;
    private CardView selectImage;

    // Data
    private String category;
    private Bitmap bitmap;

    // Firebase
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    // Progress Dialog
    private ProgressDialog progressDialog;

    // Activity Result Launcher
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_image);

        // Initialize UI Elements
        selectImage = findViewById(R.id.addGalleryImage);
        imageCategory = findViewById(R.id.image_category);
        uploadImage = findViewById(R.id.uploadImageBtn);
        galleryImageView = findViewById(R.id.galleryImageView);

        // Firebase Initialization
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Gallery");
        storageReference = FirebaseStorage.getInstance().getReference().child("Gallery");

        // Progress Dialog Initialization
        progressDialog = new ProgressDialog(this);

        // Spinner Initialization
        String[] items = new String[]{"Select Category", "Convocation", "VITS ON BEATS", "College Fest", "HORIZON", "Independence Day", "Other Event"};
        imageCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));

        // Spinner Item Selected Listener
        imageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = imageCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Upload Image Button Click Listener
        uploadImage.setOnClickListener(v -> {
            if (bitmap == null) {
                Toast.makeText(UploadImage.this, "Please Upload Image!", Toast.LENGTH_SHORT).show();
            } else if (category.equals("Select Category")) {
                Toast.makeText(UploadImage.this, "Please Select Category!", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.setMessage("Uploading...");
                progressDialog.show();
                uploadImage();
            }
        });

        // Select Image CardView Click Listener
        selectImage.setOnClickListener(v -> openGallery());

        // Activity Result Launcher Initialization
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    galleryImageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Upload Image to Firebase Storage
    private void uploadImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageData = baos.toByteArray();
        String imageName = "image_" + System.currentTimeMillis() + ".jpg";
        StorageReference filePath = storageReference.child(imageName);
        UploadTask uploadTask = filePath.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                uploadData(category, downloadUrl);
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(UploadImage.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Upload Image Data to Firebase Database
    private void uploadData(String category, String downloadUrl) {
        String uniqueKey = databaseReference.push().getKey();
        if (uniqueKey == null) {
            Toast.makeText(this, "Failed to generate unique key", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String date = dateFormat.format(cal.getTime());
        String time = timeFormat.format(cal.getTime());
        NoticeData noticeData = new NoticeData(category, date, time, downloadUrl, uniqueKey);
        databaseReference.child(category).setValue(noticeData)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(UploadImage.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(UploadImage.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Open Gallery to Select Image
    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickImage);
    }
}












