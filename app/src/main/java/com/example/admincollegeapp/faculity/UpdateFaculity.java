package com.example.admincollegeapp.faculity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.R;

import com.example.admincollegeapp.UploadPDFActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;




public class UpdateFaculity extends AppCompatActivity {

    FloatingActionButton fab;
    private RecyclerView BCADepartment, BCOMDepartment, BSCDepartment;
    private LinearLayout BCANodata, BCOMNodata, BSCNodata;
    private DatabaseReference databaseReference, dbRef;
    private StorageReference storageReference;
    private TeacherAdapter adapter1, adapter2, adapter3; // Separate adapters for each department
    private List<TeacherData> list1, list2, list3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_faculity);

        BCANodata = findViewById(R.id.BCANodata);
        BCOMNodata = findViewById(R.id.BCOMNodata);
        BSCNodata = findViewById(R.id.BSCNodata);

        BCADepartment = findViewById(R.id.BCADepartment);
        BCOMDepartment = findViewById(R.id.BCOMDepartment);
        BSCDepartment = findViewById(R.id.BSCDepartment);

        // Firebase Initialization
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Faculity");

        // Set up RecyclerViews and fetch data for each department
        setUpRecyclerView(BCADepartment, "BCA");
        setUpRecyclerView(BCOMDepartment, "BCOM");
        setUpRecyclerView(BSCDepartment, "BSC");

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(UpdateFaculity.this, AddTeachers.class)));
    }

    // Method to set up RecyclerView for a department and fetch data
    private void setUpRecyclerView(RecyclerView recyclerView, String department) {
        dbRef = databaseReference.child(department);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<TeacherData> dataList = new ArrayList<>(); // Initialize list for each department

                if (!snapshot.exists()) {
                    // Show no data layout if no data exists for the department
                    showNoDataLayout(recyclerView, department);
                } else {
                    // Iterate through dataSnapshot and add each faculty member to the list
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TeacherData data = dataSnapshot.getValue(TeacherData.class);
                        dataList.add(data);
                    }
                    // Set up RecyclerView and adapter with the fetched data
                    setUpRecyclerViewWithData(recyclerView, dataList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateFaculity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to show no data layout and hide RecyclerView for a department
    private void showNoDataLayout(RecyclerView recyclerView, String department) {
        switch (department) {
            case "BCA":
                BCANodata.setVisibility(View.VISIBLE);
                break;
            case "BCOM":
                BCOMNodata.setVisibility(View.VISIBLE);
                break;
            case "BSC":
                BSCNodata.setVisibility(View.VISIBLE);
                break;
        }
        recyclerView.setVisibility(View.GONE);
    }



    private void setUpRecyclerViewWithData(RecyclerView recyclerView, List<TeacherData> dataList) {
        if (recyclerView.getId() == R.id.BCADepartment) {
            adapter1 = new TeacherAdapter(dataList, UpdateFaculity.this);
            BCADepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculity.this));
            BCADepartment.setAdapter(adapter1);
            BCANodata.setVisibility(dataList.isEmpty() ? View.VISIBLE : View.GONE);
        } else if (recyclerView.getId() == R.id.BCOMDepartment) {
            adapter2 = new TeacherAdapter(dataList, UpdateFaculity.this);
            BCOMDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculity.this));
            BCOMDepartment.setAdapter(adapter2);
            BCOMNodata.setVisibility(dataList.isEmpty() ? View.VISIBLE : View.GONE);
        } else if (recyclerView.getId() == R.id.BSCDepartment) {
            adapter3 = new TeacherAdapter(dataList, UpdateFaculity.this);
            BSCDepartment.setLayoutManager(new LinearLayoutManager(UpdateFaculity.this));
            BSCDepartment.setAdapter(adapter3);
            BSCNodata.setVisibility(dataList.isEmpty() ? View.VISIBLE : View.GONE);
        }
        recyclerView.setVisibility(dataList.isEmpty() ? View.GONE : View.VISIBLE);
    }


}
