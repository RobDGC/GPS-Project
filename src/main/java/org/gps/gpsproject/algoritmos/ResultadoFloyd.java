package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.modelo.Parada;

import java.util.*;

public class ResultadoFloyd {

    private final Map<Parada, Map<Parada, Double>> dist;
    private final Map<Parada, Map<Parada, Parada>> pred;
    private final List<Parada>                      paradas;

    public ResultadoFloyd(Map<Parada, Map<Parada, Double>> dist,
                          Map<Parada, Map<Parada, Parada>> pred,
                          List<Parada> paradas) {
        this.dist    = dist;
        this.pred    = pred;
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
     * Reconstruye la lista de paradas del camino óptimo entre origen y destino.
     * Devuelve una lista vacía si no hay camino.
     */
    public List<Parada> getCamino(Parada origen, Parada destino) {
        if (getDistancia(origen, destino) == Double.MAX_VALUE) {
            return Collections.emptyList();
        }
        return reconstruir(origen, destino, new ArrayList<>());
    }

    private List<Parada> reconstruir(Parada origen, Parada destino, List<Parada> camino) {
        if (origen.equals(destino)) {
            camino.add(origen);
            return camino;
        }

        Parada predecesor = pred.getOrDefault(origen, Collections.emptyMap()).get(destino);

        if (predecesor == null) {
            return Collections.emptyList();
        }

        camino = reconstruir(origen, predecesor, camino);
        if (camino.isEmpty()) return camino;
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