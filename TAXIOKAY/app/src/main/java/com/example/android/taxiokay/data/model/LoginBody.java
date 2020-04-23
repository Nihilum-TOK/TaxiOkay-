package com.example.android.taxiokay.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginBody {
    @SerializedName("user")
    private String user;
    private String password;

    public LoginBody(String user, String password)
    {
        this.user=user;
        this.password=password;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
