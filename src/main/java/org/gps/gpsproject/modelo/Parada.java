package org.gps.gpsproject.modelo;

public class Parada {
    private String id;
    private String nombre;

    public Parada(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object aux){
        if(this == aux) return true;
        if(!(aux instanceof Parada)) return false;
        Parada p = (Parada) aux;
        return id.equals(p.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return nombre + " (" + id + ")";
    }
}
