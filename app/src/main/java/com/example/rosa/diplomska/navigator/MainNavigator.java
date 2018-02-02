package com.example.rosa.diplomska.navigator;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.view.MenuItem;

import com.example.rosa.diplomska.model.Entity.Post;
import com.example.rosa.diplomska.model.Entity.User;
import com.example.rosa.diplomska.view.activity.MainActivity;
import com.example.rosa.diplomska.view.fragment.AddFriendDialogFragment;
import com.example.rosa.diplomska.view.fragment.FriendsFragment;
import com.example.rosa.diplomska.view.fragment.HomeFragment;
import com.example.rosa.diplomska.view.fragment.ProfileFragment;

public interface MainNavigator {
    void setUpMainActivity();
    void startLoginActivity();
    boolean onNavItemSelected(MenuItem item);
    void mnAlertDialog(String title, String text);
    Context getMNContext();
    MainActivity getMainActivity();
    User getUser();
    HomeFragment getHomeFragment();
    FriendsFragment getFriendsFragment();
    void clickedAddFriend();
    void disableFindFriendInterface();
    void enableFindFriendInterface();
    AddFriendDialogFragment getAddFriendDialogFragment();
    ProfileFragment getProfileFragment();
    void saveProfileChanges();
    void beginEditMode();
    void endEditMode();
    void setHomePosts(ObservableArrayList<Post> post);
    ObservableArrayList<Post> getHomePosts();

}

/*
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableArrayList;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.model.Entity.Post;
import com.example.rosa.diplomska.model.Entity.User;
import com.example.rosa.diplomska.databinding.ActivityMainBinding;
import com.example.rosa.diplomska.view.fragment.AboutFragment;
import com.example.rosa.diplomska.view.fragment.AddFriendDialogFragment;
import com.example.rosa.diplomska.view.fragment.FriendsFragment;
import com.example.rosa.diplomska.view.fragment.HomeFragment;
import com.example.rosa.diplomska.view.activity.LoginActivity;
import com.example.rosa.diplomska.view.activity.MainActivity;
import com.example.rosa.diplomska.view.fragment.ProfileFragment;
import com.example.rosa.diplomska.view.fragment.SettingsFragment;
import com.example.rosa.diplomska.viewModel.FriendsViewModel;
import com.example.rosa.diplomska.viewModel.HomeViewModel;
import com.example.rosa.diplomska.volley.AppSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class MainNavigator implements NavigationView.OnNavigationItemSelectedListener, FriendsViewModel.FriendListener, HomeViewModel.HomePostsListener {

    private final static String SETTINGS_FRAGMENT_TAG = "SettingsFragment";
    private final static String PROFILE_FRAGMENT_TAG = "ProfileFragment";
    private final static String HOME_FRAGMENT_TAG = "HomeFragment";
    private final static String ABOUT_FRAGMENT_TAG = "AboutFragment";
    private final static String FRIENDS_FRAGMENT_TAG = "FriendsFragment";

    AddFriendDialogFragment df;
    private MainActivity mainActivity;
    private FragmentManager fm;
    ActivityMainBinding binding;
    DrawerLayout drawerLayout;

    public MainNavigator(MainActivity mainActivity, FragmentManager fm, ActivityMainBinding binding, DrawerLayout drawer) {
        this.mainActivity = mainActivity;
        this.fm = fm;
        this.binding = binding;
        this.drawerLayout = drawer;
    }

    public void setUpMainActivity() {
        FragmentTransaction ft = fm.beginTransaction();
        HomeFragment hf = (HomeFragment) fm.findFragmentByTag(HOME_FRAGMENT_TAG);
        hf = (hf != null) ? hf : new HomeFragment();
        hf.setHomePostsListener(this);
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right);
        ft.add(R.id.container_main,hf,HOME_FRAGMENT_TAG);
        mainActivity.getSupportActionBar().setTitle(R.string.menu_home);
        ft.commit();
    }

    public void startLoginActivity() {
        Intent myIntent = new Intent(mainActivity, LoginActivity.class);
        mainActivity.startActivity(myIntent);
        mainActivity.finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //naslednja koda menja fragmente glede na klik v nav view.
        HomeFragment hf;
        ProfileFragment pf;
        AboutFragment af;
        SettingsFragment sf;
        FriendsFragment ff;

        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right);
        switch (menuItem.getItemId()) {
            case R.id.menuHome:
                if (binding.containerMain != null) {
                    mainActivity.getSupportActionBar().setTitle(R.string.menu_home);
                    hf = (HomeFragment) fm.findFragmentByTag(HOME_FRAGMENT_TAG);
                    hf = (hf != null) ? hf : new HomeFragment();
                    hf.setHomePostsListener(this);
                    ft.replace(R.id.container_main, hf, HOME_FRAGMENT_TAG);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.menuProfile:
                if (binding.containerMain != null) {
                    mainActivity.getSupportActionBar().setTitle(R.string.menu_profile);
                    pf = (ProfileFragment) fm.findFragmentByTag(PROFILE_FRAGMENT_TAG);
                    pf = (pf != null) ? pf : new ProfileFragment();
                    ft.replace(R.id.container_main, pf,PROFILE_FRAGMENT_TAG);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.menuFriends:
                if (binding.containerMain != null) {
                    mainActivity.getSupportActionBar().setTitle(R.string.menu_friends);
                    ff = (FriendsFragment) fm.findFragmentByTag(FRIENDS_FRAGMENT_TAG);
                    ff = (ff != null) ? ff : new FriendsFragment();
                    ff.setFriendListener(this);
                    ft.replace(R.id.container_main, ff, FRIENDS_FRAGMENT_TAG);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.menuSettings:
                if (binding.containerMain != null) {
                    mainActivity.getSupportActionBar().setTitle(R.string.menu_settings);
                    sf = (SettingsFragment) fm.findFragmentByTag(SETTINGS_FRAGMENT_TAG);
                    sf = (sf != null) ? sf : new SettingsFragment();
                    ft.replace(R.id.container_main, sf, SETTINGS_FRAGMENT_TAG);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.menuAbout:
                if (binding.containerMain != null) {
                    mainActivity.getSupportActionBar().setTitle(R.string.menu_about);
                    af = (AboutFragment) fm.findFragmentByTag(ABOUT_FRAGMENT_TAG);
                    af = (af != null) ? af : new AboutFragment();
                    ft.replace(R.id.container_main, af, ABOUT_FRAGMENT_TAG);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.menuLogout:
                SharedPreferences pref = mainActivity.getApplicationContext().getSharedPreferences("myPref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("login",false);
                editor.apply();

                startLoginActivity();
            default:
                break;
        }
        menuItem.setChecked(true);
        if (drawerLayout != null)
            drawerLayout.closeDrawers();
        return true;
    }

    @Override
    public void loadUserFriends(final ObservableArrayList<User> friends) {
        String user_friends_URL = "http://192.168.1.119/ada_login_api/Source_Files/index.php";
        String user_friends_TAG = "USER_FRIENDS_REQUEST_TAG";

        Map<String, String> params = new HashMap<>();
        params.put("fun","getUserFriends");
        params.put("user_id",Integer.toString(mainActivity.getLogedUser().getUserID()));

        JSONObject jp = new JSONObject(params);

        JsonObjectRequest friendsRequest = new JsonObjectRequest
                (Request.Method.POST, user_friends_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("success") == 0) {
                                mainAlertDialog("Ooops!",response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                JSONArray jFriends = response.getJSONArray("friends");
                                for(int i = 0; i < jFriends.length(); i++) {
                                    JSONObject jFriend = jFriends.getJSONObject(i);
                                    User friend = new User(jFriend.getInt("user_id"),
                                            jFriend.getString("username"),
                                            jFriend.getString("email"),
                                            jFriend.getString("description"));
                                    friends.add(friend);
                                }
                                FriendsFragment ff = (FriendsFragment) fm.findFragmentByTag(FRIENDS_FRAGMENT_TAG);
                                ff = (ff != null) ? ff : new FriendsFragment();
                                ff.notifyFriendsAdapterOfChange();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mainAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        mainAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Context mContext = mainActivity.getApplicationContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(friendsRequest,user_friends_TAG);
    }

    @Override
    public void loadUserPendingFriends(final ObservableArrayList<User> pendingFriends) {
        String user_pending_friends_URL = "http://192.168.1.119/ada_login_api/Source_Files/index.php";
        String user_pending_friends_TAG = "USER_PENDING_FRIENDS_REQUEST_TAG";

        Map<String, String> params = new HashMap<>();
        params.put("fun","getUserPendingFriends");
        params.put("user_id",Integer.toString(mainActivity.getLogedUser().getUserID()));

        JSONObject jp = new JSONObject(params);

        JsonObjectRequest pendingFriendsRequest = new JsonObjectRequest
                (Request.Method.POST, user_pending_friends_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("success") == 0) {
                                mainAlertDialog("Ooops!",response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                JSONArray jFriends = response.getJSONArray("pending_friends");
                                for(int i = 0; i < jFriends.length(); i++) {
                                    JSONObject jFriend = jFriends.getJSONObject(i);
                                    User friend = new User(jFriend.getInt("user_id"),
                                            jFriend.getString("username"),
                                            jFriend.getString("email"),
                                            jFriend.getString("description"));
                                    pendingFriends.add(friend);
                                }
                                FriendsFragment ff = (FriendsFragment) fm.findFragmentByTag(FRIENDS_FRAGMENT_TAG);
                                ff = (ff != null) ? ff : new FriendsFragment();
                                ff.notifyPendingFriendsAdapterOfChange();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mainAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        mainAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Context mContext = mainActivity.getApplicationContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(pendingFriendsRequest,user_pending_friends_TAG);
    }

    @Override
    public void clickedAddFriend() {
        df = AddFriendDialogFragment.newInstance();
        df.setFriendListener(this);
        df.show(fm, "fragment_edit_name");
    }

    @Override
    public void clickedFindFriend(String username) {
        df.setProgressBarVisible();
        df.disableFindButton();
        df.disableSearch();
       // df.dismiss();
       // mainActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    }

    public void mainAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void getHomePosts(final ArrayList<Post> posts) {
        String home_posts_URL = "http://192.168.1.119/ada_login_api/Source_Files/index.php";
        String home_posts_TAG = "HOME_REQUEST_TAG";

        //final ArrayList<Post> posts = new ArrayList<>();
        int uID = mainActivity.getLogedUser().getUserID();
        Map<String, String> params = new HashMap<>();
        params.put("fun","getHomePosts");
        params.put("user_id",Integer.toString(mainActivity.getLogedUser().getUserID()));

        JSONObject jp = new JSONObject(params);

        JsonObjectRequest loginRequest = new JsonObjectRequest
                (Request.Method.POST, home_posts_URL, jp, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("success") == 0) {
                                mainAlertDialog("Ooops!",response.getString("message")); //Toast.makeText(loginActivity.getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                JSONArray jPosts = response.getJSONArray("home_posts");
                                for(int i = 0; i < jPosts.length(); i++) {
                                    JSONObject jPost = jPosts.getJSONObject(i);
                                    int postId = jPost.getInt("post_id");
                                    int posterId = jPost.getInt("poster_id");
                                    String content = jPost.getString("content");

                                    String ts = jPost.getString("timestamp");
                                    Timestamp timestamp = Timestamp.valueOf(ts);
                                    int favouriteCounter = jPost.getInt("favourite_counter");
                                    String username = jPost.getString("username");
                                    Post post = new Post(postId,posterId,username,content,timestamp,favouriteCounter);
                                    posts.add(post);
                                }
                                HomeFragment hf = (HomeFragment) fm.findFragmentByTag(HOME_FRAGMENT_TAG);
                                hf = (hf != null) ? hf : new HomeFragment();
                                hf.notifyHomeAdapterOfChange();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mainAlertDialog("Ooops!","Error while parsing response."); //Toast.makeText(loginActivity.getApplicationContext(),"Error while parsing response.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        mainAlertDialog("Ooops!","Something went wrong. Please try again later."); //Toast.makeText(loginActivity.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        Context mContext = mainActivity.getApplicationContext();
        AppSingleton appSingleton = AppSingleton.getInstance(mContext);
        //loginRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(loginRequest,home_posts_TAG);
    }

}*/