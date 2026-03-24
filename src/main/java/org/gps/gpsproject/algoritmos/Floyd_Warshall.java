package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Criterio;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.util.*;

public class Floyd_Warshall {

    /**
     * Ejecuta Floyd-Warshall sobre el grafo completo.
     *
     * @param grafo    El grafo de transporte
     * @param criterio Qué peso minimizar (TIEMPO, COSTO, DISTANCIA, TRANSBORDOS)
     * @return ResultadoFloyd con las matrices de distancias y predecesores
     */
    public static ResultadoFloyd floyd(GrafoTransporte grafo, Criterio criterio) {

        List<Parada> paradas = new ArrayList<>(grafo.getGrafo().keySet());

        // Inicializar matrices
        Map<Parada, Map<Parada, Double>> dist = new HashMap<>();
        Map<Parada, Map<Parada, Parada>> pred = new HashMap<>();

        for (Parada p : paradas) {
            dist.put(p, new HashMap<>());
            pred.put(p, new HashMap<>());

            for (Parada q : paradas) {
                dist.get(p).put(q, p.equals(q) ? 0.0 : Double.MAX_VALUE);
                pred.get(p).put(q, null);
            }
        }

        // Cargar aristas directas
        for (Parada origen : paradas) {
            for (Ruta ruta : grafo.getVecinos(origen)) {
                Parada destino = ruta.getDestino();
                double peso    = getPeso(ruta, criterio);

                // Solo actualiza si mejora (puede haber aristas paralelas)
                if (peso < dist.get(origen).get(destino)) {
                    dist.get(origen).put(destino, peso);
                    pred.get(origen).put(destino, destino);
                }
            }
        }

        // Relajación triple: para cada nodo intermedio k
        for (Parada k : paradas) {
            for (Parada i : paradas) {
                double distIK = dist.get(i).get(k);
                if (distIK == Double.MAX_VALUE) continue; // evita overflow

                for (Parada j : paradas) {
                    double distKJ = dist.get(k).get(j);
                    if (distKJ == Double.MAX_VALUE) continue;

                    double nueva = distIK + distKJ;

                    if (nueva < dist.get(i).get(j)) {
                        dist.get(i).put(j, nueva);
                        pred.get(i).put(j, pred.get(i).get(k));
                    }
                }
            }
        }

        return new ResultadoFloyd(dist, pred, paradas);
    }

    /**
     * Atajo: devuelve directamente el camino más corto entre dos paradas.
     */
    public static List<Parada> caminoMasCorto(GrafoTransporte grafo,
                                              Parada origen,
                                              Parada destino,
                                              Criterio criterio) {
        return floyd(grafo, criterio).getCamino(origen, destino);
    }

    // -------------------------------------------------------------------------

    private static double getPeso(Ruta ruta, Criterio criterio) {
        return switch (criterio) {
            case TIEMPO      -> ruta.getTiempo();
            case COSTO       -> ruta.getCosto();
            case DISTANCIA   -> ruta.getDistancia();
            case TRANSBORDOS -> ruta.getTransbordo();
        };
    }
}