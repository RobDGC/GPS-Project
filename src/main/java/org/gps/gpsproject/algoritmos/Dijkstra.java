package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;
import java.util.*;
import org.gps.gpsproject.modelo.Criterio;

/**
 * Implementación del algoritmo de Dijkstra para encontrar el camino más corto
 * en el grafo de transporte.
 *
 * <p>Dijkstra funciona correctamente únicamente cuando todos los pesos de las
 * aristas son no negativos. Si existen costos negativos, se debe usar
 * {@link BellmanFord} en su lugar.</p>
 *
 * <p><b>Complejidad general del algoritmo:</b></p>
 * <ul>
 *   <li>Big O (peor caso):   O((V + E) log V)</li>
 *   <li>Big Θ (caso promedio): Θ((V + E) log V)</li>
 *   <li>Big Ω (mejor caso):  Ω(V log V)</li>
 * </ul>
 * <p>Donde V = número de paradas (vértices) y E = número de rutas (aristas).</p>
 */
public class Dijkstra {

    /*
     * Nodo auxiliar interno usado en la cola de prioridad.
     * Asocia una parada con su costo acumulado para permitir la comparación
     * durante el procesamiento de la cola.
     *
     * Complejidad de compareTo:
     *   Big O: O(1)
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     */
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
     * Ejecuta el algoritmo de Dijkstra desde una parada origen sobre el grafo dado,
     * calculando el camino de menor costo hacia todas las demás paradas.
     *
     * El criterio determina qué atributo de la arista se usa como peso:
     * tiempo, costo, distancia o número de transbordos.</p>
     *
     * Complejidad:
     *   Big O: O((V + E) log V) — cada arista se relaja y cada vértice
     *       se extrae de la cola de prioridad como máximo una vez.
     *   Theta: Θ((V + E) log V) — en grafos densos conectados el comportamiento
     *       es consistente.
     *   Omega: Ω(V log V) — incluso en el mejor caso se deben extraer
     *       todos los vértices de la cola.
     *
     * @param grafo    El grafo de transporte sobre el que se ejecuta el algoritmo.
     * @param origen   Parada desde donde inicia la búsqueda.
     * @param criterio Criterio de peso a minimizar (TIEMPO, COSTO, DISTANCIA, TRANSBORDOS).
     * @return ResultadoDijkstra con las distancias mínimas y los predecesores
     *         de cada parada para reconstruir el camino.
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

    /*
     * Selecciona y retorna el peso de una ruta según el criterio indicado.
     *
     * Complejidad:
     *   Big O: O(1)
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @param ruta     La ruta de la que se extrae el peso.
     * @param criterio Criterio que determina qué atributo usar como peso.
     * @return El valor numérico del peso correspondiente al criterio.
     */
    private static double getPeso(Ruta ruta, Criterio criterio) {
        return switch (criterio) {
            case TIEMPO      -> ruta.getTiempo();
            case COSTO       -> ruta.getCosto();
            case DISTANCIA   -> ruta.getDistancia();
            case TRANSBORDOS -> ruta.getTransbordo();
        };
    }

    /*
     * Calcula y retorna la lista ordenada de paradas que forman el camino
     * más corto entre Parada origen y Parada destino según el criterio dado.
     *
     * Internamente ejecuta dijkstra y delega la reconstrucción
     * del camino a ResultadoDijkstra.getCaminos(Parada)}.
     *
     * <p><b>Complejidad:</b></p>
     *   <li>Big O: O((V + E) log V) — dominado por la ejecución de Dijkstra.</li>
     *   Theta: Θ((V + E) log V)
     *   Omega: Ω(V log V)
     *
     * @param grafo    El grafo de transporte.
     * @param origen   Parada de inicio del camino.
     * @param destino  Parada de destino del camino.
     * @param criterio Criterio de peso a minimizar.
     * @return Lista de paradas en orden desde origen hasta destino,
     *         o una lista vacía si no existe camino.
     */
    public static List<Parada> caminoMasCorto(GrafoTransporte grafo,
                                              Parada origen,
                                              Parada destino,
                                              Criterio criterio) {
        ResultadoDijkstra resultado = dijkstra(grafo, origen, criterio);
        return resultado.getCaminos(destino);
    }
}