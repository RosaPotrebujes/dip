package com.example.rosa.diplomska.view.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
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
import android.view.MenuItem;
import android.widget.Toast;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.detector.MasterDetector;
import com.example.rosa.diplomska.detector.MasterDetectorService;
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
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements MainNavigator, NavigationView.OnNavigationItemSelectedListener, MasterDetector {

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


    boolean mBounded;
    MasterDetectorService masterDetectorService;
    MasterDetectorService.MasterDetectorBinder masterDetectorBinder;

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_LOCATION = 2;
    private final static int REQUEST_CHECK_SETTINGS = 3;

    private BluetoothAdapter mBluetoothAdapter;
    private int mPeople;
    String mBTErrMsg;
    BroadcastReceiver mReceiver;

    SharedPreferences pref;
    //location
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  // 10 secs
    private long FASTEST_INTERVAL = 5000; // 5 sec
    private LocationSettingsRequest mLocationSettingsRequest;
    private Task<LocationSettingsResponse> checkLocationSettingsTask;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Location mLastKnownLocation;
    private String resultMessage;
    private String resultAddress;
    MasterDetectorService.OnChangeListener mOnChangeListener = new MasterDetectorService.OnChangeListener() {
        @Override
        public void onDetectedLocation(String location) {
            Toast.makeText(getMNContext(),"Location detection:"+location, Toast.LENGTH_LONG).show();

        }
        @Override
        public void onDetectedPeople(int people) {
            Toast.makeText(getMNContext(),"People detection:"+people, Toast.LENGTH_LONG).show();

        }
    };



    private BroadcastReceiver  BReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            //put here whaterver you want your activity to do with the intent received
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            masterDetectorBinder = (MasterDetectorService.MasterDetectorBinder) service;
            masterDetectorService = masterDetectorBinder.getService();
            masterDetectorService.setOnChangeListener(new MasterDetectorService.OnChangeListener() {
                @Override
                public void onDetectedLocation(String location) {
                    Toast.makeText(getMNContext(),"Location detection:"+location, Toast.LENGTH_LONG).show();
                }
                @Override
                public void onDetectedPeople(int people) {
                    Toast.makeText(getMNContext(),"People detection:"+people, Toast.LENGTH_LONG).show();
                }
            });
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getMNContext(),"Service is disconnected", Toast.LENGTH_LONG).show();
            mBounded = false;
            masterDetectorService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //change theme
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = pref.getString("appTheme", "1");
        if (theme.equals("1")) {
            setTheme(R.style.Drowner);
        } else if (theme.equals("2")) {
            setTheme(R.style.VineLight);
        }
  //      FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
  //      fab.setOnClickListener(new View.OnClickListener() {
  //          @Override
  //          public void onClick(View view) {
  //              Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
  //                      .setAction("Action", null).show();
  //          }
  //      });
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        User user = new User(pref.getInt("userId",-1),pref.getString("username",""),pref.getString("email",""),pref.getString("description",""));
        binding.setUser(user);

        navigator = this;
        masterDetector = this;
       // masterDetector = new MasterDetector(this, conn);
        fm = getSupportFragmentManager();

        viewModel = new MainActivityViewModel(navigator,masterDetector);
        binding.setMainViewModel(viewModel);
        super.onCreate(savedInstanceState);

        /*//dialog
        context = this;
        dialog = new Dialog(context);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Detect activity");
        dialog.setContentView(R.layout.fragment_detect_activity_dialog);
        cancelDialog = (TextView) dialog.findViewById(R.id.btnCancelDialog);
        //cancelDialog.setOnClickListener(viewModel);
        confirmDialog = (TextView) dialog.findViewById(R.id.btnConfirmDialog);
        //confirmDialog.setOnClickListener(viewModel);*/

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
        Intent intent = new Intent(this, MasterDetectorService.class);
        this.bindService(intent, conn, Context.BIND_AUTO_CREATE);
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
        this.unbindService(conn);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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


            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public boolean checkIfBTSupported() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
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
    }

    @Override
    public void checkSensorSupport() {
        SharedPreferences.Editor editor = pref.edit();
        if(!checkIfBTSupported()){
            editor.putBoolean("blueTooth", false);
        }
        if(!checkIfGPSSupported() || !checkIfGooglePlayServicesAvailable()) {
            editor.putBoolean("location", false);
        }
        editor.apply();
    }

    @Override
    public void startDetection() {
        masterDetectorService.startBTDetection();
        startLocationDetection();
    }

    @Override
    public void startBTDetection() {
/*        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    mPeople = 0;
                    //discovery starts, we can show progress dialog or perform other tasks
                    Log.i(TAG, "Bluetooth discovery started.");
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    //devicesFound = 0;//
                    unregisterReceiver(mReceiver);
                    Log.i(TAG, "Bluetooth discovery ended. Found "+mPeople+" devices.");
                    mBTErrMsg = "";


                    //btServiceResponse(Constants.SUCCESS_RESULT,errMsg,devicesFound);
                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //bluetooth device found
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i(TAG, "Device found: " + device.getName() + "; MAC " + device.getAddress());
                    mPeople++;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver,filter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();*/
    }

    @Override
    public void startLocationDetection() {
        setUpLocationRequest();
        //check settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastKnownLocation = locationResult.getLastLocation();
                if(mLastKnownLocation != null) {
                    masterDetectorService.getAddress(mLastKnownLocation);
                }
            }
        };

        //check location settings
        SettingsClient client = LocationServices.getSettingsClient(MainActivity.this);
        checkLocationSettingsTask = client.checkLocationSettings(mLocationSettingsRequest);
        checkLocationSettingsTask.addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //preverim permission in zahtevam location update
                startLocationUpdate();
            }
        });
        checkLocationSettingsTask.addOnFailureListener(MainActivity.this, new OnFailureListener() {
            //ce niso nastavitve OK pogledam če je kej kar loh uporabnik poprav.
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("location", false);
                        editor.apply();
                    }
                }
            }
        });
    }

    @Override
    public void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    @Override
    public void stopLocationUpdate() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void setUpLocationRequest(){
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(UPDATE_INTERVAL);
        this.mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public Location getLastKnownLocation() {
        return this.mLastKnownLocation;
    }
}

/*
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.model.Entity.User;
import com.example.rosa.diplomska.databinding.ActivityMainBinding;
import com.example.rosa.diplomska.navigator.MainNavigator;
import com.example.rosa.diplomska.view.fragment.AboutFragment;
import com.example.rosa.diplomska.view.fragment.HomeFragment;
import com.example.rosa.diplomska.view.fragment.ProfileFragment;
import com.example.rosa.diplomska.view.fragment.SettingsFragment;
import com.example.rosa.diplomska.viewModel.HomeViewModel;
import com.example.rosa.diplomska.viewModel.MainActivityViewModel;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {
    FragmentManager fm;

    //nav
    DrawerLayout drawerLayout;
    NavigationView navView;

    //fab
    FloatingActionButton floatingActionButton;

    //detect activity
    Context context;
    Dialog dialog;
    TextView cancelDialog;
    TextView confirmDialog;

    private ActivityMainBinding binding;
    MainActivityViewModel viewModel;
    MainNavigator mainNavigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //change theme
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = pref.getString("appTheme", "1");
        if (theme.equals("1")) {
            setTheme(R.style.Drowner);
        } else if (theme.equals("2")) {
            setTheme(R.style.VineLight);
        }

        //databinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //brez data binding
        //setContentView(R.layout.activity_main);

        User user = new User(pref.getInt("userId",-1),pref.getString("username",""),pref.getString("email",""),pref.getString("description",""));
        binding.setUser(user);

        fm = getSupportFragmentManager();
        drawerLayout = binding.drawerLayout;//(DrawerLayout) findViewById(R.id.drawer_layout);
        mainNavigator = new MainNavigator(this, fm, binding, drawerLayout);
        viewModel = new MainActivityViewModel(mainNavigator);
        binding.setMainViewModel(viewModel);
        super.onCreate(savedInstanceState);

        //dialog
        context = this;
        dialog = new Dialog(context);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Detect activity");
        dialog.setContentView(R.layout.fragment_detect_activity_dialog);
        cancelDialog = (TextView) dialog.findViewById(R.id.btnCancelDialog);
        //cancelDialog.setOnClickListener(viewModel);
        confirmDialog = (TextView) dialog.findViewById(R.id.btnConfirmDialog);
        //confirmDialog.setOnClickListener(viewModel);

        Toolbar myToolbar = binding.myToolbar;//(Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //myToolbar.setSubtitle("Test Subtitle");
        myToolbar.inflateMenu(R.menu.menu);
        myToolbar.setNavigationIcon(R.drawable.ic_menu);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            Drawable menuIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
            actionBar.setHomeAsUpIndicator(menuIcon);
            actionBar.setDisplayHomeAsUpEnabled(true);
            setSupportActionBar(myToolbar);
        }

        navView = binding.navigationView;//(NavigationView) findViewById(R.id.navigation_view);//binding.navigationView;
        navView.setNavigationItemSelectedListener(mainNavigator);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        if (findViewById(R.id.container_main) != null) {
            if(savedInstanceState == null) {
                mainNavigator.setUpMainActivity();
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
        //fab
        //floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        //floatingActionButton.setOnClickListener(this);
        //binding.fab.setOnClickListener(viewModel);
    }

    public ActivityMainBinding getMainActivityBinding() {
        return this.binding;
    }

    public Dialog getDialog(){
        return this.dialog;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
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
    }

    public User getLogedUser() {
        return binding.getUser();
    }

    private Boolean checkPermission(){
        int gps = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);
        int bt = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.BLUETOOTH);
        int mic = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECORD_AUDIO);
        int mov = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.BODY_SENSORS);

        if (gps == PackageManager.PERMISSION_GRANTED) {

        }

        return true;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}*/