package com.example.admincollegeapp.faculity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admincollegeapp.R;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {

    private List<TeacherData> teacherList;
    private Context context;


    public TeacherAdapter(List<TeacherData> teacherList,Context context) {
        this.teacherList = teacherList;
        this.context = context;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faculity_item_layout, parent, false);
        return new TeacherViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        TeacherData item = teacherList.get(position);
        holder.name.setText(item.getName());
        holder.email.setText(item.getEmail());
        holder.post.setText(item.getPost());

        try {
            Context context = holder.itemView.getContext();
            Glide.with(context)
                    .load(item.getImage())
                    .apply(new RequestOptions().error(R.drawable.profile))
                    .into(holder.imageView);
        } catch (Exception e) {
            Log.e("TeacherAdapter", "Error loading image", e);
            // Provide a fallback image if loading fails
            holder.imageView.setImageResource(R.drawable.profile);
        }

        // Handle update button click
        holder.update.setOnClickListener(v -> {
            // You can implement your update logic here, or use an interface to communicate with the hosting activity/fragment
            Toast.makeText(context, "Update Teacher: " + item.getName(), Toast.LENGTH_SHORT).show();
        });
    }









    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public class TeacherViewHolder extends RecyclerView.ViewHolder {

        private TextView name,email,post;
        private Button update;
        private ImageView imageView;
        private TextView teacherSubjectTextView;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.teacherName);
            email = itemView.findViewById(R.id.teacherEmail);
            post = itemView.findViewById(R.id.teacherPost);
            update = itemView.findViewById(R.id.teacherUpdate);
            imageView = itemView.findViewById(R.id.teacherImage);
        }


    }
}

