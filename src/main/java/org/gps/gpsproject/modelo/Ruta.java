package org.gps.gpsproject.modelo;

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
