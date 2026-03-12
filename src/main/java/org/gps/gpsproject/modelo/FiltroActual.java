package org.gps.gpsproject.modelo;

public class FiltroActual {
    private static String filtro = "Tiempo";

    public static void setFiltro(String f) {
        filtro = f;
    }

    public static String getFiltro() {
        return filtro;
    }
}