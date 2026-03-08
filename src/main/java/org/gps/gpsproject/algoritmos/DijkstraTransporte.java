package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.util.*;

public class DijkstraTransporte {

    // Clase interna para almacenar el resultado del algoritmo
    public static class ResultadoDijkstra {
        private final Map<Parada, Integer> distancias;
        private final Map<Parada, Parada> predecesores;
        private final Parada origen;

        public ResultadoDijkstra(Map<Parada, Integer> distancias,
                                 Map<Parada, Parada> predecesores,
                                 Parada origen) {
            this.distancias = distancias;
            this.predecesores = predecesores;
            this.origen = origen;
        }

        // Reconstruye el camino desde el origen hasta el destino
        public List<Parada> getCamino(Parada destino) {
            List<Parada> camino = new ArrayList<>();

            if (!distancias.containsKey(destino) || distancias.get(destino) == Integer.MAX_VALUE) {
                return camino; // No hay camino
            }

            for (Parada actual = destino; actual != null; actual = predecesores.get(actual)) {
                camino.add(0, actual); // Inserta al inicio para obtener el orden correcto
            }

            return camino;
        }

        public int getDistancia(Parada destino) {
            return distancias.getOrDefault(destino, Integer.MAX_VALUE);
        }

        public Map<Parada, Integer> getTodasDistancias() {
            return Collections.unmodifiableMap(distancias);
        }
    }

    // Nodo auxiliar para la cola de prioridad
    private static class NodoPrioridad implements Comparable<NodoPrioridad> {
        Parada parada;
        int costo;

        NodoPrioridad(Parada parada, int costo) {
            this.parada = parada;
            this.costo = costo;
        }

        @Override
        public int compareTo(NodoPrioridad otro) {
            return Integer.compare(this.costo, otro.costo);
        }
    }

    // Enum para elegir qué métrica optimizar
    public enum Criterio {
        TIEMPO,
        COSTO,
        DISTANCIA,
        TRANSBORDOS
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
        Map<Parada, Integer> distancias = new HashMap<>();
        Map<Parada, Parada> predecesores = new HashMap<>();
        PriorityQueue<NodoPrioridad> colaPrioridad = new PriorityQueue<>();

        for (Parada parada : grafo.getGrafo().keySet()) {
            distancias.put(parada, Integer.MAX_VALUE);
        }

        distancias.put(origen, 0);
        colaPrioridad.add(new NodoPrioridad(origen, 0));

        while (!colaPrioridad.isEmpty()) {
            NodoPrioridad nodoActual = colaPrioridad.poll();
            Parada actual = nodoActual.parada;

            // Si encontramos un costo mayor al registrado, ignoramos este nodo (ya fue procesado)
            if (nodoActual.costo > distancias.get(actual)) continue;

            // Recorre todos los vecinos (rutas) del nodo actual
            for (Ruta ruta : grafo.getVecinos(actual)) {
                Parada vecino = ruta.getDestino();
                int peso = getPeso(ruta, criterio);

                // Verifica que el vecino esté en el grafo (puede haberse añadido después)
                distancias.putIfAbsent(vecino, Integer.MAX_VALUE);

                int nuevaDistancia = distancias.get(actual) + peso;

                // Relajación: actualiza si encontramos un camino más corto
                if (nuevaDistancia < distancias.get(vecino)) {
                    distancias.put(vecino, nuevaDistancia);
                    predecesores.put(vecino, actual);
                    colaPrioridad.add(new NodoPrioridad(vecino, nuevaDistancia));
                }
            }
        }

        return new ResultadoDijkstra(distancias, predecesores, origen);
    }

    // Selecciona el peso de la ruta segun el criterio elegido
    private static int getPeso(Ruta ruta, Criterio criterio) {
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
        return resultado.getCamino(destino);
    }
}