package com.example.rosa.diplomska.model;


import android.databinding.BindingAdapter;
import android.support.design.widget.TextInputLayout;

public class CustomDataBindingAdapters {
    public CustomDataBindingAdapters() {
    }
    @BindingAdapter("app:errorText")
    public static void setErrorMessage(TextInputLayout view, String errorMessage) {
        view.setError(errorMessage);
    }
}
