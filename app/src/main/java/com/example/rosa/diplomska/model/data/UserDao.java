package com.example.rosa.diplomska.model.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.databinding.ObservableArrayList;

import com.example.rosa.diplomska.model.Entity.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user WHERE pending=0")
    List<User> getUserFriends();
    @Query("SELECT * FROM user WHERE pending=1")
    List<User> getUserPendingFriends();
    @Query("DELETE FROM user WHERE userId=:friendId")
    void deleteFriendById(int friendId);
    @Query("DELETE from user WHERE userId=:friendId AND pending=1")
    void declineFriendRequestById(int friendId);
    @Query("UPDATE user SET pending=1 WHERE userId=:friendId")
    void acceptFriendRequestById(int friendId);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User friend);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ObservableArrayList<User> friends);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<User> friends);
    @Delete
    void delete(User friend);


/*    @Query("SELECT user_id FROM user WHERE username = :username LIMIT 1")
    int getUserIdByUsername(String username);

    @Query("SELECT username FROM user WHERE user_id=:userId LIMIT 1")
    String getUsernameById(int userId);

    @Query("SELECT * FROM user WHERE user_id = :userId LIMIT 1")
    User getUserById(int userId);

    @Query("SELECT * FROM user WHERE username=:username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM user WHERE username=:username AND password=:password LIMIT 1")
    User getUserByUsernamePassword(String username, String password);

    @Query("UPDATE user SET description=:description WHERE username=:username")
    void updateUserDescription(String username, String description);

    @Query("SELECT user_id FROM user WHERE username = :username LIMIT 1")
    int checkIfUsernameTaken(String username);

    @Query("SELECT user_id FROM user WHERE email = :email LIMIT 1")
    int checkIfEmailTaken(String email);

    @Query("UPDATE user SET username=:newUsername WHERE username=:oldUsername")
    void updateUsername(String oldUsername, String newUsername);

    @Query("UPDATE user SET email=:newEmail WHERE email=:oldEmail")
    void updateEmail(String oldEmail, String newEmail);

    @Query("INSERT INTO user (username, email, description) VALUES (:username, :email, :description)")
    void insertUser(String username, String email, String description);

    //friends
    @Query("SELECT user_one_id, user_two_id FROM FriendEntity WHERE (user_one_id=:userId OR user_two_id=:userId) AND status=1")
    ObservableArrayList<User> getUserFriends(int userId);
    @Query("SELECT user_one_id, user_two_id FROM FriendEntity WHERE (user_one_id=:userId OR user_two_id=:userId) AND status=0")
    ObservableArrayList<User> getPendingFriendships(int userId);
    @Query("INSERT INTO FriendEntity (user_one_id, user_two_id, status, action_user_id) VALUES (:userOneId, :userTwoId, 0, :actionUserId)")
    void sendFriendRequest(int userOneId, int userTwoId, int actionUserId);
    @Query("DELETE FROM FriendEntity WHERE (user_one_id=:userOneId AND user_two_id=:userTwoId AND status=1)")
    void removeFriend(int userOneId, int userTwoId);
    @Query("DELETE from FriendEntity WHERE (user_one_id=:userOneId AND user_two_id=:userTwoId AND status=0)")
    void declineFriendRequest(int userOneId, int userTwoId);
    @Query("UPDATE FriendEntity SET status=:status WHERE user_one_id=:userOneId AND user_two_id=:userTwoId and status=0")
    void confirmFriendshipRequest(int status, int userOneId, int userTwoId);*/
}
