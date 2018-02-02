package com.example.rosa.diplomska.volley;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.ObservableArrayList;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.rosa.diplomska.model.Entity.Post;
import com.example.rosa.diplomska.model.Entity.User;
import com.example.rosa.diplomska.model.data.AppDatabase;
import com.example.rosa.diplomska.navigator.LoginNavigator;
import com.example.rosa.diplomska.navigator.MainNavigator;
import com.example.rosa.diplomska.view.fragment.FriendsFragment;
import com.example.rosa.diplomska.view.fragment.HomeFragment;
import com.example.rosa.diplomska.view.fragment.ProfileFragment;
import com.example.rosa.diplomska.viewModel.MainActivityViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class DataProvider {
    MainNavigator mainNavigator;
    LoginNavigator loginNavigator;
    private final String serverAddr = "http://192.168.1.119/ada_login_api/Source_Files/index.php";
    public DataProvider() {
    }
    public DataProvider(MainNavigator navigator) {
        mainNavigator = navigator;
    }
    public DataProvider(LoginNavigator navigator) {
        loginNavigator = navigator;
    }

    public void login(String username, String password) {
        loginNavigator.disableLoginInterface();

        String log_in_URL = serverAddr;
        String login_TAG = "LOGIN_REQUEST_TAG";

        final User u = new User();

        Map<String, String> params = new HashMap<>();
        params.put("fun","login");
        params.put("username",username);
        params.put("password",password);
        JSONObject jp = new JSONObject(params);

        JsonObjectRequest loginRequest = new JsonObjectRequest
                (Request.Method.POST, log_in_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loginNavigator.enableLoginInterface();
                        try {
                            if(response.getInt("success") == 0) {
                                loginNavigator.lnAlertDialog("Ooops!", response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                JSONObject jUser = response.getJSONObject("user");
                                int userId = jUser.getInt("user_id");
                                String username = jUser.getString("username");
                                String email = jUser.getString("email");
                                String description = jUser.getString("description");

                                u.setUserID(userId);
                                u.setUsername(username);
                                u.setEmail(email);
                                u.setDescription(description);

                                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(loginNavigator.getLNContext());
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("username",username);
                                editor.putInt("userId",userId);
                                editor.putString("email",email);
                                editor.putString("description",description);
                                editor.putBoolean("login",true);
                                editor.apply();

                                loginNavigator.startMainActivity();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            loginNavigator.lnAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        loginNavigator.lnAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        loginNavigator.enableLoginInterface();
                    }
                });
        Context mContext = loginNavigator.getLNContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(loginRequest,login_TAG);
    }
    public void register(String username, String email, String password ) {
        loginNavigator.disableRegisterInterface();
        String register_URL = serverAddr;
        String register_TAG = "REGISTER_REQUEST_TAG";
        Map<String, String> params = new HashMap<>();
        params.put("fun","register");
        params.put("username",username);
        params.put("email",email);
        params.put("password",password);
        JSONObject jp = new JSONObject(params);

        JsonObjectRequest registerRequest = new JsonObjectRequest
                (Request.Method.POST, register_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loginNavigator.enableRegisterInterface();
                        //loginActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        try {
                            if(response.getInt("success") == 0) {
                                //if(progressBarDialog.isShowing())
                                //    progressBarDialog.hide();
                                loginNavigator.lnAlertDialog("Ooops!",response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                AlertDialog alertDialog = new AlertDialog.Builder(loginNavigator.getLNContext()).create();
                                alertDialog.setTitle("Done!");
                                alertDialog.setMessage(response.getString("message"));
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        //gremo nazaj na login
                                        loginNavigator.getLNFM().popBackStack();
                                    }
                                });

                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            loginNavigator.lnAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loginNavigator.enableRegisterInterface();
                        //loginActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        loginNavigator.lnAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Context mContext = loginNavigator.getLNContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(registerRequest,register_TAG);
    }
    public void getHomePosts() {
        //pogledam če so že v bazi.
        GetHomePostsTask hp = new GetHomePostsTask();
        hp.execute();
    }
    public void getUserPosts() {
        //pogledamo v bazo, če ni podatkov -> volley in jih stlačmo v bazo.
        GetUserPostsTask up = new GetUserPostsTask();
        up.execute();
    }
    public void loadUserFriends() {
        GetUserFriendsTask uf = new GetUserFriendsTask();
        uf.execute();
    }
    public void loadUserPendingFriends() {
        GetUserPendingFriendsTask pf = new GetUserPendingFriendsTask();
        pf.execute();
    }
    public void deleteFriend(final User friend){
        String delete_friend_URL = serverAddr;
        String delete_friend_TAG = "DELETE_FRIEND_TAG";

        //final ObservableArrayList<Post> posts = new ObservableArrayList<>();
        int uID = mainNavigator.getUser().getUserID();
        Map<String, String> params = new HashMap<>();
        params.put("fun","deleteFriend");
        params.put("user_id",Integer.toString(uID));
        params.put("friend_id",Integer.toString(friend.getUserID()));
        params.put("friend_username",friend.getUsername());

        JSONObject jp = new JSONObject(params);

        JsonObjectRequest deleteFriendRequest = new JsonObjectRequest
                (Request.Method.POST, delete_friend_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("success") == 0) {
                                mainNavigator.mnAlertDialog("Ooops!",response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                mainNavigator.getFriendsFragment().addUserToFriends(friend);
                            } else {
                                //navigator.mnAlertDialog("Done!",response.getString("message"));
                                Thread t = new Thread() {
                                    public void run() {
                                        AppDatabase.getInstance(mainNavigator.getMNContext()).getUserDao().delete(friend);
                                    }
                                };
                                t.start();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mainNavigator.mnAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                            mainNavigator.getFriendsFragment().addUserToFriends(friend);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        mainNavigator.mnAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        mainNavigator.getFriendsFragment().addUserToFriends(friend);
                    }
                });
        Context mContext = mainNavigator.getMNContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(deleteFriendRequest,delete_friend_TAG);
    }
    public void loadUserFriendsVolley() {
        String user_friends_URL = serverAddr;
        String user_friends_TAG = "USER_FRIENDS_REQUEST_TAG";

        Map<String, String> params = new HashMap<>();
        params.put("fun","getUserFriends");
        params.put("user_id",Integer.toString(mainNavigator.getUser().getUserID()));

        JSONObject jp = new JSONObject(params);

        final FriendsFragment ff = mainNavigator.getFriendsFragment();

        JsonObjectRequest friendsRequest = new JsonObjectRequest
                (Request.Method.POST, user_friends_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("success") == 0) {
                                mainNavigator.mnAlertDialog("Ooops!",response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                JSONArray jFriends = response.getJSONArray("friends");
                                ObservableArrayList<User> u = new ObservableArrayList<>();
                                for(int i = 0; i < jFriends.length(); i++) {
                                    JSONObject jFriend = jFriends.getJSONObject(i);
                                    User friend = new User(jFriend.getInt("user_id"),
                                            jFriend.getString("username"),
                                            jFriend.getString("email"),
                                            jFriend.getString("description"),false);
                                    u.add(friend);
                                }
                                ff.setFriends(u);
                                //ff.notifyFriendsAdapterOfChange();
                                InsertUserFriendsLocal insertFriendsTask = new InsertUserFriendsLocal();
                                insertFriendsTask.execute(u);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mainNavigator.mnAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        mainNavigator.mnAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Context mContext = mainNavigator.getMNContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(friendsRequest,user_friends_TAG);
    }
    public void sendFriendRequest(String newFriendUsername) {
        mainNavigator.disableFindFriendInterface();

        String send_friend_request_URL = serverAddr;
        String send_friend_request_TAG = "SEND_FRIEND_REQUEST_TAG";

        Map<String, String> params = new HashMap<>();
        params.put("fun","sendFriendRequest");
        params.put("sender_id",Integer.toString(mainNavigator.getUser().getUserID()));
        params.put("receiver_username",newFriendUsername);

        JSONObject jp = new JSONObject(params);

        JsonObjectRequest sendFriendRequest = new JsonObjectRequest
                (Request.Method.POST, send_friend_request_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mainNavigator.enableFindFriendInterface();
                        try {
                            if(response.getInt("success") == 0) {
                                mainNavigator.mnAlertDialog("Ooops!",response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                mainNavigator.mnAlertDialog("Done!",response.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mainNavigator.mnAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        mainNavigator.enableFindFriendInterface();
                        mainNavigator.mnAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Context mContext = mainNavigator.getMNContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(sendFriendRequest,send_friend_request_TAG);
    }
    public void acceptFriendRequest(final User friend) {
        String accept_friend_URL = serverAddr;
        String accept_friend_TAG = "ACCEPT_FRIEND_TAG";

        //final ObservableArrayList<Post> posts = new ObservableArrayList<>();
        int uID = mainNavigator.getUser().getUserID();
        Map<String, String> params = new HashMap<>();
        params.put("fun","acceptFriendRequest");
        params.put("user_id",Integer.toString(uID));
        params.put("friend_id",Integer.toString(friend.getUserID()));
        params.put("friend_username",friend.getUsername());

        JSONObject jp = new JSONObject(params);

        JsonObjectRequest acceptFriendRequest = new JsonObjectRequest
                (Request.Method.POST, accept_friend_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("success") == 0) {
                                mainNavigator.mnAlertDialog("Ooops!",response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                mainNavigator.getFriendsFragment().removeUserFromFriends(friend);
                                mainNavigator.getFriendsFragment().addUserToPendingFriends(friend);
                            } else {
                                //navigator.mnAlertDialog("Done!",response.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mainNavigator.mnAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                            mainNavigator.getFriendsFragment().removeUserFromFriends(friend);
                            mainNavigator.getFriendsFragment().addUserToPendingFriends(friend);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        mainNavigator.mnAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        mainNavigator.getFriendsFragment().removeUserFromFriends(friend);
                        mainNavigator.getFriendsFragment().addUserToPendingFriends(friend);
                    }
                });
        Context mContext = mainNavigator.getMNContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(acceptFriendRequest,accept_friend_TAG);
    }
    public void declineFriendRequest(final User friend){
        String decline_friend_URL = serverAddr;
        String decline_friend_TAG = "DECLINE_FRIEND_TAG";

        //final ObservableArrayList<Post> posts = new ObservableArrayList<>();
        int uID = mainNavigator.getUser().getUserID();
        Map<String, String> params = new HashMap<>();
        params.put("fun","declineFriendRequest");
        params.put("user_id",Integer.toString(uID));
        params.put("friend_id",Integer.toString(friend.getUserID()));
        params.put("friend_username",friend.getUsername());

        JSONObject jp = new JSONObject(params);

        JsonObjectRequest declineFriendRequestRequest = new JsonObjectRequest
                (Request.Method.POST, decline_friend_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("success") == 0) {
                                mainNavigator.mnAlertDialog("Ooops!",response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                mainNavigator.getFriendsFragment().addUserToPendingFriends(friend);
                            } else {
                               // navigator.mnAlertDialog("Done!",response.getString("message"))
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mainNavigator.mnAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                            mainNavigator.getFriendsFragment().addUserToPendingFriends(friend);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        mainNavigator.mnAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        mainNavigator.getFriendsFragment().addUserToPendingFriends(friend);
                    }
                });
        Context mContext = mainNavigator.getMNContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(declineFriendRequestRequest,decline_friend_TAG);
    }
    public void loadUserPendingFriendsVolley() {
        String user_pending_friends_URL = serverAddr;
        String user_pending_friends_TAG = "USER_PENDING_FRIENDS_REQUEST_TAG";

        Map<String, String> params = new HashMap<>();
        params.put("fun","getUserPendingFriends");
        params.put("user_id",Integer.toString(mainNavigator.getUser().getUserID()));

        JSONObject jp = new JSONObject(params);

        final FriendsFragment ff = mainNavigator.getFriendsFragment();

        JsonObjectRequest pendingFriendsRequest = new JsonObjectRequest
                (Request.Method.POST, user_pending_friends_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("success") == 0) {
                                mainNavigator.mnAlertDialog("Ooops!",response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                JSONArray jFriends = response.getJSONArray("pending_friends");
                                ObservableArrayList<User> pf = new ObservableArrayList<>();
                                for(int i = 0; i < jFriends.length(); i++) {
                                    JSONObject jFriend = jFriends.getJSONObject(i);
                                    User friend = new User(jFriend.getInt("user_id"),
                                            jFriend.getString("username"),
                                            jFriend.getString("email"),
                                            jFriend.getString("description"),true);
                                    pf.add(friend);
                                }
                                ff.setPendingFriends(pf);
                                InsertUserFriendsLocal upf = new InsertUserFriendsLocal();
                                upf.execute(pf);
                                //ff.notifyPendingFriendsAdapterOfChange();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mainNavigator.mnAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        mainNavigator.mnAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Context mContext = mainNavigator.getMNContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(pendingFriendsRequest,user_pending_friends_TAG);
    }


    public void getHomePostsVolley() {
        final HomeFragment hf = mainNavigator.getHomeFragment();
        hf.setHomeProgressBarVisible();

        String home_posts_URL = serverAddr;
        String home_posts_TAG = "HOME_REQUEST_TAG";

        //final ObservableArrayList<Post> posts = new ObservableArrayList<>();
        int uID = mainNavigator.getUser().getUserID();
        Map<String, String> params = new HashMap<>();
        params.put("fun","getHomePosts");
        params.put("user_id",Integer.toString(uID));

        JSONObject jp = new JSONObject(params);

        JsonObjectRequest loginRequest = new JsonObjectRequest
                (Request.Method.POST, home_posts_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("success") == 0) {
                                mainNavigator.mnAlertDialog("Ooops!",response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                JSONArray jPosts = response.getJSONArray("home_posts");
                                ArrayList<Post> posts = new ArrayList<>();
                                for(int i = 0; i < jPosts.length(); i++) {
                                    JSONObject jPost = jPosts.getJSONObject(i);
                                    int postId = jPost.getInt("post_id");
                                    int posterId = jPost.getInt("poster_id");
                                    String content = jPost.getString("content");

                                    String ts = jPost.getString("timestamp");
                                    Timestamp timestamp = Timestamp.valueOf(ts);
                                    int favouriteCounter = jPost.getInt("favourite_counter");
                                    String username = jPost.getString("username");
                                    Post post = new Post(postId, posterId, username, content, timestamp, favouriteCounter);
                                    posts.add(post);
                                }
                                hf.setHomePosts(posts);
                                hf.notifyHomeAdapterOfChange();
                                hf.setHomeProgressBarInvisible();
                                InsertHomePostsLocal insertHomePostsTask = new InsertHomePostsLocal();
                                insertHomePostsTask.execute(posts);

                                //hf.notifyHomeAdapterOfChange();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mainNavigator.mnAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        mainNavigator.mnAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Context mContext = mainNavigator.getMNContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(loginRequest,home_posts_TAG);
    }
    public void getUserPostsVolley() {
        String user_posts_URL = serverAddr;
        String user_posts_TAG = "USER_POSTS_TAG";

        int uID = mainNavigator.getUser().getUserID();
        Map<String, String> params = new HashMap<>();
        params.put("fun","getUserPosts");
        params.put("user_id",Integer.toString(uID));

        JSONObject jp = new JSONObject(params);

        final ProfileFragment pf = mainNavigator.getProfileFragment();

        JsonObjectRequest userPostsRequest = new JsonObjectRequest
                (Request.Method.POST, user_posts_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("success") == 0) {
                                mainNavigator.mnAlertDialog("Ooops!",response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                JSONArray jPosts = response.getJSONArray("posts");
                                List<Post> posts = new ArrayList<>();
                                for(int i = 0; i < jPosts.length(); i++) {
                                    JSONObject jPost = jPosts.getJSONObject(i);
                                    int postId = jPost.getInt("post_id");
                                    int posterId = jPost.getInt("poster_id");
                                    String content = jPost.getString("content");

                                    String ts = jPost.getString("timestamp");
                                    Timestamp timestamp = Timestamp.valueOf(ts);
                                    int favouriteCounter = jPost.getInt("favourite_counter");
                                    String username = jPost.getString("username");
                                    Post post = new Post(postId, posterId, username, content, timestamp, favouriteCounter);
                                    posts.add(post);
                                }
                                pf.setProfilePosts(posts);
                                InsertUserPostsLocal insertUserPostsTask = new InsertUserPostsLocal();
                                insertUserPostsTask.execute(posts);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mainNavigator.mnAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        mainNavigator.mnAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Context mContext = mainNavigator.getMNContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(userPostsRequest,user_posts_TAG);
    }

    //pogleda v lokalno bazo ce mamo podatke. če nimamo jih uzame z serverja pa jih pol shran v bazo.
    private class GetHomePostsTask extends AsyncTask<Void,Void,List<Post>>
    {
        @Override
        protected List<Post> doInBackground(Void... voids) {
            List<Post> p = AppDatabase.getInstance(mainNavigator.getMNContext()).getPostDao().getHomePosts(mainNavigator.getUser().getUserID());
            return p;
        }
        @Override
        protected void onPostExecute(List<Post> result) {
            if(!result.isEmpty()) {
                mainNavigator.getHomeFragment().setHomePosts(result);
            } else {
                getHomePostsVolley();
            }
            super.onPostExecute(result);
        }
    }
    private class InsertHomePostsLocal extends AsyncTask<List<Post>,Void,Void>
    {

        @Override
        protected Void doInBackground(List... lists) {
            List<Post> hp = lists[0];
            AppDatabase.getInstance(mainNavigator.getMNContext()).getPostDao().insert(hp);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
    private class GetUserPostsTask extends AsyncTask<Void,Void,List<Post>>
    {
        @Override
        protected List<Post> doInBackground(Void... voids) {
            List<Post> p = AppDatabase.getInstance(mainNavigator.getMNContext()).getPostDao()
                    .getUserPostsById(mainNavigator.getUser().getUserID());
            return p;
        }
        @Override
        protected void onPostExecute(List<Post> result) {
            if(!result.isEmpty()) {
                mainNavigator.getProfileFragment().setProfilePosts(result);
            } else {
                getUserPostsVolley();
            }
            super.onPostExecute(result);
        }
    }
    private class InsertUserPostsLocal extends AsyncTask<List<Post>,Void,Void>
    {
        @Override
        protected Void doInBackground(List... lists) {
            List<Post> hp = lists[0];
            AppDatabase.getInstance(mainNavigator.getMNContext()).getPostDao().insert(hp);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
    private class GetUserFriendsTask extends AsyncTask<Void,Void,List<User>>
    {
        @Override
        protected List<User> doInBackground(Void... voids) {
            List<User> u = AppDatabase.getInstance(mainNavigator.getMNContext()).getUserDao().getUserFriends();
            return u;
        }
        @Override
        protected void onPostExecute(List<User> result) {
            if(!result.isEmpty()) {
                mainNavigator.getFriendsFragment().setFriends(result);
            } else {
                loadUserFriendsVolley();
            }
            super.onPostExecute(result);
        }
    }
    private class InsertUserFriendsLocal extends AsyncTask<List<User>,Void,Void>
    {
        @Override
        protected Void doInBackground(List... lists) {
            List<User> uf = lists[0];
            AppDatabase.getInstance(mainNavigator.getMNContext()).getUserDao().insert(uf);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
    private class GetUserPendingFriendsTask extends AsyncTask<Void,Void,List<User>>
    {
        @Override
        protected List<User> doInBackground(Void... voids) {
            List<User> u = AppDatabase.getInstance(mainNavigator.getMNContext()).getUserDao().getUserPendingFriends();
            return u;
        }
        @Override
        protected void onPostExecute(List<User> result) {
            if(!result.isEmpty()) {
                mainNavigator.getFriendsFragment().setPendingFriends(result);
            } else {
                loadUserPendingFriendsVolley();
            }
            super.onPostExecute(result);
        }
    }
}
