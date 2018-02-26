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
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.databinding.HeaderBinding;
import com.example.rosa.diplomska.detector.DetectedActivitiesIntentService;
import com.example.rosa.diplomska.detector.DetectorConstants;
import com.example.rosa.diplomska.detector.LocationDetector;
import com.example.rosa.diplomska.detector.MasterDetector;
import com.example.rosa.diplomska.detector.PeopleDetectorService;
import com.example.rosa.diplomska.detector.SongDetectorService;
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



import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

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

    //detect activity
    LocationDetector mLocationDetector;
    BroadcastReceiver receiverS;
    BroadcastReceiver receiverP;
    BroadcastReceiver receiverL;
    BroadcastReceiver receiverM;

    IntentFilter filterS;
    IntentFilter filterP;
    IntentFilter filterL;
    IntentFilter filterM;

    private boolean isFSregistered = false;
    private boolean isFPregistered = false;
    private boolean isFLregistered = false;
    private boolean isFMregistered = false;

    private boolean detectionInprogress = false;

    //permissions
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_LOCATION = 2;
    private final static int REQUEST_CHECK_SETTINGS = 3;
    private final static int REQUEST_EXTERNAL_STORAGE_AND_RECORD_AUDIO = 4;

    private BluetoothAdapter mBluetoothAdapter;

    SharedPreferences pref;

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
            } else {
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
        unregisterDetectionReceivers();
        super.onDestroy();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
    @Override
    public void onPause() {
        unregisterDetectionReceivers();
        super.onPause();
    }
    @Override
    public void onResume() {
        registerDetectionReceivers();
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
        alertDialogBuilder.show();
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
            }
            else {//if (resultCode == MainActivity.RESULT_CANCELED) {
                //uporabnik/ca je partypoop
                editor.putBoolean("blueTooth", false);
            }
        }
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if(resultCode == MainActivity.RESULT_OK) {
                editor.putBoolean("location",true);
            } else {
                editor.putBoolean("location",false);
            }
        }
        editor.apply();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("location",true);
                    editor.apply();
                } else {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("location",false);
                    editor.apply();
                }
                return;
            }
            case REQUEST_EXTERNAL_STORAGE_AND_RECORD_AUDIO: {
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = pref.edit();

                    if (StoragePermission && RecordPermission) {
                        editor.putBoolean("music",true);
                    } else {
                        editor.putBoolean("music",false);
                    }
                    editor.apply();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
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
        //bluetooth
        if(pref.getBoolean("blueTooth",true)) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            //prosim da prizge bluetooth
            if(!mBluetoothAdapter.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
            }
        }
        if(pref.getBoolean("location", true)) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        }
        if(pref.getBoolean("music",true)) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new
                        String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, REQUEST_EXTERNAL_STORAGE_AND_RECORD_AUDIO);
            }
        }
    }

    @Override
    public void checkSensorSupport() {
        SharedPreferences.Editor editor = pref.edit();
        if(!checkIfBTSupported()){
            editor.putBoolean("blueTooth", false);
        }
        boolean b = checkIfGooglePlayServicesAvailable();
        boolean b2 = checkIfGPSSupported();
        if(!checkIfGPSSupported() || !checkIfGooglePlayServicesAvailable()) {
            editor.putBoolean("location", false);
        }
        if(!checkIfMotionDetectionSupported()) {
            editor.putBoolean("motion",false);
        }
        if(!checkIfMusicDetectionSupported()) {
            editor.putBoolean("music",false);
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

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        checkSensorSupport();
        askPermissions();

        UserActivity ua = new UserActivity();
        ua.setGotMusic(!pref.getBoolean("music",true));
        ua.setGotLocation(!pref.getBoolean("location", true));
        //ua.setGotMovement(!pref.getBoolean("motion",true));
        ua.setGotMovement(false);
        ua.setGotPeople(!pref.getBoolean("blueTooth",true));

        ua.setUaUsername(getUser().getUsername());
        ua.setOnActivityDoneListener(viewModel);
        viewModel.setUserActivity(ua);

        filterS = new IntentFilter();
        filterS.addAction(DetectorConstants.ACTION_SONG_DETECTED);

        filterP = new IntentFilter();
        filterP.addAction(DetectorConstants.ACTION_PEOPLE_DETECTED);

        filterL = new IntentFilter();
        filterL.addAction(DetectorConstants.ACTION_LOCATION_DETECTED);

        filterM = new IntentFilter();
        filterM.addAction(DetectorConstants.ACTION_MOTION_DETECTED);

        receiverP = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(DetectorConstants.ACTION_PEOPLE_DETECTED)) {
                    int detectedPeople = intent.getIntExtra(DetectorConstants.EXTRA_PEOPLE,0);
                    Log.i("PEOPLE DETECTION","Detected people: " + detectedPeople);
                    viewModel.onPeopleDetected(detectedPeople);
                }
            }
        };
        receiverS = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(intent.getAction()) {
                    case DetectorConstants.ACTION_SONG_DETECTED:
                        String detectedSong = intent.getStringExtra(DetectorConstants.EXTRA_SONG);
                        Log.i("SONG DETECTION", "Song detected: " + detectedSong);
                        viewModel.onSongDetected(detectedSong);
                        break;
                    default:
                        break;
                }
            }
        };
        receiverL = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(intent.getAction()) {
                    case DetectorConstants.ACTION_LOCATION_DETECTED:
                        String detectedLocation = intent.getStringExtra(DetectorConstants.EXTRA_LOCATION);
                        Log.i("LOCATION DETECTION", "Location detected:" + detectedLocation);
                        viewModel.onLocationDetected(detectedLocation);
                        break;
                    default:
                        break;
                }
            }
        };
        receiverM = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(intent.getAction()) {
                    case DetectorConstants.ACTION_MOTION_DETECTED:
                        String detectedMovement = intent.getStringExtra(DetectorConstants.EXTRA_ACTIVITY);
                        Log.i("MOVEMENT DETECTION: ","Detected movement:" + detectedMovement);
                        removeActivityUpdatesButtonHandler();
                        viewModel.onMotionDetected(detectedMovement);
                        break;
                    default:
                        break;
                }
            }
        };
        registerDetectionReceivers();
        //registerReceiver(receiverM,filterM);
        Intent intent = new Intent(MainActivity.this, SongDetectorService.class);
        startService(intent);

        Intent intentP = new Intent(MainActivity.this, PeopleDetectorService.class);
        startService(intentP);

        //location detection
        if(mLocationDetector == null) {
            mLocationDetector = new LocationDetector();
        }
        mLocationDetector.startLocationDetection(this);

        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        Intent intentM = new Intent(MainActivity.this, DetectedActivitiesIntentService.class);
        PendingIntent pintent =  PendingIntent.getService(MainActivity.this, 0, intentM, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesButtonHandler(pintent);
    }

    public void requestActivityUpdatesButtonHandler(PendingIntent intent) {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                DETECTION_INTERVAL_IN_MILLISECONDS,
                intent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(MainActivity.this,
                        "UPDATES ENABLED",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
    public void removeActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                getActivityDetectionPendingIntent());
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(MainActivity.this,
                        "UPDATES REMOVED",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Activity recognition", "Failed to enable activity recognition.");
                Toast.makeText(MainActivity.this, "updates not removed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(MainActivity.this, DetectedActivitiesIntentService.class);
        return PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public String getUserActivity() {
        UserActivity ua = new UserActivity();
        //User lojze is in Ljubljana,
        //walking and listening to enya
        //with 2 other people.
        boolean location = !ua.getUaLocation().isEmpty();
        boolean movement = !ua.getUaMovement().isEmpty();
        boolean music = !ua.getUaMusic().isEmpty();
        boolean people = ua.getUaPeople() != 0;

        String mPost = "User " + getUser().getUsername() + " is in " + ua.getUaLocation() +
                ", hanging out and listening to " + ua.getUaMusic() + " with " + ua.getUaPeople()
                + " other people.";
 /*       String mPost = "User " + getUser().getUsername();
        mPost += " is ";
        if(location) {
            mPost += " in " + ua.getUaLocation() + ", ";
        }
        if(movement) {
            mPost += ua.getUaMovement();
        } else {
            mPost += "hanging out";
        }
        if(music) {
            mPost += " and listening to " + ua.getUaMusic();
        }
        if(people) {
            mPost += " with " + ua.getUaPeople() + " other people";
        }
        mPost += ".";
*/
        return mPost;

        //String tPost = "User "+getUser().getUsername()+
        //        " is "+ua.getUaMovement()+" at "+ua.getUaLocation()
        //        + " with "+ua.getUaPeople()+" other people.";
//        String tPost = "User " + getUser().getUsername();
//        if(!ua.getUaMovement().isEmpty()) {
//            tPost += " is " + ua.getUaMovement();
//        } else {
//            tPost += " is hanging";
//        }
//        if(!ua.getUaLocation().isEmpty()) {
//            tPost += " at " + ua.getUaLocation();
//        }
//        if(ua.getUaPeople() != 0) {
//            tPost += " with " + ua.getUaPeople() + " other people";
//        }
//        if(!ua.getUaMusic().isEmpty()) {
//            tPost += " while listening to " + ua.getUaMusic() + ".";
//        } else {
//            tPost += ".";
//        }
    }

    @Override
    public void activityDetectedDialog(String title, final String post) {
        unregisterDetectionReceivers();
        detectionInprogress = false;
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);//new AlertDialog.Builder(new ContextThemeWrapper(loginActivity, R.style.Drowner));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(getUser().getUsername()+" \n"+post);
        alertDialogBuilder.setPositiveButton("POST", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Post p = new Post(getUser().getUserID(), getUser().getUsername(), post);
                dialog.dismiss();
                viewModel.postDetectedActivity(p);
            }
        });
        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    public void registerDetectionReceivers() {
        pref = (pref == null) ? PreferenceManager.getDefaultSharedPreferences(this) : pref;
        if(detectionInprogress) {
            if(pref.getBoolean("music",true) && !isFSregistered) {
                if(receiverS != null && filterS != null) {
                    registerReceiver(receiverS,filterS);
                    isFSregistered = true;
                }
            }
            if(pref.getBoolean("blueTooth",true) && !isFPregistered) {
                if(receiverP != null && filterP != null) {
                    registerReceiver(receiverP,filterP);
                    isFPregistered = true;
                }
            }
            if(pref.getBoolean("location",true) && !isFLregistered) {
                if(receiverL != null && filterL != null) {
                    registerReceiver(receiverL, filterL);
                    isFLregistered = true;
                }
            }
          //  if(pref.getBoolean("motion",true) && !isFMregistered) {
            //    if(receiverM != null && filterM != null) {
                    registerReceiver(receiverM, filterM);
                    isFMregistered = true;
              //  }
            //}
        }
    }
    public void unregisterDetectionReceivers() {
        if(detectionInprogress) {
            if(isFSregistered && receiverS != null) {
                unregisterReceiver(receiverS);
                isFSregistered = false;
            }
            if(isFLregistered && receiverL != null) {
                unregisterReceiver(receiverL);
                isFLregistered = false;
            }
            if(isFMregistered && receiverM != null) {
                unregisterReceiver(receiverM);
                isFMregistered = false;
            }
            if(isFPregistered && receiverP != null) {
                unregisterReceiver(receiverP);
                isFPregistered = false;
            }
        }
    }
}