 package com.example.admincollegeapp.faculity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.admincollegeapp.NoticeData;
import com.example.admincollegeapp.R;
import com.example.admincollegeapp.UploadImage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

 public class AddTeachers extends AppCompatActivity {


    private ImageView addTeacherImage;
    private EditText addTeacherName, addTeacherEmail, addTeacherPost;
    private Spinner addTeacherCategory;
    private String category;
    private String name, email, post, DowloadUrl = "";
    private Button addTeacherbtn;
     private DatabaseReference databaseReference;
     private StorageReference storageReference;
     private ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_teachers);


        addTeacherImage = findViewById(R.id.addTeacherImage);
        addTeacherName = findViewById(R.id.addTeacherName);
        addTeacherEmail = findViewById(R.id.addTeacherEmail);
        addTeacherPost = findViewById(R.id.addTeacherPost);
        addTeacherCategory = findViewById(R.id.addTeacherCategory);
        addTeacherbtn = findViewById(R.id.addTeacherbtn);

        // Firebase Initialization
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Gallery");
        storageReference = FirebaseStorage.getInstance().getReference().child("Gallery");

        // Progress Dialog Initialization
        progressDialog = new ProgressDialog(this);


        // Spinner Initialization
        String[] items = new String[]{"Select Department", "BCA", "BCOM", "BSC", "Other Department"};
        addTeacherCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));

        // Spinner Item Selected Listener
        addTeacherCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = addTeacherCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        addTeacherImage.setOnClickListener(v -> openGallery());

        // Activity Result Launcher Initialization
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    addTeacherImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        addTeacherbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });


    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickImage);
    }


    private void checkValidation() {
        name = addTeacherName.getText().toString();
        email = addTeacherEmail.getText().toString();
        post = addTeacherPost.getText().toString();
        if (name.isEmpty()) {
            addTeacherName.setError("Empty");
            addTeacherName.requestFocus();
        } else if (email.isEmpty()) {
            addTeacherEmail.setError("Empty");
            addTeacherEmail.requestFocus();
        } else if (post.isEmpty()) {
            addTeacherPost.setError("Empty");
            addTeacherPost.requestFocus();
        } else if (category.equals("Select Department")) {
            Toast.makeText(this, "Please Select Faculity Department", Toast.LENGTH_SHORT).show();
        }else if (bitmap == null){
            uploadImage();
        }else {
            uploadData();
        }


    }


        // Upload Image to Firebase Storage
        private void uploadImage() {

            progressDialog.setMessage("Uploading...");
            progressDialog.show();


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
                Toast.makeText(AddTeachers.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddTeachers.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(AddTeachers.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        
    }

