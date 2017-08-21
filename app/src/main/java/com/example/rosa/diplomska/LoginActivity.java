package com.example.rosa.diplomska;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;

public class LoginActivity extends AppCompatActivity implements LoginFragment.LoginListener,RegisterFragment.RegisterListener {
    //TODO: klick na back mal poglej kk dela (ala, da ne bo ce se logoutas sel z backom nazaj v aplikacijo not)
    private LoginFragment lf;
    private RegisterFragment rf;
    private static final String TAG = LoginActivity.class.getSimpleName();

    private final static String LOGIN_FRAGMENT_TAG = "LoginFragment";
    private final static String REGISTER_FRAGMENT_TAG = "RegisterFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //change theme (!more bit pred klicem super on create!
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = pref.getString("appTheme", "1");
        if (theme.equals("1")) {
            setTheme(R.style.Drowner);
        } else if (theme.equals("2")) {
            setTheme(R.style.VineLight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //preverjam izgled dialoga
        // setContentView(R.layout.fragment_detect_activity_dialog);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right);

        if (findViewById(R.id.container_login) != null) {
            if(savedInstanceState != null){
                return;
            }else{
                lf = new LoginFragment();
                ft.add(R.id.container_login,lf,LOGIN_FRAGMENT_TAG);
               // ft.addToBackStack(null);
                ft.commit();
                lf.setLoginListener(this);
                rf = new RegisterFragment();
                rf.setRegisterListener(this);
            }
        }
    }

    @Override
    public void registerUser(String username, String password, String email){
        //TODO: pogledam v bazi, ce mogoce ta uporabnik ze obstaja. ce ne ga ustvarm.
    }
    @Override
    public void clickedRegister(FragmentManager fm){
        //OK... here's the deal. Ce mam sam tale toast (spodi) navadn. Ne crkne ob sprememb orientacije.
        //Ce dam pa da swappa fragment... se pa nek zalom. Dokler ne spremenim orientaciej je kul.
        //ko jo spremenim :
        //06-23 02:53:15.754 3450-3450/com.example.rosa.dipuitest E/AndroidRuntime: FATAL EXCEPTION: main
        //Process: com.example.rosa.dipuitest, PID: 3450
        //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        //RESEN: problem je bil da sm fragment manager ustvarla not v metod. ob spremembi orientacije sej
        //activity ugasnila pa sej nova nrdila, fm znotr je pa se kr kazu na prejsno. Dodala da managerja
        //podam metodi.
        //Toast toast;
        //toast = Toast.makeText(this.getApplicationContext(),"prasdfasf",Toast.LENGTH_SHORT);
        //toast.show();
        if(findViewById(R.id.container_login) != null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right);
            ft.replace(R.id.container_login, rf, REGISTER_FRAGMENT_TAG);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void clickedLogin(String username, String password){
        //TODO: poiscem uporabnika v bazi, preverim geslo.
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
       // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
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
}
