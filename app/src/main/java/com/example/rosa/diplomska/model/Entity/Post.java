package com.example.rosa.diplomska.model.Entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

import java.sql.Timestamp;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "post",
        indices = {
            @Index(value = "postId", unique = true),
            @Index("posterId"),
            @Index(value = "posterUsername"),
            @Index(value = "timestamp")
        }/*,
        foreignKeys = {
            @ForeignKey(onDelete = CASCADE, onUpdate = CASCADE, entity = User.class,
                parentColumns = "userId", childColumns = "posterId"),
            @ForeignKey(onDelete = CASCADE, onUpdate = CASCADE, entity = User.class,
                parentColumns = "username", childColumns = "posterUsername")}*/)
public class Post extends BaseObservable {
    @PrimaryKey
    private int postId;
    private int posterId;
    private String posterUsername;
    private String content;
    private Timestamp timestamp;
    private int favouriteCounter;

    private boolean isUserFav = false;

    public Post(int postId, int posterId, String posterUsername, String content, Timestamp timestamp, int favouriteCounter, boolean isUserFav) {
        this.postId = postId;
        this.posterId = posterId;
        this.posterUsername = posterUsername;
        this.content = content;
        this.timestamp = timestamp;
        this.favouriteCounter = favouriteCounter;
        this.isUserFav = isUserFav;
    }

    @Ignore
    public Post(int postId, int posterId, String content, Timestamp timestamp, int favouriteCounter) {
        this.postId = postId;
        this.posterId = posterId;
        this.content = content;
        this.timestamp = timestamp;
        this.favouriteCounter = favouriteCounter;
    }

    @Ignore
    public Post(int postId, int posterId, String posterUsername, String content, Timestamp timestamp, int favouriteCounter) {
        this.postId = postId;
        this.posterId = posterId;
        this.posterUsername = posterUsername;
        this.content = content;
        this.timestamp = timestamp;
        this.favouriteCounter = favouriteCounter;
    }

    @Ignore
    public Post(String username, String content) {
        this.posterUsername = username;
        this.content = content;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    @Ignore
    public Post(String username, String content, Timestamp timestamp) {
        this.posterUsername = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    @Bindable
    public int getPostId() {
      return this.postId;
    }
    public void setPostId(int postId) {
        this.postId = postId;
        notifyPropertyChanged(BR.postId);
    }

    @Bindable
    public int getPosterId(){
        return this.posterId;
    }
    public void setPosterId(int posterId) {
        this.posterId = posterId;
        notifyPropertyChanged(BR.posterId);
    }

    @Bindable
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
        notifyPropertyChanged(BR.content);
    }
    @Bindable
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        notifyPropertyChanged(BR.timestamp);
    }
    @Bindable
    public int getFavouriteCounter() {
        return this.favouriteCounter;
    }
    public void setFavouriteCounter(int counter) {
        this.favouriteCounter = counter;
        notifyPropertyChanged(BR.favouriteCounter);
    }
    @Bindable
    public String getPosterUsername() {
        return this.posterUsername;
    }
    public void setPosterUsername(String username) {
        this.posterUsername = username;
        notifyPropertyChanged(BR.posterUsername);
    }

    public boolean getIsUserFav() {
        return this.isUserFav;
    }
    public void setIsUserFav(boolean isFav) {
        this.isUserFav = isFav;
    }
}
