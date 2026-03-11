package org.gps.gpsproject.modelo;

import java.util.Objects;

public class Ruta {
    private Parada destino;
    private int tiempo;
    private int distancia;
    private int costo;
    private int transbordo;

    public Ruta(Parada destino, int tiempo, int distancia, int costo, int transbordo) {
        this.destino = destino;
        this.tiempo = tiempo;
        this.distancia = distancia;
        this.costo = costo;
        this.transbordo = transbordo;
    }

    @Override
    public String toString() {
        return tiempo + "min | $" + costo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Ruta)) return false;
        Ruta r = (Ruta) obj;
        return destino.equals(r.destino) &&
                tiempo == r.tiempo &&
                costo == r.costo &&
                distancia == r.distancia &&
                transbordo == r.transbordo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(destino, tiempo, costo, distancia, transbordo);
    }

    public Parada getDestino() {
        return destino;
    }

    public int getTiempo() {
        return tiempo;
    }

    public int getDistancia() {
        return distancia;
    }

    public int getCosto() {
        return costo;
    }

    public int getTransbordo() {
        return transbordo;
    }
}
