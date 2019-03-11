package com.example.rosa.diplomska.model.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;

import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "user",
        indices = {
            @Index(value = "userId", unique = true),
            @Index("pending"),
            @Index(value = "username", unique = true)
        })

public class User extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "userId")
    private int userID;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "pending")
    private boolean pending;// = false;

    @Ignore
    private ObservableArrayList<Post> posts;
    @Ignore
    private ObservableArrayList<User> friends;
    @Ignore
    private ObservableArrayList<Post> favouritePosts;

    public User(int userID, String username, String email, String description, boolean pending) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.description = description;
        this.pending = pending;
    }

    @Ignore
    public User(int userID, String username, String email, String description,
                 ObservableArrayList<Post> posts, ObservableArrayList<User> friends){
        this.userID      = userID;
        this.username    = username;
        this.email       = email;
        this.description = description;
        this.posts       = posts;
        this.friends     = friends;
    }

    @Ignore
    public User(int userID, String username, String email){
        this.userID      = userID;
        this.username    = username;
        this.email       = email;
        this.description = "";
        this.posts       = new ObservableArrayList<>();
        this.friends     = new ObservableArrayList<>();
    }

    @Ignore
    public User(int userID, String username, String email, String description){
        this.userID      = userID;
        this.username    = username;
        this.email       = email;
        this.description = description;
        this.posts       = new ObservableArrayList<>();
        this.friends     = new ObservableArrayList<>();
    }

    @Ignore
    public User(){
        this.userID      = -1;
        this.username    = "";
        this.email       = "";
        this.description = "";
        this.posts       = new ObservableArrayList<>();
        this.friends     = new ObservableArrayList<>();
    }


    @Bindable
    public int getUserID(){
        return this.userID;
    }
    public void setUserID(int userID){
        this.userID = userID;
        notifyPropertyChanged(BR.userID);
    }

    @Bindable
    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username){
        this.username = username;
        notifyPropertyChanged(BR.username);
    }
    @Bindable
    public String getEmail(){
        return this.email;
    }
    public void setEmail(String email){
        this.email = email;
        notifyPropertyChanged(BR.email);
    }
    @Bindable
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
        notifyPropertyChanged(BR.description);
    }
    @Bindable
    public boolean getPending() {
        return this.pending;
    }
    public void setPending(boolean pending) {
        this.pending = pending;
        notifyPropertyChanged(BR.pending);
    }
    public ObservableArrayList<Post> getPosts(){
        return this.posts;
    }
    public void setPosts(ObservableArrayList<Post> posts){
        this.posts = posts;
    }
    public void setPosts(ArrayList<Post> posts){
        this.posts.addAll(posts);
    }
    public void setPosts(List<Post> posts){
        this.posts.addAll(posts);
    }
    public void addPost(Post post){
        this.posts.add(post);
    }
    public void deletePost(Post p) {
        this.posts.remove(p);
    }
    public void removePost(Post post) {
        this.posts.remove(post);
    }
    public ObservableArrayList<Post> getFavouritePosts() {
        return this.favouritePosts;
    }
    public void setFavouritePosts(ObservableArrayList<Post> favouritePosts) {
        this.favouritePosts = favouritePosts;
    }
    public void addFavouritePost(Post post) {
        this.favouritePosts.add(post);
    }
    public void removeFavouritePost(Post post) {
        this.favouritePosts.remove(post);
    }
    public ObservableArrayList<User> getFriends() {
        return this.friends;
    }
    public void addFriend(User user){
        this.friends.add(user);
    }
    public void removeFriend(User user){
        this.friends.remove(user);
    }
    public Boolean isUsersFavouritePost(Post post) {
        return favouritePosts.contains(post);
    }

    @TypeConverter
    public static int fromBoolean(boolean v) {
        return v ? 1 : 0;

    }
    @TypeConverter
    public static boolean toBoolean(int v) {
        return v == 1;
    }
}
