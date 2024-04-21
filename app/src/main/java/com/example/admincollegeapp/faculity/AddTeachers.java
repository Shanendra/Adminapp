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

import com.example.admincollegeapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

 public class AddTeachers extends AppCompatActivity {


    private ImageView addTeacherImage;
    private EditText addTeacherName, addTeacherEmail, addTeacherPost;
    private Spinner addTeacherCategory;

    private String category;
    private String name, email, post, dowloadUrl = "";
    private Button addTeacherbtn;
     private DatabaseReference databaseReference,dbRef;
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
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Faculity");
        storageReference = FirebaseStorage.getInstance().getReference();

        // Progress Dialog Initialization
        progressDialog = new ProgressDialog(this);


        // Spinner Initialization
        String[] items = new String[]{"Select Category", "BCA", "BCOM", "BSC"};
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
        } else if (category.equals("Select Category")) {
            Toast.makeText(this, "Please Provide Category", Toast.LENGTH_SHORT).show();
        }else if (bitmap == null){
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            uploadImage();
        }else {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            uploadData(dowloadUrl);
        }


    }








     private void uploadImage() {
         // Compress the bitmap
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
         byte[] imageData = baos.toByteArray();

         // Generate a unique file name
         String fileName = System.currentTimeMillis() + ".jpg";

         // Create a reference to the Firebase Storage location
         StorageReference imageRef = storageReference.child("Faculity").child(fileName);

         // Upload the image data
         UploadTask uploadTask = imageRef.putBytes(imageData);
         uploadTask.addOnSuccessListener(taskSnapshot -> {
             // Image upload successful, now get the download URL
             imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                 // Get the download URL
                 String downloadUrl = uri.toString();
                 // Proceed to upload data with the download URL
                 uploadData(downloadUrl);
             }).addOnFailureListener(e -> {
                 // Handle failure to get download URL
                 progressDialog.dismiss();
                 Toast.makeText(AddTeachers.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
             });
         }).addOnFailureListener(e -> {
             // Handle image upload failure
             progressDialog.dismiss();
             Toast.makeText(AddTeachers.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
         });
     }








     private void uploadData(String downloadUrl) {
         String uniqueKey = databaseReference.push().getKey();

         if (uniqueKey == null) {
             Toast.makeText(this, "Failed to generate unique key", Toast.LENGTH_SHORT).show();
             return;
         }

         TeacherData teacherData = new TeacherData(name, email, post, downloadUrl, uniqueKey);

         // Use the category (department) as the key under which the data will be stored
         DatabaseReference departmentReference = databaseReference.child(category).child(uniqueKey);

         departmentReference.setValue(teacherData)
                 .addOnSuccessListener(aVoid -> {
                     progressDialog.dismiss();
                     Toast.makeText(AddTeachers.this, "Faculty Added!", Toast.LENGTH_SHORT).show();
                 })
                 .addOnFailureListener(e -> {
                     progressDialog.dismiss();
                     Toast.makeText(AddTeachers.this, "Something Went Wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                 });
     }



 }

