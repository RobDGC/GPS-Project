package org.gps.gpsproject.modelo;

import java.util.Objects;

public class Ruta {
    private Parada destino;
    private double tiempo;
    private double distancia;
    private double costo;
    private int transbordo;

    public Ruta(Parada destino, double tiempo, double distancia, double costo, int transbordo) {
        this.destino = destino;
        this.tiempo = tiempo;
        this.distancia = distancia;
        this.costo = costo;
        this.transbordo = transbordo;
    }

    @Override
    public String toString() {
        return switch (FiltroActual.getFiltro()) {
            case "Distancia"  -> distancia + " km";
            case "Costo"      -> "$" + costo;
            case "Transbordo" -> transbordo + " transbordos";
            default           -> tiempo + " min";
        };
    }

    public Parada getDestino() { return destino; }
    public double getTiempo()     { return tiempo; }
    public double getDistancia()  { return distancia; }
    public double getCosto()      { return costo; }
    public int getTransbordo() { return transbordo; }
}