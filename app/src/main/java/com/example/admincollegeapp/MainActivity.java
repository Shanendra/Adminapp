package com.example.admincollegeapp;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.TextView;
import android.os.Bundle;








public class MainActivity extends AppCompatActivity {

    TextView textView, textupl, UploadPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextViews
        textView = findViewById(R.id.textUpload);
        textupl = findViewById(R.id.textuploadimg);
        UploadPDF = findViewById(R.id.UploadPDF);

        // Set onClick listeners for each TextView to launch respective activities
        UploadPDF.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UploadPDFActivity.class)));
        textupl.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UploadImage.class)));
        textView.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UploadNotice.class)));
    }
}



