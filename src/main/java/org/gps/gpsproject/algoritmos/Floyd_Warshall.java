package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Criterio;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.util.*;

/*
 * Implementación del algoritmo de Floyd-Warshall para encontrar los caminos
 * más cortos entre todos los pares de paradas del grafo de transporte.
 *
 * A diferencia de Dijkstra y Bellman-Ford, Floyd-Warshall calcula las rutas
 * óptimas entre todos los pares de vértices en una sola ejecución,
 * lo que lo hace ideal cuando se necesitan múltiples consultas de origen-destino.
 *
 * Soporta pesos negativos pero no ciclos negativos.
 *
 * Complejidad general del algoritmo:
 * 
 *   Big O: O(V³) — triple bucle anidado sobre todos los vértices.
 *   Theta: Θ(V³) — siempre ejecuta el mismo número de iteraciones.
 *   Omega: Ω(V³) — no existen atajos; siempre recorre el triple bucle completo.
 * 
 * Donde V = número de paradas (vértices) del grafo.
 */
public class Floyd_Warshall {

    /*
     * Ejecuta el algoritmo de Floyd-Warshall sobre el grafo completo,
     * calculando el camino de menor costo entre todos los pares de paradas.
     *
     * El proceso se divide en tres fases:
     *
     *   Inicialización de matrices de aristasancias Map aristas y siguientes
     *       nodos Map nodos con valores infinitos y nulos respectivamente.
     *   Carga de las aristas directas del grafo en las matrices.
     *   Relajación triple: para cada nodo intermedio k, se actualiza
     *       la distancia entre i y j si pasar por k es más corto.
     * 
     *
     * Complejidad:
     *   Big O: O(V³) — el triple bucle de relajación domina el tiempo de ejecución.
     *   Theta: Θ(V³) — el número de iteraciones es fijo independientemente del grafo.
     *   Omega: Ω(V³) — no existe condición de parada temprana; siempre completa las V³ iteraciones.
     * 
     * Uso de memoria: O(V²) para las matrices {@code aristas} y {@code nodos}.
     *
     * param grafo El grafo de transporte sobre el que se ejecuta el algoritmo.
     * param criterio Criterio de peso a minimizar (TIEMPO, COSTO, DISTANCIA, TRANSBORDOS).
     * return ResultadoFloyd con las matrices de distancias mínimas y predecesores
     *         para todos los pares de paradas.
     */
    public static ResultadoFloyd floyd(GrafoTransporte grafo, Criterio criterio) {

        List<Parada> paradas = new ArrayList<>(grafo.getGrafo().keySet());

        Map<Parada, Map<Parada, Double>> aristas = new HashMap<>();
        Map<Parada, Map<Parada, Parada>> nodos = new HashMap<>();

        // Inicializar matrices
        for (Parada p : paradas) {
            aristas.put(p, new HashMap<>());
            nodos.put(p, new HashMap<>());

            for (Parada q : paradas) {
                aristas.get(p).put(q, p.equals(q) ? 0.0 : Double.MAX_VALUE);
                nodos.get(p).put(q, null);
            }
        }

        // Cargar aristas directas — nodos[origen][destino] = destino (salto directo)
        for (Parada origen : paradas) {
            for (Ruta ruta : grafo.getVecinos(origen)) {
                Parada destino = ruta.getDestino();
                double peso    = getPeso(ruta, criterio);

                if (peso < aristas.get(origen).get(destino)) {
                    aristas.get(origen).put(destino, peso);
                    nodos.get(origen).put(destino, destino);
                }
            }
        }

        // Relajación triple: para cada nodo intermedio k
        for (Parada k : paradas) {
            for (Parada i : paradas) {
                double aristasIK = aristas.get(i).get(k);
                if (aristasIK == Double.MAX_VALUE) continue;

                for (Parada j : paradas) {
                    double aristasKJ = aristas.get(k).get(j);
                    if (aristasKJ == Double.MAX_VALUE) continue;

                    double nueva = aristasIK + aristasKJ;

                    if (nueva < aristas.get(i).get(j)) {
                        aristas.get(i).put(j, nueva);
                        nodos.get(i).put(j, nodos.get(i).get(k));
                    }
                }
            }
        }

        return new ResultadoFloyd(aristas, nodos, paradas);
    }

    /*
     * Atajo que ejecuta Floyd-Warshall y retorna directamente el camino
     * más corto entre Parada origen y Parada destino.
     *
     * Este metodo es conveniente para consultas únicas, pero si se necesitan
     * múltiples consultas es más eficiente llamar a {@link #floyd} una sola vez
     * y reutilizar el {@link ResultadoFloyd}.
     *
     * Complejidad:
     *   Big O: O(V³) — dominado por la ejecución completa de Floyd-Warshall.
     *   Theta: Θ(V³)
     *   Omega: Ω(V³)
     *
     * param grafo    El grafo de transporte.
     * param origen   Parada de inicio del camino.
     * param destino  Parada de destino del camino.
     * param criterio Criterio de peso a minimizar.
     * return Lista ordenada de paradas desde origen hasta destino,
     *         o lista vacía si no existe camino.
     */
    public static List<Parada> caminoMasCorto(GrafoTransporte grafo,
                                              Parada origen,
                                              Parada destino,
                                              Criterio criterio) {
        return floyd(grafo, criterio).getCamino(origen, destino);
    }

    /*
     * Selecciona y retorna el peso de una ruta según el criterio indicado.
     *
     * Complejidad:
     *   Big O: O(1)
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     * 
     * param ruta     La ruta de la que se extrae el peso.
     * param criterio Criterio que determina qué atributo usar como peso.
     * return El valor numérico del peso correspondiente al criterio.
     */
    private static double getPeso(Ruta ruta, Criterio criterio) {
        return switch (criterio) {
            case TIEMPO      -> ruta.getTiempo();
            case COSTO       -> ruta.getCosto();
            case DISTANCIA   -> ruta.getDistancia();
            case TRANSBORDOS -> ruta.getTransbordo();
        };
    }
}