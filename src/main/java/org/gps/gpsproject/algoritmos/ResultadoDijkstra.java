package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.modelo.Parada;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResultadoDijkstra {
    private final Map<Parada, Double> aristas;
    private final Map<Parada, Parada> nodos;
    private final Parada origen;

    public ResultadoDijkstra(Map<Parada, Double> aristas,
                             Map<Parada, Parada> nodos,
                             Parada origen) {
        this.aristas = aristas;
        this.nodos = nodos;
        this.origen = origen;
    }

    // Reconstruye el camino desde el origen hasta el destino
    public List<Parada> getCaminos(Parada destino) {
        List<Parada> camino = new ArrayList<>();

        if (!aristas.containsKey(destino) || aristas.get(destino) == Double.MAX_VALUE) {
            return camino; // No hay camino
        }

        for (Parada actual = destino; actual != null; actual = nodos.get(actual)) {
            camino.add(0, actual); // Inserta al inicio para obtener el orden correcto
        }

        return camino;
    }

    public double getDistancia(Parada destino) {
        return aristas.getOrDefault(destino, Double.MAX_VALUE);
    }

    public Map<Parada, Double> getTodasDistancias() {
        return Collections.unmodifiableMap(aristas);
    }
}
