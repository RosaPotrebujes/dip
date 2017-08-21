package com.example.rosa.diplomska;

public class Post {
    private String user;
    private String activity;

    public Post(String user, String activity){
        this.user = user;
        this.activity = activity;
    }

    public String getUser(){
        return this.user;
    }

    public String getActivity(){
        return  this.activity;
    }

}
