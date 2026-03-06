package org.example.gpsproject.prueba;

import org.example.gpsproject.grafo.GrafoTransporte;
import org.example.gpsproject.modelo.Parada;

public class DatosDePrueba {
    public static void main(String[] args) {
        GrafoTransporte aux = new GrafoTransporte();

        Parada a = new Parada("A","Centro");
        Parada b = new Parada("B","Hospital");
        Parada c = new Parada("C","Universidad");

        aux.addRuta(a,b,10,20,5,0);
        aux.addRuta(b,c,5,10,2,0);
        aux.addRuta(a,c,20,30,10,1);
    }

}
