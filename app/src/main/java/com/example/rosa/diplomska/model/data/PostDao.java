package com.example.rosa.diplomska.model.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.databinding.ObservableArrayList;

import com.example.rosa.diplomska.model.Entity.Post;

import java.util.ArrayList;
import java.util.List;
@Dao
public interface PostDao {
    @Query("SELECT * FROM post WHERE posterId=:posterId")
    List<Post> getUserPostsById(int posterId);
    @Query("SELECT * FROM post WHERE posterId!=:userId")
    List<Post> getHomePosts(int userId);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Post post);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ObservableArrayList<Post> posts);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<Post> posts);
    @Delete
    void delete(Post post);
    @Delete
    void delete(ObservableArrayList<Post> posts);


////post_id, poster_id, content, timestamp, favourite_counter
//    @Query("SELECT * FROM PostsEntity WHERE post_id=:postId LIMIT 1")
//    Post getPostById(int postId);
//
//    @Query("INSERT INTO PostsEntity (poster_id, content, timestamp) VALUES (:userId, :content, :timestamp)")
//    void addPost(int userId, String content, Timestamp timestamp);

//    @Query("DELETE FROM PostsEntity WHERE post_id=:postId")
//    void removePost(int postId);

//    @Query("UPDATE PostsEntity SET favourite_counter = favourite_counter + 1 WHERE post_id=:postId")
//    void incrementFavouriteCounter(int postId);

//    @Query("SELECT COUNT (post_id) FROM favourite WHERE post_id=:postId")
//    int countFavourites(int postId);

//    @Query("SELECT * FROM PostsEntity WHERE user_id=:userId ORDER BY timestamp DESC LIMIT 20 OFFSET :offset")
//    ObservableArrayList<Post> getNextTwentyUserPosts(int userId, int offset);

//    @Query("SELECT * FROM posts WHERE post_id IN (SELECT post_id FROM favourite WHERE user_id=:userID) ORDER BY timestamp DESC LIMIT 20 OFFSET :offset")
//    ObservableArrayList<Post> getUsersFavPostsByUserId(int userId, int offset);

//    @Query("INSERT INTO favourite (user_id, post_id, timestamp) VALUES (:userId, :postId, :timestamp)")
//    void favouritePost(int userId, int postId, Timestamp timestamp);

//    @Query("DELETE FROM favourite WHERE user_id=:userId AND post_id=:postId")
//    void removeFavouritePost(int userId, int postId);*/
}
