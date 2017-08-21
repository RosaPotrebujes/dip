package com.example.rosa.diplomska;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //fragmenti ki jih bom prkazvala
    private SettingsFragment sf;
    private ProfileFragment pf;
    private HomeFragment hf;
    private AboutFragment af;

    //tags za fragmente
    private final static String SETTINGS_FRAGMENT_TAG = "SettingsFradment";
    private final static String PROFILE_FRAGMENT_TAG = "ProfileFragment";
    private final static String HOME_FRAGMENT_TAG = "HomeFragment";
    private final static String ABOUT_FRAGMENT_TAG = "AboutFragment";

    //booleani s katerimi bom pogledala kateri senzorji so na voljo.
    //TODO: naredi to z preferences
    boolean hasMicrophone;
    boolean hasBluetooth;
    boolean hasGps;
    boolean hasMotion;

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

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //dialog
        context = this;
        dialog = new Dialog(context);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Detect activity");
        dialog.setContentView(R.layout.fragment_detect_activity_dialog);

        cancelDialog = (TextView) dialog.findViewById(R.id.btnCancelDialog);
        cancelDialog.setOnClickListener(this);
        confirmDialog = (TextView) dialog.findViewById(R.id.btnConfirmDialog);
        confirmDialog.setOnClickListener(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //myToolbar.setSubtitle("Test Subtitle");
        myToolbar.inflateMenu(R.menu.menu);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (findViewById(R.id.container_main) != null) {
            hf = (HomeFragment) fm.findFragmentByTag(HOME_FRAGMENT_TAG);
            pf = (ProfileFragment) fm.findFragmentByTag(PROFILE_FRAGMENT_TAG);
            sf = (SettingsFragment) fm.findFragmentByTag(SETTINGS_FRAGMENT_TAG);
            af = (AboutFragment) fm.findFragmentByTag(ABOUT_FRAGMENT_TAG);

            if (savedInstanceState == null) { //ko se prvic naredi activity ni nobenih fragmentov zato jih nrdimo
                sf = new SettingsFragment();
                pf = new ProfileFragment();
                hf = new HomeFragment();
                af = new AboutFragment();
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right);
                ft.add(R.id.container_main,hf,HOME_FRAGMENT_TAG);
                getSupportActionBar().setTitle(R.string.menu_home);
                ft.commit();
            }else{
                if(hf == null){
                    hf = new HomeFragment();
                }
                if(pf == null){
                    pf = new ProfileFragment();
                }
                if(sf == null){
                    sf = new SettingsFragment();
                }
                if(af == null){
                    af = new AboutFragment();
                }
                //da ne zjebe naslov pri rotaciji
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
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);

        //nav
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            Drawable menuIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.menu_icon, null);
            //   menuIcon = DrawableCompat.wrap(menuIcon);
            // DrawableCompat.setTint(menuIcon, getResources().getColor(R.color.lightBlue));//ContextCompat.getColor(context, R.color.lightBlue));//getResources().getColor(R.color.lightBlue,this.getTheme()));
            actionBar.setHomeAsUpIndicator(menuIcon);
            actionBar.setDisplayHomeAsUpEnabled(true);

/*            int color = ContextCompat.getColor(context, R.color.yourcolor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DrawableCompat.setTint(drawable, color);

            } else {
                drawable.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }*/

        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                //naslednja koda menja fragmente glede na klik v nav view.
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                //animation
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right);
                switch (menuItem.getItemId()) {
                    case R.id.menuHome:
                        if (findViewById(R.id.container_main) != null) {
                            getSupportActionBar().setTitle(R.string.menu_home);
                            ft.replace(R.id.container_main, hf, HOME_FRAGMENT_TAG);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                        break;
                    case R.id.menuProfile:
                        if (findViewById(R.id.container_main) != null) {
                            getSupportActionBar().setTitle(R.string.menu_profile);
                            ft.replace(R.id.container_main,pf,PROFILE_FRAGMENT_TAG);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                        break;
                    case R.id.menuSettings:
                        /*TODO: ta source code does not match binary sranje...*/
                        if (findViewById(R.id.container_main) != null) {
                            getSupportActionBar().setTitle(R.string.menu_settings);
                            ft.replace(R.id.container_main,sf,SETTINGS_FRAGMENT_TAG);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                        break;
                    case R.id.menuAbout:
                        if (findViewById(R.id.container_main) != null) {
                            getSupportActionBar().setTitle(R.string.menu_about);
                            ft.replace(R.id.container_main,af,ABOUT_FRAGMENT_TAG);
                            ft.addToBackStack(null);
                            ft.commit();
                        }
                        break;
                    case R.id.menuLogout:
                        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(myIntent);
                        finish();
                    default:
                        break;
                }
               // Toast toast;
               // toast = Toast.makeText(getApplicationContext(),"swag",Toast.LENGTH_LONG);
               // toast.show();
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
        //endnav
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                dialog.show();
                break;
            case R.id.btnCancelDialog:
                dialog.dismiss();
                break;
            case R.id.btnConfirmDialog:
                Snackbar.make(findViewById(R.id.coordinator_main),"Accept", Snackbar.LENGTH_SHORT).show();
                break;
            default:
                break;
                //  Snackbar.make(findViewById(R.id.coordinator_main),"FAB", Snackbar.LENGTH_SHORT).show();
        }
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

    private Boolean checkPermission(){
        int gps = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);
        int bt = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.BLUETOOTH);
        int mic = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECORD_AUDIO);
        int mov = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.BODY_SENSORS);

        if (gps == PackageManager.PERMISSION_GRANTED) {

        }


        return true;
    }

    /*
   meni na desni strani (pikce). mam navigation view zato bo ta kle ce bom mrbit se kej rabla.
   Sicer je pa upokojen.
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        //Tale del je zato, da k izberem nek element ga odpre, prikaze naslov in skrije v meniju gumb.
        //pogledam ce fragment obstaja in ce je viden. ce je, pomen da je v ospredju, in da smo izbrali
        //doloceni element v meniju home fragment - prtisnl smo home.
        if(hf != null && hf.isVisible()){
            menu.findItem(R.id.menuHome).setVisible(false);
            invalidateOptionsMenu();
        }
        else if(pf!=null && pf.isVisible()){
            menu.findItem(R.id.menuProfile).setVisible(false);
            invalidateOptionsMenu();
        }
        //ne bom dodala else, in case ce bo se kaksen fragment. nej bo kr else if.
        else if(sf != null && sf.isVisible()){
            menu.findItem(R.id.menuSettings).setVisible(false);
            invalidateOptionsMenu();
        }
        return true;
    }*/
}
