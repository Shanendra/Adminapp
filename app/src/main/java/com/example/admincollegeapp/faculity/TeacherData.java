package com.example.admincollegeapp.faculity;

public class TeacherData {

private  String name,email,post,image,key;


    public TeacherData() {
    }

    public TeacherData(String name, String email, String post, String image, String key) {
        this.name = name;
        this.email = email;
        this.post = post;
        this.image = image;
        this.key = key;
    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPost() {
        return post;
    }

    public String getImage() {
        return image;
    }

    public String getKey() {
        return key;
    }
}

