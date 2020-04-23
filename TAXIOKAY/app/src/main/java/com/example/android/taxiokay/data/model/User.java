package com.example.android.taxiokay.data.model;

public class User {
    private String id;
    private String username;
    private String nombre;
    private String telefono;

    public User(String id, String username, String nombre, String telefono)
    {
        this.id = id;
        this.username=username;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getId()
    {
        return id;
    }

    public String getUsername()
    {
        return username;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}
