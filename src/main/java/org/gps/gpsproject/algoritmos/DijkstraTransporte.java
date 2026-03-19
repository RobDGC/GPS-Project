package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;
import java.util.*;
import org.gps.gpsproject.modelo.Criterio;

public class DijkstraTransporte {

    // Nodo auxiliar para la cola de prioridad
    private static class NodoPrioridad implements Comparable<NodoPrioridad> {
        Parada parada;
        double peso;

        NodoPrioridad(Parada parada, double peso) {
            this.parada = parada;
            this.peso = peso;
        }

        @Override
        public int compareTo(NodoPrioridad otro) {
            return Double.compare(this.peso, otro.peso);
        }
    }

    /*
     Ejecuta Dijkstra desde una parada origen sobre el grafo dado.
     Parametros:
     grafo    El grafo de transporte
     origen   Parada de inicio
     criterio Qué peso usar para minimizar (TIEMPO, COSTO, DISTANCIA, TRANSBORDOS)
     Return ResultadoDijkstra con distancias y predecesores
     */
    public static ResultadoDijkstra dijkstra(GrafoTransporte grafo, Parada origen, Criterio criterio) {
        Map<Parada, Double> aristas = new HashMap<>();
        Map<Parada, Parada> nodos = new HashMap<>();
        PriorityQueue<NodoPrioridad> colaPrioridad = new PriorityQueue<>();

        for (Parada parada : grafo.getGrafo().keySet()) {
            aristas.put(parada, Double.MAX_VALUE);
        }

        aristas.put(origen, 0.0);
        colaPrioridad.add(new NodoPrioridad(origen, 0));

        while (!colaPrioridad.isEmpty()) {
            NodoPrioridad nodoActual = colaPrioridad.poll();
            Parada actual = nodoActual.parada;

            // Si encontramos un costo mayor al registrado, ignoramos este nodo (ya fue procesado)
            if (nodoActual.peso > aristas.get(actual)) continue;

            // Recorre todos los vecinos (rutas) del nodo actual
            for (Ruta ruta : grafo.getVecinos(actual)) {
                Parada vecino = ruta.getDestino();
                double peso = getPeso(ruta, criterio);

                // Verifica que el vecino esté en el grafo (puede haberse añadido después)
                aristas.putIfAbsent(vecino, Double.MAX_VALUE);

                double nuevaDistancia = aristas.get(actual) + peso;

                // Relajación: actualiza si encontramos un camino más corto
                if (nuevaDistancia < aristas.get(vecino)) {
                    aristas.put(vecino, nuevaDistancia);
                    nodos.put(vecino, actual);
                    colaPrioridad.add(new NodoPrioridad(vecino, nuevaDistancia));
                }
            }
        }

        return new ResultadoDijkstra(aristas, nodos, origen);
    }

    // Selecciona el peso de la ruta segun el criterio elegido
    private static double getPeso(Ruta ruta, Criterio criterio) {
        return switch (criterio) {
            case TIEMPO      -> ruta.getTiempo();
            case COSTO       -> ruta.getCosto();
            case DISTANCIA   -> ruta.getDistancia();
            case TRANSBORDOS -> ruta.getTransbordo();
        };
    }

    //  Manda la lista de las paradas mas rapidas segun el peso.
    public static List<Parada> caminoMasCorto(GrafoTransporte grafo,
                                              Parada origen,
                                              Parada destino,
                                              Criterio criterio) {
        ResultadoDijkstra resultado = dijkstra(grafo, origen, criterio);
        return resultado.getCaminos(destino);
    }
}