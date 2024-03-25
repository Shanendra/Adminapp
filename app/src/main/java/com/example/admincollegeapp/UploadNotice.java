package com.example.admincollegeapp;




import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadNotice extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 1;

    private CardView addImage;
    private ImageView noticeImageView;
    private EditText noticeTitle;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Button uploadNoticeBtn;
    private Bitmap bitmap;
    private ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Notice");
        storageReference = FirebaseStorage.getInstance().getReference().child("Notice");
        progressDialog = new ProgressDialog(this);

        addImage = findViewById(R.id.addimage);
        noticeImageView = findViewById(R.id.noticeImageView);
        noticeTitle = findViewById(R.id.noticeTitle);
        uploadNoticeBtn = findViewById(R.id.uploadNoticeBtn);

        uploadNoticeBtn.setOnClickListener(v -> uploadNotice());

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    noticeImageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        addImage.setOnClickListener(v -> openGallery());
    }

    private void uploadNotice() {
        String title = noticeTitle.getText().toString().trim();
        if (title.isEmpty()) {
            noticeTitle.setError("Empty!");
            noticeTitle.requestFocus();
            return;
        }
        if (bitmap == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Uploading ....");
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
                uploadData(title, downloadUrl);
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(UploadNotice.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadData(String title, String downloadUrl) {
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
        NoticeData noticeData = new NoticeData(title, date, time, downloadUrl, uniqueKey);
        databaseReference.child(uniqueKey).setValue(noticeData)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(UploadNotice.this, "Notice Uploaded!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(UploadNotice.this, "Failed to upload notice: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickImage);
    }
}

