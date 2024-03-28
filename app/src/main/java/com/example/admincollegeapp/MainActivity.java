package com.example.admincollegeapp;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.TextView;
import android.os.Bundle;
import android.view.View;



public class MainActivity extends AppCompatActivity {

    TextView textView;
    TextView textupl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textUpload);
        textupl = findViewById(R.id.textuploadimg);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch UploadNotice activity
                Intent intent = new Intent(MainActivity.this, UploadNotice.class);
                startActivity(intent);
            }
        });

        textupl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch UploadImage activity
                Intent intent = new Intent(MainActivity.this, UploadImage.class);
                startActivity(intent);
            }
        });
    }
}


