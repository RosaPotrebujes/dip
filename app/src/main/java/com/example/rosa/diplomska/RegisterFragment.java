package com.example.rosa.diplomska;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterFragment extends Fragment {
    Button btnRegister;

    EditText username;
    EditText pass;
    EditText confPass;
    EditText email;

    TextInputLayout textLayoutUsername;
    TextInputLayout textLayoutPass;
    TextInputLayout textLayoutConfPass;
    TextInputLayout textLayoutEmail;

    interface RegisterListener{
        public void registerUser(String username, String password, String email);
    }

    private RegisterListener registerListener;

    public void setRegisterListener(RegisterListener listener){
        registerListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        View v = inflater.inflate(R.layout.fragment_register, null);
        //View v = inflater.inflate(R.layout.fragment_register,container,false);

        //vnosna polja
        username = (EditText)v.findViewById(R.id.editTextRegisterUsername);
        pass = (EditText)v.findViewById(R.id.editTextRegisterPassword);
        confPass = (EditText)v.findViewById(R.id.editTextRegisterConfirmPassword);
        email = (EditText)v.findViewById(R.id.editTextRegisterEmail);

        //textWatcher
        username.addTextChangedListener(usernameWatcher);
        pass.addTextChangedListener(passWatcher);
        confPass.addTextChangedListener(confPassWatcher);
        email.addTextChangedListener(emailWatcher);

        //TextLayoutInputs
        textLayoutUsername = (TextInputLayout) v.findViewById(R.id.textInputLayoutRegisterUsername);
        textLayoutEmail = (TextInputLayout) v.findViewById(R.id.textInputLayoutRegisterEmail);
        textLayoutPass = (TextInputLayout) v.findViewById(R.id.textInputLayoutRegisterPassword);
        textLayoutConfPass = (TextInputLayout) v.findViewById(R.id.textInputLayoutRegisterConfirmPassword);

        btnRegister = (Button) v.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRegisterInput(v);
            }
        });

        setRetainInstance(true);
        return v;
    }

    private final TextWatcher usernameWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //textLayoutUsername.setError("before text changed");
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //textLayoutUsername.setError("on text changed");
        }
        public void afterTextChanged(Editable s) {
            if (s.length() == 0)
                textLayoutUsername.setError("");
            else
                validateUsername();
        }
    };

    private final TextWatcher passWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //textLayoutPass.setError("before text changed");
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //textLayoutPass.setError("on text changed");
        }
        public void afterTextChanged(Editable s) {
            if (s.length() == 0)
                textLayoutPass.setError("");
            else
                validatePassword();
        }
    };

    private final TextWatcher confPassWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //textLayoutConfPass.setError("before text changed");
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //textLayoutConfPass.setError("on text changed");
        }
        public void afterTextChanged(Editable s) {
            if (s.length() == 0)
                textLayoutConfPass.setError("");
            else
                validateConfirmPassword();
        }
    };

    private final TextWatcher emailWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //textLayoutEmail.setError("before text changed");
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //textLayoutEmail.setError("on text changed");
        }
        public void afterTextChanged(Editable s) {
            if (s.length() == 0)
                textLayoutEmail.setError("");
            else
                //vnos se je spremenil
                validateEmail();
        }
    };

    public void validateUsername() {
        StringBuilder errorUsername = new StringBuilder();
        if(!username.getText().toString().isEmpty()){
            if(username.getText().toString().length() < 3){
                if(!errorUsername.toString().isEmpty())
                    errorUsername.append("\n");
                errorUsername.append(getString(R.string.register_warning_username_length));
            }

            Pattern p = Pattern.compile("[^A-Za-z0-9_]");
            Matcher m = p.matcher(username.getText().toString());
            if (m.find()) {
                if(!errorUsername.toString().isEmpty())
                    errorUsername.append("\n");
                errorUsername.append(getString(R.string.register_warning_username_special_characters));
            }
            if(!errorUsername.toString().isEmpty())
                textLayoutUsername.setError(errorUsername);
            else
                textLayoutUsername.setError("");
        }
    }

    public void validatePassword(){
        //validate password
        StringBuilder errorPassword = new StringBuilder();
        if(!pass.getText().toString().isEmpty()){
            //pogledam, ce je koda dolga vsaj 6 znakov. max dolzina je omejena v layoutu (20)
            if(pass.getText().toString().length()<6) {
                //textLayoutPass.setError(getString(R.string.register_warning_password_short));
                if(!errorPassword.toString().isEmpty())
                    errorPassword.append("\n");
                errorPassword.append(getString(R.string.register_warning_password_short));
            }
            //preverim, ce ima koda vsaj eno veliko crko, vsaj eno malo in vsaj eno stevilko
            if(!pass.getText().toString().matches("(.*[A-Z].*)") || !pass.getText().toString().matches("(.*[a-z].*)") || !pass.getText().toString().matches("(.*[0-9].*)")) {
                if(!errorPassword.toString().isEmpty())
                    errorPassword.append("\n");
                errorPassword.append(getString(R.string.register_warning_password_structure));
            }
            if(!errorPassword.toString().isEmpty())
                textLayoutPass.setError(errorPassword);
            else
                textLayoutPass.setError("");
        }
    }

    public void validateConfirmPassword(){
        if(!pass.getText().toString().equals(confPass.getText().toString()))
            textLayoutConfPass.setError(getString(R.string.register_warning_passwords_do_not_match));
        else
            textLayoutConfPass.setError("");
    }

    public void validateEmail(){
        //TODO: check if email is valid
        if(email.getText().toString().isEmpty())
            textLayoutEmail.setError(getString(R.string.register_warning_email_not_valid));
        else
            textLayoutEmail.setError("");
    }

    public void checkRegisterInput(View v) {
        //Snackbar ce je uporabnik pozabil katero polje
        if(username.getText().toString().isEmpty() || pass.getText().toString().isEmpty() ||
                confPass.getText().toString().isEmpty() || email.getText().toString().isEmpty()) {
            Snackbar.make(v, getString(R.string.register_warning_empty_input), Snackbar.LENGTH_SHORT).show();
        }
        else if(textLayoutUsername.getError().toString().isEmpty() && textLayoutEmail.getError().toString().isEmpty() &&
                textLayoutPass.getError().toString().isEmpty() && textLayoutConfPass.getError().toString().isEmpty()){
            Snackbar.make(v, "All input is OK.", Snackbar.LENGTH_SHORT).show();
        }
    }
}