package com.example.admincollegeapp;



import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UploadPDFActivity extends AppCompatActivity {

    // Constants
    private static final int REQUEST_CODE_PICK_FILE = 1;

    // UI Elements
    private EditText PDFTitle;
    private Button uploadFileBtn;

    // Firebase
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    // Uri for File
    private Uri fileUri;

    // Progress Dialog
    private ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdfactivity);

        // Firebase Initialization
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Files");
        storageReference = FirebaseStorage.getInstance().getReference().child("Files");

        // Progress Dialog Initialization
        progressDialog = new ProgressDialog(this);

        // Initialize UI Elements
        PDFTitle = findViewById(R.id.PDFtitle);
        uploadFileBtn = findViewById(R.id.uploadPDFBtn);

        // Upload File Button Click Listener
        uploadFileBtn.setOnClickListener(v -> uploadFile());

        // Open Gallery to Pick File
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Accept all file types
        String[] mimeTypes = {"application/pdf", "application/msword", "application/vnd.ms-powerpoint", "application/vnd.ms-excel"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CODE_PICK_FILE);
    }

    private void uploadFile() {
        if (fileUri != null) {
            // You can upload the file to Firebase storage here
            // Example: storageReference.child("files").child(fileUri.getLastPathSegment()).putFile(fileUri);

            // You can also save other details like title to Firebase database if needed
            String title = PDFTitle.getText().toString().trim();
            // Example: databaseReference.push().setValue(new File(title, fileUri.toString()));

            Toast.makeText(this, "File Uploaded Successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            Toast.makeText(this, "File Selected", Toast.LENGTH_SHORT).show();
        }
    }
}










































//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//public class UploadPDFActivity extends AppCompatActivity {
//
//
//    // Constants
//    private static final int REQUEST_CODE_PICK_IMAGE = 1;
//
//    // UI Elements
//    private CardView addPDF;
//    private EditText pdfTitle;
//    private Button uploadPDFBtn;
//
//    // Firebase
//    private DatabaseReference databaseReference;
//    private StorageReference storageReference;
//
//    // Bitmap for image
//    private Uri PdfData;
//
//    // Progress Dialog
//    private ProgressDialog progressDialog;
//
//    // Activity Result Launcher
//    private ActivityResultLauncher<Intent> galleryLauncher;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_upload_pdfactivity);
//
//
//        // Firebase Initialization
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("Notice");
//        storageReference = FirebaseStorage.getInstance().getReference().child("Notice");
//
//        // Progress Dialog Initialization
//        progressDialog = new ProgressDialog(this);
//
//        // Initialize UI Elements
//        addPDF = findViewById(R.id.addPDF);
//        //  noticeImageView = findViewById(R.id.noticeImageView);
//        pdfTitle = findViewById(R.id.noticePDFtitle);
//        uploadPDFBtn = findViewById(R.id.uploadPDFBtn);
//
//
//        // Gallery Launcher Initialization
//        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                PdfData = result.getData().getData();
//                Toast.makeText(this, ""+PdfData, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//        // Add Image Click Listener
//        addPDF.setOnClickListener(v -> openGallery());
//    }
//
//    private void openGallery() {
//         Intent intent = new Intent() ;
//         intent.setType("pdf/docs/ppt");
//         intent.setAction(Intent.ACTION_GET_CONTENT);
//         startActivityForResult(Intent.createChooser(intent,"Select Pdf File"),REQUEST_CODE_PICK_IMAGE);
//    }
//
//    }
//
//
//
