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
        return switch (FiltroActual.getFiltro()) {
            case "Distancia"  -> distancia + " km";
            case "Costo"      -> "$" + costo;
            case "Transbordo" -> transbordo + " transbordos";
            default           -> tiempo + " min";
        };
    }

    public Parada getDestino() { return destino; }
    public int getTiempo()     { return tiempo; }
    public int getDistancia()  { return distancia; }
    public int getCosto()      { return costo; }
    public int getTransbordo() { return transbordo; }
}