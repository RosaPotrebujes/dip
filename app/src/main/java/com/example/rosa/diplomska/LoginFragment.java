package com.example.rosa.diplomska;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginFragment extends Fragment implements View.OnClickListener{
    public TextView tvRegister;
    public Button btnLogin;

    private EditText username;
    private EditText password;

    private LoginListener loginListener;

    interface LoginListener{
        public void clickedRegister(FragmentManager fm);
        public void clickedLogin(String username, String password);
    }

    public void setLoginListener(LoginListener listener){
        this.loginListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /** Inflating the layout for this fragment **/
        View v = inflater.inflate(R.layout.fragment_login, null);

        username = (EditText) v.findViewById(R.id.editTextUsername);
        password = (EditText) v.findViewById(R.id.editTextPassword);

        tvRegister = (TextView) v.findViewById(R.id.textViewRegister);
        tvRegister.setOnClickListener(this);

        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        setRetainInstance(true);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewRegister:
                FragmentManager fm = this.getActivity().getSupportFragmentManager();
                loginListener.clickedRegister(fm);
                break;
            case R.id.btnLogin:
                loginListener.clickedLogin(username.toString(),password.toString());
                break;
        }
    }
    //ne pozabi! vnosa ne rabm shrant (ala username)
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
