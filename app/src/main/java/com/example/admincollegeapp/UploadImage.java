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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadImage extends AppCompatActivity {



    private ImageView galleryImageView;
    private Spinner  imageCategory;
    private Button   uploadImage;
    private CardView  selectImage;
    private  String category;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Bitmap bitmap;



    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_image);


        selectImage = findViewById(R.id.addGalleryImage);
        imageCategory= findViewById(R.id.image_category);
        uploadImage=findViewById(R.id.uploadImageBtn);
        galleryImageView=findViewById(R.id.galleryImageView);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Gallery");
        storageReference = FirebaseStorage.getInstance().getReference().child("Gallery");


        progressDialog = new ProgressDialog(this);

        String[] items = new String[]{"Select Category","Convocation","VITS ON BEATS","College Fest","HORIZON","Independence Day","Other Event"};
        imageCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,items));


        imageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = imageCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (bitmap == null){
                     Toast.makeText(UploadImage.this, "Please Upload Image! ", Toast.LENGTH_SHORT).show();
                 } else if (category.equals("Select Category")) {
                     Toast.makeText(UploadImage.this, "Please Select Catergory! ", Toast.LENGTH_SHORT).show();
                 }else {
                     progressDialog.setMessage("Uploading...");
                     progressDialog.show();
                     uploadImage();
                 }
            }
        });



        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });




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
                uploadData(category,downloadUrl);
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(UploadImage.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


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

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickImage);
    }
}