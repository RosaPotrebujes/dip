package com.example.rosa.diplomska.view.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.databinding.HeaderBinding;
import com.example.rosa.diplomska.detector.DetectedActivitiesIntentService;
import com.example.rosa.diplomska.detector.DetectorConstants;
import com.example.rosa.diplomska.detector.LocationDetector;
import com.example.rosa.diplomska.detector.MasterDetector;
import com.example.rosa.diplomska.detector.PeopleDetectorService;
import com.example.rosa.diplomska.detector.SongDetectorService;
import com.example.rosa.diplomska.detector.DetectionReceiver;
import com.example.rosa.diplomska.detector.UserActivity;
import com.example.rosa.diplomska.model.Entity.Post;
import com.example.rosa.diplomska.model.Entity.User;
import com.example.rosa.diplomska.databinding.ActivityMainBinding;
import com.example.rosa.diplomska.navigator.MainNavigator;
import com.example.rosa.diplomska.view.fragment.AboutFragment;
import com.example.rosa.diplomska.view.fragment.AddFriendDialogFragment;
import com.example.rosa.diplomska.view.fragment.FriendsFragment;
import com.example.rosa.diplomska.view.fragment.HomeFragment;
import com.example.rosa.diplomska.view.fragment.ProfileFragment;
import com.example.rosa.diplomska.view.fragment.SettingsFragment;
import com.example.rosa.diplomska.viewModel.MainActivityViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;


public class MainActivity extends AppCompatActivity implements MainNavigator,
        NavigationView.OnNavigationItemSelectedListener, MasterDetector {

    private final static String SETTINGS_FRAGMENT_TAG = "SettingsFragment";
    private final static String PROFILE_FRAGMENT_TAG = "ProfileFragment";
    private final static String HOME_FRAGMENT_TAG = "HomeFragment";
    private final static String ABOUT_FRAGMENT_TAG = "AboutFragment";
    private final static String FRIENDS_FRAGMENT_TAG = "FriendsFragment";

    private ActivityMainBinding binding;
    MainActivityViewModel viewModel;
    MainNavigator navigator;
    MasterDetector masterDetector;
    FragmentManager fm;
    AddFriendDialogFragment df;

    //za alert dialog, da ne zjebe usega orientacija
    AlertDialog mNavAlertDialog;
    String mNavAlertDialogTitle;
    String mNavAlertDialogMessage;
    boolean wasNavDialogDismissed;
    //detect activity
    boolean wasDismissed;
    AlertDialog.Builder alertDialogBuilder = null;
    AlertDialog mAlertDialog;
    String mDialogPost;

    LocationDetector mLocationDetector;

    DetectionReceiver receiverS = new DetectionReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case DetectorConstants.ACTION_SONG_DETECTED:
                    String detectedSong = intent.getStringExtra(DetectorConstants.EXTRA_SONG);
                    //Log.i("SONG DETECTION", "Song detected: " + detectedSong);
                    viewModel.onSongDetected(detectedSong);
                    break;
                default:
                    break;
            }
        }
    };
    DetectionReceiver receiverP = new DetectionReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(DetectorConstants.ACTION_PEOPLE_DETECTED)) {
                int detectedPeople = intent.getIntExtra(DetectorConstants.EXTRA_PEOPLE,0);
                //Log.i("PEOPLE DETECTION","Detected people: " + detectedPeople);
                stopService(intentP);
                viewModel.onPeopleDetected(detectedPeople);
            } else if (intent.getAction().equals(DetectorConstants.ACTION_BT_NOT_ENABLED)) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
            }
        }
    };
    DetectionReceiver receiverL = new DetectionReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case DetectorConstants.ACTION_LOCATION_DETECTED:
                    String detectedLocation = intent.getStringExtra(DetectorConstants.EXTRA_LOCATION);
                    //Log.i("LOCATION DETECTION", "Location detected:" + detectedLocation);
                    viewModel.onLocationDetected(detectedLocation);
                    break;
                default:
                    break;
            }
        }
    };
    DetectionReceiver receiverM = new DetectionReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case DetectorConstants.ACTION_MOTION_DETECTED:
                    String detectedMovement = intent.getStringExtra(DetectorConstants.EXTRA_ACTIVITY);
                    //Log.i("MOVEMENT DETECTION: ","Detected movement:" + detectedMovement);
                    removeActivityUpdatesButtonHandler();
                    viewModel.onMotionDetected(detectedMovement);
                    break;
                default:
                    break;
            }
        }
    };

    IntentFilter filterS;
    IntentFilter filterP;
    IntentFilter filterL;
    IntentFilter filterM;
    Intent intentP;

    private boolean detectionInprogress;
    UserActivity ua;
    //permissions
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_LOCATION = 2;
    private final static int REQUEST_CHECK_SETTINGS = 3;
    private final static int REQUEST_EXTERNAL_STORAGE_AND_RECORD_AUDIO = 4;
    private final static int REQUEST_ALL_PERMISSIONS = 666;

    private BluetoothAdapter mBluetoothAdapter;

    SharedPreferences pref;


    AutomaticActivityDetectionHandler automaticDetectionHandler;
    boolean automaticActivityDetection;
    private boolean timerRunning = false;
    private long timerInterval = 10000; //30 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //change theme
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = pref.getString("appTheme", "1");
        if (theme.equals("1")) {
            setTheme(R.style.Drowner);
        } else if (theme.equals("2")) {
            setTheme(R.style.VineLight);
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        User user = new User(pref.getInt("userId",-1),pref.getString("username",""),pref.getString("email",""),pref.getString("description",""));
        binding.setUser(user);

        navigator = this;
        masterDetector = this;

        fm = getSupportFragmentManager();

        viewModel = new MainActivityViewModel(navigator,masterDetector);
        //viewModel.setUser(user);
        viewModel.getUserPosts();
        viewModel.getUserFriends();
        viewModel.getUserPendingFriends();
        binding.setMainViewModel(viewModel);
        super.onCreate(savedInstanceState);

        setSupportActionBar(binding.myToolbar);
        //myToolbar.setSubtitle("Test Subtitle");
        binding.myToolbar.inflateMenu(R.menu.menu);
        binding.myToolbar.setNavigationIcon(R.drawable.ic_menu);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            Drawable menuIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
            actionBar.setHomeAsUpIndicator(menuIcon);
            actionBar.setDisplayHomeAsUpEnabled(true);
            setSupportActionBar(binding.myToolbar);
        }

        binding.navigationView.setNavigationItemSelectedListener(this);

        //HeaderBinding navBinding = HeaderBinding.inflate(LayoutInflater.from(binding.navigationView.getContext()));
        //navBinding.setUser(user);
        //navBinding.navHeaderUsername.setText("usernaimu");
        //navBinding.navHeaderEmail.setText(user.getEmail());

        HeaderBinding b = DataBindingUtil.inflate(getLayoutInflater(), R.layout.header, binding.navigationView, false);
        binding.navigationView.addHeaderView(b.getRoot());
        b.setUser(user);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        if (findViewById(R.id.container_main) != null) {
            if(savedInstanceState == null) {
                navigator.setUpMainActivity();
                wasDismissed = true;
                wasNavDialogDismissed = true;
                mDialogPost = "";
                detectionInprogress = false;
            } else {
                detectionInprogress = savedInstanceState.getBoolean("detectionInProgress");
                if(detectionInprogress) {
                    ua = new UserActivity();
                    ua.setGotMovement(savedInstanceState.getBoolean("gotMotion"));
                    ua.setGotMusic(savedInstanceState.getBoolean("gotMusic"));
                    ua.setGotLocation(savedInstanceState.getBoolean("gotLocation"));
                    ua.setGotPeople(savedInstanceState.getBoolean("gotBlueTooth"));
                    ua.setUaUsername(savedInstanceState.getString("uaUsername"));

                    ua.setOnActivityDoneListener(viewModel);
                    viewModel.setUserActivity(ua);

                    if(ua.getGotMovement()) {
                        viewModel.onMotionDetected(savedInstanceState.getString("uaMotion"));
                    }
                    if(ua.getGotMusic()) {
                        viewModel.onSongDetected(savedInstanceState.getString("uaMusic"));
                    }
                    if(ua.getGotPeople()) {
                        viewModel.onPeopleDetected(savedInstanceState.getInt("uaPeople"));
                    }
                    if(ua.getGotLocation()) {
                        viewModel.onLocationDetected(savedInstanceState.getString("uaLocation"));
                    }
                }
                wasDismissed = savedInstanceState.getBoolean("wasDismissed");
                mDialogPost = savedInstanceState.getString("dialogPost");
                if(!wasDismissed){
                    activityDetectedDialog("Activity detected",mDialogPost);
                }
                wasNavDialogDismissed = savedInstanceState.getBoolean("wasNavDialogDismissed");
                if(!wasNavDialogDismissed) {
                    mNavAlertDialogTitle = savedInstanceState.getString("mNavAlertDialogTitle");
                    mNavAlertDialogMessage = savedInstanceState.getString("mNavAlertDialogMessage");
                    mnAlertDialog(mNavAlertDialogTitle,mNavAlertDialogMessage);
                }

                Fragment f = getSupportFragmentManager().findFragmentById(R.id.container_main);
                if (f instanceof HomeFragment)
                    getSupportActionBar().setTitle(R.string.menu_home);
                if (f instanceof AboutFragment)
                    getSupportActionBar().setTitle(R.string.menu_about);
                if (f instanceof ProfileFragment)
                    getSupportActionBar().setTitle(R.string.menu_profile);
                if (f instanceof SettingsFragment)
                    getSupportActionBar().setTitle(R.string.menu_settings);
            }
        }

        timerStart();


        //filtri za detection
        filterL = new IntentFilter();
        filterL.addAction(DetectorConstants.ACTION_LOCATION_DETECTED);
        filterS = new IntentFilter();
        filterS.addAction(DetectorConstants.ACTION_SONG_DETECTED);
        filterM = new IntentFilter();
        filterM.addAction(DetectorConstants.ACTION_MOTION_DETECTED);
        filterP = new IntentFilter();
        filterP.addAction(DetectorConstants.ACTION_PEOPLE_DETECTED);
        filterP.addAction(DetectorConstants.ACTION_BT_NOT_ENABLED);
    }

    public void startAutomaticActivityDetectionTimer() {
        /*boolean automaticDetection = true; //pref.getBoolean("automaticDetection",false);
        if(automaticDetection) {
            Boolean timerInProgress = savedInstanceState.getBoolean("detectionInProgress");
            automaticDetectionHandler = new AutomaticActivityDetectionHandler();
            automaticDetectionHandler.ma = this;
            automaticDetectionHandler.startTimer();
        }*/

        automaticDetectionHandler = new AutomaticActivityDetectionHandler();
        automaticDetectionHandler.ma = this;
        automaticDetectionHandler.startTimer();
        timerRunning = true;


/*        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean timerRunning = pref.getBoolean("timerRunning",false);
        Long timeElapsed = pref.getLong("timerTimeElapsed",0);

        if(timerRunning) {
            automaticDetectionHandler.resumeTimer(timeElapsed);
        } else {
            automaticDetectionHandler.startTimer();
        }*/
    }
    public void pauseAutomaticActivityDetectionTimer() {
        if(automaticDetectionHandler.isRunning) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("timerRunning",true);
            editor.putLong("timerTimeElapsed",automaticDetectionHandler.getElapsedTime());
            editor.apply();
            automaticDetectionHandler.stopTimer();
        }
    }
    public void stopAutomaticActivityDetectionTimer() {
        /*SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("timerRunning",false);
        editor.putLong("timerTimeElapsed",automaticDetectionHandler.getElapsedTime());
        editor.apply();*/
        automaticDetectionHandler.stopTimer();
    }

    public void timerStart() {
        if(!timerRunning) {
            long delay;

            if(pref == null)
                pref = PreferenceManager.getDefaultSharedPreferences(this);

            boolean resumeTimer = pref.getBoolean("resumeTimer",false);

            if(resumeTimer) {
                delay = timerInterval - pref.getLong("timeElapsed",0);
                Log.i("TIMER","resume");
                Log.i("TIMER","Time left:"+delay/1000+" Time elapsed:" + pref.getLong("timeElapsed",0)/1000);
                //pobrišemo
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("resumeTimer",false);
                editor.putLong("timerTimeElapsed",0);
                editor.apply();
                Log.i("TIMER","resume");
            } else {
                delay = timerInterval;
                Log.i("TIMER","START");
            }
            automaticDetectionHandler = new AutomaticActivityDetectionHandler();
            automaticDetectionHandler.ma = this;
            automaticDetectionHandler.TIME_TO_WAIT = delay;
            automaticDetectionHandler.startTimer();

            timerRunning = true;
        }
    }

    public void timerStop() {
        if(timerRunning) {
            if(pref == null)
                pref = PreferenceManager.getDefaultSharedPreferences(this);

            long timeElapsed = automaticDetectionHandler.getElapsedTime();
            automaticDetectionHandler.stopTimer();
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("resumeTimer",true);
            editor.putLong("timerTimeElapsed",timeElapsed);
            editor.apply();

            timerRunning = false;

            Log.i("TIMER","STOP");
        }
    }

    @Override
    public ProfileFragment getProfileFragment() {
        ProfileFragment pf = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT_TAG);
        pf = (pf != null) ? pf : new ProfileFragment();
        return pf;
    }
    @Override
    public void saveProfileChanges() {
        //TODO: saveProfileChanges
    }
    @Override
    public void beginEditMode() {
        getProfileFragment().editModeOn();
    }
    @Override
    public void endEditMode() {
        saveProfileChanges();
        getProfileFragment().editModeOff();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                binding.drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        //pauseAutomaticActivityDetectionTimer();
        timerStop();
    }
    @Override
    public void onPause() {
        if(mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
        unregisterDetectionReceivers();
        //pauseAutomaticActivityDetectionTimer();
        timerStop();
        super.onPause();
    }
    @Override
    public void onResume() {
        registerDetectionReceivers();
        //startAutomaticActivityDetectionTimer();
        timerStart();
        super.onResume();
    }
    @Override
    public void setUpMainActivity() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        HomeFragment hf = (HomeFragment) fm.findFragmentByTag(HOME_FRAGMENT_TAG);
        hf = (hf != null) ? hf : new HomeFragment();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right);
        ft.add(R.id.container_main,hf,HOME_FRAGMENT_TAG);
        getSupportActionBar().setTitle(R.string.menu_home);
        ft.commit();
    }
    @Override
    public void startLoginActivity() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Username","");
        editor.putInt("userId",-1);
        editor.putString("email","");
        editor.putString("description","");
        editor.putBoolean("login",false);
        editor.apply();

        Intent myIntent = new Intent(this, LoginActivity.class);
        startActivity(myIntent);
        finish();
    }
    @Override
    public Context getMNContext() {
        return this;
    }
    @Override
    public MainActivity getMainActivity() {
        return this;
    }
    @Override
    public boolean onNavItemSelected(MenuItem menuItem) {
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
                    getSupportActionBar().setTitle(R.string.menu_home);
                    hf = (HomeFragment) fm.findFragmentByTag(HOME_FRAGMENT_TAG);
                    hf = (hf != null) ? hf : new HomeFragment();
                    ft.replace(R.id.container_main, hf, HOME_FRAGMENT_TAG);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.menuProfile:
                if (binding.containerMain != null) {
                    getSupportActionBar().setTitle(R.string.menu_profile);
                    pf = (ProfileFragment) fm.findFragmentByTag(PROFILE_FRAGMENT_TAG);
                    pf = (pf != null) ? pf : new ProfileFragment();
                    ft.replace(R.id.container_main, pf,PROFILE_FRAGMENT_TAG);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.menuFriends:
                if (binding.containerMain != null) {
                    getSupportActionBar().setTitle(R.string.menu_friends);
                    ff = (FriendsFragment) fm.findFragmentByTag(FRIENDS_FRAGMENT_TAG);
                    ff = (ff != null) ? ff : new FriendsFragment();
                    ft.replace(R.id.container_main, ff, FRIENDS_FRAGMENT_TAG);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.menuSettings:
                if (binding.containerMain != null) {
                    getSupportActionBar().setTitle(R.string.menu_settings);
                    sf = (SettingsFragment) fm.findFragmentByTag(SETTINGS_FRAGMENT_TAG);
                    sf = (sf != null) ? sf : new SettingsFragment();
                    ft.replace(R.id.container_main, sf, SETTINGS_FRAGMENT_TAG);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.menuAbout:
                if (binding.containerMain != null) {
                    getSupportActionBar().setTitle(R.string.menu_about);
                    af = (AboutFragment) fm.findFragmentByTag(ABOUT_FRAGMENT_TAG);
                    af = (af != null) ? af : new AboutFragment();
                    ft.replace(R.id.container_main, af, ABOUT_FRAGMENT_TAG);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                break;
            case R.id.menuLogout:
                startLoginActivity();
            default:
                break;
        }
        menuItem.setChecked(true);
        if (binding.drawerLayout != null)
            binding.drawerLayout.closeDrawers();
        return true;
    }
    @Override
    public void mnAlertDialog(String title, String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);//new AlertDialog.Builder(new ContextThemeWrapper(loginActivity, R.style.Drowner));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mNavAlertDialog = alertDialogBuilder.show();
        mNavAlertDialogTitle = title;
        mNavAlertDialogMessage = message;

    }
    @Override
    public HomeFragment getHomeFragment() {
        HomeFragment hf = (HomeFragment) fm.findFragmentByTag(HOME_FRAGMENT_TAG);
        hf = (hf != null) ? hf : new HomeFragment();
        return hf;
    }
    @Override
    public void setHomePosts(ObservableArrayList<Post> posts) {
        getHomeFragment().setHomePosts(posts);
    }
    @Override
    public ObservableArrayList<Post> getHomePosts(){
        return getHomeFragment().getHomePosts();
    }
    @Override
    public FriendsFragment getFriendsFragment(){
        FriendsFragment ff = (FriendsFragment) getSupportFragmentManager().findFragmentByTag(FRIENDS_FRAGMENT_TAG);
        ff = (ff != null) ? ff : new FriendsFragment();
        return ff;
    }
    @Override
    public AddFriendDialogFragment getAddFriendDialogFragment() {
        if(df == null)
            df = AddFriendDialogFragment.newInstance();
        return df;
    }
    @Override
    public User getUser(){
        if (binding.getUser() != null)
            return binding.getUser();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        User user = new User(pref.getInt("userId",-1),pref.getString("username",""),pref.getString("email",""),pref.getString("description",""));
        return user;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return navigator.onNavItemSelected(item);
    }
    @Override
    public void clickedAddFriend() {
        df = AddFriendDialogFragment.newInstance();
        df.show(fm, "fragment_edit_name");
    }
    @Override
    public void disableFindFriendInterface() {
        if(df != null){
            df.setProgressBarVisible();
            df.disableFindButton();
            df.disableSearch();
        }
    }
    @Override
    public void enableFindFriendInterface() {
        if(df != null) {
            df.setProgressBarInvisible();
            df.enableFindButton();
            df.enableSearch();
        }
    }
    public MainNavigator getNavigator() {
        return this.navigator;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = pref.edit();

        //tuki prestrezem kaj je uporabnik dal za bluetooth
        if (requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == MainActivity.RESULT_OK){
                //je prizgal bt
                editor.putBoolean("blueTooth", true);
                if(detectionInprogress) {
                    //receiverP.register(this,filterP);
                    startPeopleDetection();
                }
            }
            else {//if (resultCode == MainActivity.RESULT_CANCELED) {
                //uporabnik/ca je partypoop
                editor.putBoolean("blueTooth", false);
                if(detectionInprogress)
                    Log.i("BT NOT ENABLED","");
                    viewModel.onPeopleDetected(0);
            }
        }
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if(resultCode == MainActivity.RESULT_OK) {
                editor.putBoolean("location",true);
                mLocationDetector.getLocationResolutionCallback().onResolutionResult(true);
            } else {
                viewModel.onLocationDetected("");
                editor.putBoolean("location",false);
                //mLocationDetector.getLocationResolutionCallback().onResolutionResult(false);
            }
        }
        editor.apply();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean requestAll = false;
        boolean rA = false;
        boolean wES = false;
        if(pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(this);
        }
        SharedPreferences.Editor editor = pref.edit();
        switch (requestCode) {
            case REQUEST_ALL_PERMISSIONS: {
                requestAll = true;

                for(int i = 0; i < permissions.length; i++) {
                    switch (permissions[i]) {
                        case Manifest.permission.ACCESS_FINE_LOCATION:
                            if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                editor.putBoolean("location",true);
                                startLocationDetection();
                            } else {
                                editor.putBoolean("location",false);
                                viewModel.onLocationDetected("");
                            }
                            break;
                        case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                            wES = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                            break;
                        case Manifest.permission.RECORD_AUDIO:
                            rA = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                            break;
                    }
                }
                editor.apply();
            }
            default:
                break;
        }
        if(requestAll) {
            if(!rA || !wES)
                viewModel.onSongDetected("");
            else if (rA && wES) {
                editor.putBoolean("music", true);
                startSongDetection();
            }
        }
    }

    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("detectionInProgress",detectionInprogress);
        if(detectionInprogress) {
            bundle.putBoolean("gotMusic",ua.getGotMusic());
            if(ua.getGotMusic()) {
                bundle.putString("uaMusic",ua.getUaMusic());
            } else {
                bundle.putString("uaMusic","");
            }
            bundle.putBoolean("gotLocation",ua.getGotLocation());
            if(ua.getGotLocation()) {
                bundle.putString("uaLocation",ua.getUaLocation());
            } else {
                bundle.putString("uaLocation","");
            }
            bundle.putBoolean("gotMotion",ua.getGotMovement());
            if(ua.getGotMovement()) {
                bundle.putString("uaMotion",ua.getUaMovement());
            } else {
                bundle.putString("uaMotion","");
            }
            bundle.putBoolean("gotBlueTooth",ua.getGotPeople());
            if(ua.getGotPeople()) {
                bundle.putInt("uaPeople",ua.getUaPeople());
            } else {
                bundle.putInt("uaPeople",0);
            }
            bundle.putString("uaUsername",getUser().getUsername());
        }

        // automatic activity detection
        bundle.putBoolean("timerRunning",automaticDetectionHandler.isRunning);
        bundle.putLong("timerTimeElapsed",automaticDetectionHandler.getElapsedTime());

        bundle.putBoolean("wasDismissed",wasDismissed);
        if(wasDismissed)
            bundle.putString("post","");
        else
            bundle.putString("dialogPost", mDialogPost);
        bundle.putBoolean("wasNavDialogDismissed",wasNavDialogDismissed);
        if(wasNavDialogDismissed) {//za usak slučaj
            bundle.putString("mNavAlertDialogTitle","");
            bundle.putString("mNavAlertDialogMessage","");
        } else {
            bundle.putString("mNavAlertDialogTitle",mNavAlertDialogTitle);
            bundle.putString("mNavAlertDialogMessage",mNavAlertDialogMessage);
        }
    }

    @Override
    public boolean checkIfBTSupported() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    @Override
    public boolean checkIfMusicDetectionSupported() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    @Override
    public boolean checkIfMotionDetectionSupported() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
    }

    @Override
    public boolean checkIfGPSSupported() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }

    @Override
    public boolean checkIfGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void askPermissions() {
        if(pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(this);
        }
        SharedPreferences.Editor editor = pref.edit();
        ArrayList<String> permissionsNeeded = new ArrayList<>();

        if(pref.getBoolean("locationSensor",false)) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                editor.putBoolean("location",true);
            }
        }

        if (pref.getBoolean("musicSensor", false)) {
            boolean wES = false;
            boolean rA = false;
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                wES = true;
            }
            if(ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
            } else {
                rA = true;
            }
            if(wES && rA) {
                editor.putBoolean("music",true);
            }
        }
        editor.apply();
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), REQUEST_ALL_PERMISSIONS);
        }
    }

    @Override
    public void checkSensorSupport() {
        if(pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(this);
        }
        SharedPreferences.Editor editor = pref.edit();
        if(checkIfBTSupported()){
            editor.putBoolean("blueToothSensor", true);
            editor.putBoolean("blueTooth",true);
        }
        if(checkIfGPSSupported() || checkIfGooglePlayServicesAvailable()) {
            editor.putBoolean("locationSensor", true);
            editor.putBoolean("motionSensor", true);
            editor.putBoolean("motion",true);
        }
        //if(checkIfMotionDetectionSupported()) {
        //    editor.putBoolean("motion",true);
       // }
        if(checkIfMusicDetectionSupported()) {
            editor.putBoolean("musicSensor", true);
        }
        editor.apply();
    }

    private ActivityRecognitionClient mActivityRecognitionClient;
    static final long DETECTION_INTERVAL_IN_MILLISECONDS = 5 * 1000; // 5 seconds

    @Override
    public void startDetection() {
        if(detectionInprogress) {
           return;
        }
        detectionInprogress = true;
        //binding.fab.setEnabled(false);
        if(pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(this);
        }
        //TODO: ZAČASNA REŠITEV, DOKLER NE UGOTOVIM ZAKAJ JE CRASHAL - SKLEPAM DA JE ORIENTACIJA
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        //mnAlertDialog("","Detection started.");
        //pref.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        Snackbar snackbar = Snackbar
                .make(binding.coordinatorMain, "Activity detection started.", Snackbar.LENGTH_LONG);
        snackbar.show();

        checkSensorSupport();

        ua = new UserActivity();

        //uzame pravice k so nastavljene, sicer je true
        ua.setGotMusic(!pref.getBoolean("music",true));
        ua.setGotLocation(!pref.getBoolean("location", true));
        ua.setGotMovement(!pref.getBoolean("motion",true));
        ua.setGotPeople(!pref.getBoolean("blueTooth",true));

        Log.i("User Activity:","gotMusic: "+ua.getGotMusic()+"\n"
        +"gotLocation: "+ua.getGotLocation()+"\n"+"gotMovement: "+ua.getGotMovement()+
        "\n"+ua.getGotPeople());

        ua.setUaUsername(getUser().getUsername());
        ua.setOnActivityDoneListener(viewModel);
        viewModel.setUserActivity(ua);

        registerDetectionReceivers();

        boolean askPer = false;
        //naprava ima bt. bt ni nevarna pravica zato jo mamo dodeljeno
        if(pref.getBoolean("blueToothSensor",false) && pref.getBoolean("blueTooth",false)) {
            //če ni bt ukloplen prosmo uporabnika d ga uklop
            if(!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
            } else {
                startPeopleDetection();
            }
        }
        //ni nevarna pravica
        if(pref.getBoolean("motionSensor",false) && pref.getBoolean("motion",false)) {
            startMovementDetection();
        }

        if(pref.getBoolean("locationSensor",false)) {
            if(pref.getBoolean("location", false)) {
                startLocationDetection();
            } else {
                askPer = true;
            }
        }

        if(pref.getBoolean("musicSensor", false)) {
            if(pref.getBoolean("music",false)) {
                startSongDetection();
            } else {
                askPer = true;
            }
        }

        if(askPer)
            askPermissions();
    }

    @Override
    public void startSongDetection() {
        receiverS.register(this,filterS);
        Intent intent = new Intent(MainActivity.this, SongDetectorService.class);
        startService(intent);
    }

    @Override
    public void startPeopleDetection() {
        receiverP.register(this,filterP);
        intentP = new Intent(MainActivity.this, PeopleDetectorService.class);
        startService(intentP);
    }

    @Override
    public void startMovementDetection() {
        receiverM.register(this,filterM);
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        Intent intentM = new Intent(MainActivity.this, DetectedActivitiesIntentService.class);
        PendingIntent pintent =  PendingIntent.getService(MainActivity.this, 0, intentM, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesButtonHandler(pintent);
    }

    @Override
    public void startLocationDetection() {
        receiverL.register(MainActivity.this,filterL);
        if(mLocationDetector == null) {
            mLocationDetector = new LocationDetector();
        }
        mLocationDetector.startLocationDetection(this);
    }

    public void requestActivityUpdatesButtonHandler(PendingIntent intent) {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                DETECTION_INTERVAL_IN_MILLISECONDS,
                intent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                //Toast.makeText(MainActivity.this,
                //        "UPDATES ENABLED",
                //        Toast.LENGTH_SHORT)
                //        .show();
            }
        });
    }
    public void removeActivityUpdatesButtonHandler() {
        if (mActivityRecognitionClient == null) {
            mActivityRecognitionClient = new ActivityRecognitionClient(this);
        }
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                getActivityDetectionPendingIntent());
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                //Toast.makeText(MainActivity.this,
                //        "UPDATES REMOVED",
                //        Toast.LENGTH_SHORT)
                //        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Activity recognition", "Failed to enable activity recognition.");
                //Toast.makeText(MainActivity.this, "updates not removed",
                //        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(MainActivity.this, DetectedActivitiesIntentService.class);
        return PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void activityDetectedDialog(String title, final String post) {
        //TODO: začasna rešitev. mislim da je problem orientacija
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);


        unregisterDetectionReceivers();
        Log.i("DETECTED ACTIVITY"," "+post);
        detectionInprogress = false;
        viewModel.setUserActivity(new UserActivity());
        alertDialogBuilder = new AlertDialog.Builder(this);//new AlertDialog.Builder(new ContextThemeWrapper(loginActivity, R.style.Drowner));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(getUser().getUsername()+" \n"+post);
        alertDialogBuilder.setPositiveButton("POST", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Post p = new Post(getUser().getUserID(), getUser().getUsername(), post);
                dialog.dismiss();
                wasDismissed = true;
                viewModel.postDetectedActivity(p);
            }
        });
        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                wasDismissed = true;
            }
        });
        mAlertDialog = alertDialogBuilder.show();
        wasDismissed = false;
        mDialogPost = post;
    }

    public void registerDetectionReceivers() {
        if(!receiverL.isRegistered) {
            receiverL.register(this,filterL);
        }
        if(!receiverS.isRegistered) {
            receiverS.register(this,filterS);
        }
        if(!receiverP.isRegistered) {
            receiverP.register(this,filterP);
        }
        if(!receiverM.isRegistered) {
            receiverM.register(this,filterM);
        }
    }
    public void unregisterDetectionReceivers() {
        if(receiverM.isRegistered) {
            receiverM.unregister(this);
            //Log.i("ON PAUSE","receiverM unregistered.");
        }
        if(receiverP.isRegistered) {
            receiverP.unregister(this);
            //Log.i("ON PAUSE","receiverP unregistered.");
        }
        if(receiverS.isRegistered) {
            receiverS.unregister(this);
            //Log.i("ON PAUSE","receiverS unregistered.");
        }
        if(receiverL.isRegistered) {
            receiverL.unregister(this);
            //Log.i("ON PAUSE","receiverL unregistered.");
        }
    }

    int i = 0;
    public void test() {
        timerRunning = false;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Continue timer? Time left:"+automaticDetectionHandler.getElapsedTime());
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(MainActivity.this,"You clicked yes",Toast.LENGTH_LONG).show();
                                timerStart();
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stopAutomaticActivityDetectionTimer();
                            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    // https://stackoverflow.com/questions/22839492/how-to-change-reset-handler-post-delayed-time
    static class AutomaticActivityDetectionHandler extends Handler {
        MainActivity ma;
        //static int TIME_TO_WAIT = 30 * 60 * 1000;
        long TIME_TO_WAIT;
        long startTime;
        boolean isRunning;
        int i;
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                //ma.startDetection();
                ma.test();
            }
        };

        public void startTimer() {
            startTime = System.nanoTime();
            isRunning = true;
            this.postDelayed(myRunnable, TIME_TO_WAIT);
            Log.i("TIMER START","timer started "+startTime);
        }

        public void stopTimer() {
            isRunning = false;
            this.removeCallbacks(myRunnable);
            Log.i("TIMER STOP","timer stoped");
        }

        public void restartTimer() {
            isRunning = true;
            startTime = System.nanoTime();
            this.removeCallbacks(myRunnable);
            this.postDelayed(myRunnable, TIME_TO_WAIT);
            Log.i("TIMER RESTART","timer restarted");
        }

        public void resumeTimer(Long timePassed) {
            this.removeCallbacks(myRunnable);
            this.postDelayed(myRunnable, TIME_TO_WAIT - timePassed);
            isRunning = true;
            startTime = System.nanoTime();
            Log.i("TIMER RESUMED","time left:"+Long.toString((TIME_TO_WAIT - timePassed)/1000));
        }

        public long getElapsedTime() {
            if(isRunning)
                return (System.nanoTime()-startTime);
            else
                return 0;
        }
    }


    //ni več v uporabi
    //private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener
   //         = new SharedPreferences.OnSharedPreferenceChangeListener() {
    //    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    //        switch (key) {
    //            case "location":
    //                if(prefs.getBoolean("location",false)) {
    //                    Log.i("location"," is true");
    //                    //startLocationDetection();
    //                }
    //                break;
    //            case "music":
    //                if(prefs.getBoolean("music",false)) {
    //                    Log.i("music"," is true");
    //                    //startSongDetection();
     //               }
     //               break;
     //           case "blueTooth":
     //               if(prefs.getBoolean("blueTooth",false)) {
     //                   Log.i("bt"," is true");
     //                   //startPeopleDetection();
     //               }
     //               break;
     //           case "motion":
     //               if(prefs.getBoolean("motion",false)) {
     //                   Log.i("motion", " is true");
     //                   //   startMovementDetection();
     //               }
     //               break;
     //           default:
     //               break;
     //       }
     //   }
    //};
}



/*
@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean requestAll = false;
        boolean rA = false;
        boolean wES = false;
        if(pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(this);
        }
        SharedPreferences.Editor editor = pref.edit();
        switch (requestCode) {
            case REQUEST_ALL_PERMISSIONS: {
                requestAll = true;
                //boolean bt = false;
                //boolean btA = false;

                for(int i = 0; i < permissions.length; i++) {
                    switch (permissions[i]) {
                        case Manifest.permission.ACCESS_FINE_LOCATION:
                            if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                                editor.putBoolean("location",true);
                                startLocationDetection();
                            } else {
                                editor.putBoolean("location",false);
                                viewModel.onLocationDetected("");
                            }
                            break;
                        case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                            wES = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                            break;
                        case Manifest.permission.RECORD_AUDIO:
                            rA = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                            break;
                        //case Manifest.permission.BLUETOOTH:
                        //    if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //        bt = true;
                        //        if(btA) {
                        //            editor.putBoolean("blueTooth",true);
                        //        }
                        //    } else {
                        //        bt = false;
                        //    }
                        //    break;
                        //case Manifest.permission.BLUETOOTH_ADMIN:
                        //    if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //        btA = true;
                        //        if(bt) {
                        //            editor.putBoolean("blueTooth",true);
                        //        }
                        //    } else {
                        //        btA = false;
                        //    }
                        //    break;
                    }
                    //Log.i("PERMISSIONS","request: "+permissions[i]+" result: "+grantResults[i]);
                    //if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //    Log.i("PERMISSIONS", "granted");
                    //}
                }
                editor.apply();
            }
            default:
                break;
        }
        if(requestAll) {
            if(!rA || !wES)
                viewModel.onSongDetected("");
            else if (rA && wES) {
                editor.putBoolean("music", true);
                startSongDetection();
            }
        }
    }
* */

/*
@Override
    public void askPermissions() {
        if(pref == null) {
            pref = PreferenceManager.getDefaultSharedPreferences(this);
        }
        SharedPreferences.Editor editor = pref.edit();
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        //bluetooth permissions se dobijo avtomatsko
        //if (pref.getBoolean("blueTooth",false)) {
        //    if(ActivityCompat.checkSelfPermission(MainActivity.this,
        //            Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED &&
        //            ActivityCompat.checkSelfPermission(MainActivity.this,
        //                    Manifest.permission.BLUETOOTH_ADMIN) !=
        //            PackageManager.PERMISSION_GRANTED) {
        //        permissionsNeeded.add(Manifest.permission.BLUETOOTH);
        //        permissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN);
        //    } else {
        //        editor.putBoolean("blueTooth",true);
        //        //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                ////prosim da prizge bluetooth
                //if(!mBluetoothAdapter.isEnabled()){
                //    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //    startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
                //}
        //    }
        //}

        //ni nevarna pravica
        //if(pref.getBoolean("blueToothSensor",false))
            //if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
               // Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
               // startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
            //} else {
          //      editor.putBoolean("blueTooth", true);
            //}
        //"com.google.android.gms.permission.ACTIVITY_RECOGNITION" izgleda ni dangerous
       // if(pref.getBoolean("motionSensor",false))
        //    editor.putBoolean("motion",true);

        if(pref.getBoolean("locationSensor",false)) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                editor.putBoolean("location",true);
            }
        }

        if (pref.getBoolean("musicSensor", false)) {
            boolean wES = false;
            boolean rA = false;
            if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                wES = true;
            }
            if(ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
            } else {
                rA = true;
            }
            if(wES && rA) {
                editor.putBoolean("music",true);
            }
        }
        editor.apply();
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), REQUEST_ALL_PERMISSIONS);
        }
    }

*/