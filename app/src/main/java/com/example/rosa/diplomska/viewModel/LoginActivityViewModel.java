package com.example.rosa.diplomska.viewModel;

import android.arch.lifecycle.ViewModel;

import com.example.rosa.diplomska.R;
import com.example.rosa.diplomska.model.Registration;
import com.example.rosa.diplomska.model.ResourceProvider;
import com.example.rosa.diplomska.navigator.LoginNavigator;
import com.example.rosa.diplomska.volley.DataProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivityViewModel extends ViewModel {
    //registration
    Registration registration;
    ResourceProvider resourceProvider;
    DataProvider dp;
    LoginNavigator navigator;
    public LoginActivityViewModel(LoginNavigator navigator) {
        this.navigator = navigator;
        this.dp = new DataProvider(navigator);
    }
    public LoginActivityViewModel() {
    }

    public void setNavigator(LoginNavigator navigator) {
        this.navigator = navigator;
    }

    public void setDataProvider(LoginNavigator navigator) {
        this.dp = new DataProvider(navigator);
    }

    public void setResourceProvider(ResourceProvider rp) {
        this.resourceProvider = rp;
    }
    public void setRegistration(Registration r) {
        this.registration = r;
    }
    public Registration getRegistration() {
        return this.registration;
    }


    public void onBtnLoginClick(String username, String password) {
        String passwordToHash = password;
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(passwordToHash.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        dp.login(username,generatedPassword);

        //navigator.startMainActivity();
    }
    public void onRegisterTextClick() {
        navigator.goToRegisterFragment();
    }


    public void checkRegisterInput() {
        String username = registration.getRegisterUsername();
        String pass = registration.getRegisterPassword();
        String confPass = registration.getRegisterConfirmPassword();
        String email = registration.getRegisterEmail();

        boolean emptyFields = username.isEmpty() || pass.isEmpty() || confPass.isEmpty() || email.isEmpty();

        if(emptyFields){
            registration.setRegisterEmptyFieldsError(resourceProvider.getString(R.string.register_warning_empty_input));
        } else {
            registration.setRegisterEmptyFieldsError("");
        }

        if (!emptyFields && registration.getRegisterUsernameError().isEmpty() && registration.getRegisterEmailError().isEmpty()
                && registration.getRegisterPasswordError().isEmpty() && registration.getRegisterConfirmPasswordError().isEmpty()) {
            //registerFragmentClickListener.registerUser(username,email,pass);

            // https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/#md5
            String passwordToHash = pass;
            String generatedPassword = null;
            try {
                // Create MessageDigest instance for MD5
                MessageDigest md = MessageDigest.getInstance("MD5");
                //Add password bytes to digest
                md.update(passwordToHash.getBytes());
                //Get the hash's bytes
                byte[] bytes = md.digest();
                //This bytes[] has bytes in decimal format;
                //Convert it to hexadecimal format
                StringBuilder sb = new StringBuilder();
                for(int i=0; i< bytes.length ;i++)
                {
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                //Get complete hashed password in hex format
                generatedPassword = sb.toString();
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            }

            dp.register(username,email,generatedPassword);
        }
    }

    public void validateUsername() {
        StringBuilder errorUsername = new StringBuilder();
        String username = registration.getRegisterUsername();
        if(!username.isEmpty()){
            if(username.length() < 3){
                if(!errorUsername.toString().isEmpty())
                    errorUsername.append("\n");
                errorUsername.append(resourceProvider.getString(R.string.register_warning_username_length));
            }
            Pattern p = Pattern.compile("[^A-Za-z0-9_]");
            Matcher m = p.matcher(username);
            if (m.find()) {
                if(!errorUsername.toString().isEmpty())
                    errorUsername.append("\n");
                errorUsername.append(resourceProvider.getString(R.string.register_warning_username_special_characters));
            }
            if(!errorUsername.toString().isEmpty()){
                registration.setRegisterUsernameError(errorUsername.toString());
            }
            else
                registration.setRegisterUsernameError("");
        }
    }

    public void validatePassword(){
        //validate password
        StringBuilder errorPassword = new StringBuilder();
        String pass = registration.getRegisterPassword();
        if(!pass.isEmpty()){
            //pogledam, ce je koda dolga vsaj 6 znakov. max dolzina je omejena v layoutu (20)
            if(pass.length()<6) {
                if(!errorPassword.toString().isEmpty())
                    errorPassword.append("\n");
                errorPassword.append(resourceProvider.getString(R.string.register_warning_password_short));
            }
            //preverim, ce ima koda vsaj eno veliko crko, vsaj eno malo in vsaj eno stevilko
            if(!pass.matches("(.*[A-Z].*)") || !pass.matches("(.*[a-z].*)") || !pass.matches("(.*[0-9].*)")) {
                if(!errorPassword.toString().isEmpty())
                    errorPassword.append("\n");
                errorPassword.append(resourceProvider.getString(R.string.register_warning_password_structure));
            }
            if(!errorPassword.toString().isEmpty())
                registration.setRegisterPasswordError(errorPassword.toString());
            else
                registration.setRegisterPasswordError("");
        }
    }

    public void validateConfirmPassword(){
        String pass = registration.getRegisterPassword();
        String confPass = registration.getRegisterConfirmPassword();
        if(!pass.equals(confPass))
            registration.setRegisterConfirmPasswordError(resourceProvider.getString(R.string.register_warning_passwords_do_not_match));
        else
            registration.setRegisterConfirmPasswordError("");
    }

    public void validateEmail(){
        String email = registration.getRegisterEmail();
        boolean isValid = registration.isValidEmail(email);
        if(isValid)
            registration.setRegisterEmailError("");
        else
            registration.setRegisterEmailError(resourceProvider.getString(R.string.register_warning_email_not_valid));
    }
}