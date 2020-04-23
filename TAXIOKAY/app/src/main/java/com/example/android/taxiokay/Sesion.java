package com.example.android.taxiokay;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Sesion {
    private SharedPreferences sesion;
    private Boolean mIsLoggedIn= false;

    public Sesion(Context context){
        sesion= PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void guardarUsuario(String usuario){
        sesion.edit().putString("usuario", usuario).apply();
    }

    public void guardarNombre(String nombre){
        sesion.edit().putString("nombre", nombre).apply();
    }
    public void guardarTelefono(String telefono)
    {
        sesion.edit().putString("telefono", telefono).apply();
    }

    public void guardarContraseña(String password){
        sesion.edit().putString("contraseña", password).apply();
    }

    public void guardarId(String id) {
        sesion.edit().putString("id", id).apply();
    }

    public String obtenerUsuario(){
        return sesion.getString("usuario", "");
    }

    public String obtenerContraseña(){
        return sesion.getString("contraseña", "");
    }

    public String obtenerId(){
        return sesion.getString("id", "");
    }

    public String obtenerTelefono(){
        return sesion.getString("telefono", "");
    }

    public void cerrarSesion(){
        sesion.edit().clear().apply(); setmIsLoggedIn(false);
    }

    public Boolean getmIsLoggedIn() {
        return mIsLoggedIn;
    }

    public void setmIsLoggedIn(Boolean mIsLoggedIn) {
        this.mIsLoggedIn = mIsLoggedIn;
    }
}
