package org.gps.gpsproject.prueba;

import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;

public class DatosDePrueba {

    public static void cargarDatos() {
        GrafoTransporte grafo = GrafoTransporte.getInstance();

        Parada norte      = grafo.addParada("Norte");
        Parada centro     = grafo.addParada("Centro");
        Parada este       = grafo.addParada("Este");
        Parada oeste      = grafo.addParada("Oeste");
        Parada sur        = grafo.addParada("Sur");
        Parada aeropuerto = grafo.addParada("Aeropuerto");
        Parada terminal   = grafo.addParada("Terminal");


        grafo.addRuta(norte, oeste, 10, 80, 12, 0);
        grafo.addRuta(norte,  centro, 15, 20, 10, 0);
        grafo.addRuta(centro, oeste,  12, 15, 9,  1);
        grafo.addRuta(norte,    terminal, 20, 30, 18, 0);
        grafo.addRuta(terminal, oeste,    18, 25, 14, 1);
        grafo.addRuta(norte,      este,       18, 22, 12, 1);
        grafo.addRuta(este,       centro,     10, 15,  8, 0);
        grafo.addRuta(este,       aeropuerto, 35, 50, 30, 2);
        grafo.addRuta(centro,     sur,        20, 25, 15, 0);
        grafo.addRuta(sur,        oeste,      15, 20, 11, 1);
        grafo.addRuta(sur,        terminal,   20, 30, 18, 0);
        grafo.addRuta(aeropuerto, terminal,   10, 15,  7, 0);
        grafo.addRuta(terminal,   centro,     40, 45, 35, 2);
        grafo.addRuta(oeste,      sur,        14, 18, 10, 0);
        grafo.addRuta(centro,     aeropuerto, 30, 60, 25, 1);
    }
}