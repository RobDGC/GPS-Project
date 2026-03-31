package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.modelo.Parada;

import java.util.*;

public class ResultadoFloyd {

    private final Map<Parada, Map<Parada, Double>> dist;
    private final Map<Parada, Map<Parada, Parada>> next; // next[i][j] = siguiente nodo desde i hacia j
    private final List<Parada>                      paradas;

    public ResultadoFloyd(Map<Parada, Map<Parada, Double>> dist,
                          Map<Parada, Map<Parada, Parada>> next,
                          List<Parada> paradas) {
        this.dist    = dist;
        this.next    = next;
        this.paradas = paradas;
    }

    /*
     * Devuelve el costo mínimo entre origen y destino,
     * o Double.MAX_VALUE si no existe camino.
     */
    public double getDistancia(Parada origen, Parada destino) {
        return dist.getOrDefault(origen, Collections.emptyMap())
                .getOrDefault(destino, Double.MAX_VALUE);
    }

    /*
     * Reconstruye el camino óptimo entre origen y destino de forma iterativa.
     * Devuelve lista vacía si no hay camino.
     */
    public List<Parada> getCamino(Parada origen, Parada destino) {
        if (getDistancia(origen, destino) == Double.MAX_VALUE) {
            return Collections.emptyList();
        }

        List<Parada> camino = new ArrayList<>();
        Parada actual = origen;

        while (!actual.equals(destino)) {
            camino.add(actual);
            actual = next.getOrDefault(actual, Collections.emptyMap()).get(destino);

            if (actual == null) {
                // No hay camino válido
                return Collections.emptyList();
            }
        }

        camino.add(destino);
        return camino;
    }

    public List<Parada> getParadas() {
        return Collections.unmodifiableList(paradas);
    }

    /* Toda la matriz de distancias (solo lectura). */
    public Map<Parada, Map<Parada, Double>> getTodasDistancias() {
        return Collections.unmodifiableMap(dist);
    }
}