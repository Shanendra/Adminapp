package com.example.admincollegeapp.faculity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.admincollegeapp.R;
import com.example.admincollegeapp.UploadPDFActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UpdateFaculity extends AppCompatActivity {


    FloatingActionButton fab ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_faculity);


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddTeachers.class)));

    }
}