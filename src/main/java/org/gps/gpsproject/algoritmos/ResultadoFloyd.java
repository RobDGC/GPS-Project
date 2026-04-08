package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.modelo.Parada;

import java.util.*;

/*
 * Encapsula el resultado de una ejecución del algoritmo de Floyd-Warshall.
 *
 * Almacena las matrices de distancias mínimas y de siguientes nodos
 * para todos los pares de paradas del grafo, permitiendo reconstruir
 * el camino óptimo entre cualquier par origen-destino en tiempo O(V).
 */
public class ResultadoFloyd {

    /* Matriz de distancias mínimas: dist[i][j] = costo mínimo de i a j. */
    private final Map<Parada, Map<Parada, Double>> aristas;

    /* Matriz de siguientes nodos: next[i][j] = primer paso desde i hacia j en el camino óptimo. */
    private final Map<Parada, Map<Parada, Parada>> nodos;

    /* Lista de todas las paradas del grafo en el momento de la ejecución. */
    private final List<Parada> paradas;

    /*
     * Construye el resultado de Floyd-Warshall con las matrices ya calculadas.
     *
     * Complejidad: 
     *   Big O: O(1) — solo asignación de referencias.
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @param aristas  Matriz de distancias mínimas entre todos los pares.
     * @param nodos    Matriz de siguientes nodos para reconstruir caminos.
     * @param paradas Lista de paradas incluidas en el cálculo.
     */
    public ResultadoFloyd(Map<Parada, Map<Parada, Double>> aristas,
                          Map<Parada, Map<Parada, Parada>> nodos,
                          List<Parada> paradas) {
        this.aristas = aristas;
        this.nodos = nodos;
        this.paradas = paradas;
    }

    /*
     * Retorna el costo mínimo calculado entre Parada origen} y Parada destino}.
     *
     * Complejidad:
     *   Big O: O(1) — dos accesos a mapas hash.
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @param origen  Parada de inicio.
     * @param destino Parada de llegada.
     * @return El costo mínimo acumulado, o Double.MAX_VALUE si no hay camino.
     */
    public double getDistancia(Parada origen, Parada destino) {
        return aristas.getOrDefault(origen, Collections.emptyMap())
                .getOrDefault(destino, Double.MAX_VALUE);
    }

    /*
     * Reconstruye el camino óptimo entre {@code origen} y {@code destino}
     * siguiendo la matriz de siguientes nodos de forma iterativa.
     *
     * Si no existe camino entre ambas paradas, retorna una lista vacía.
     *
     * Complejidad:
     *   Big O: O(V) — en el peor caso el camino pasa por todos los vértices.
     *   Theta: Θ(k) — donde k es la longitud real del camino reconstruido.
     *   Omega: Ω(1) — si no existe camino, retorna inmediatamente.
     *
     * @param origen  Parada de inicio del camino.
     * @param destino Parada de destino del camino.
     * @return Lista ordenada de paradas desde origen hasta destino,
     *         o lista vacía si no existe camino.
     */
    public List<Parada> getCamino(Parada origen, Parada destino) {
        if (getDistancia(origen, destino) == Double.MAX_VALUE) {
            return Collections.emptyList();
        }

        List<Parada> camino = new ArrayList<>();
        Parada actual = origen;

        while (!actual.equals(destino)) {
            camino.add(actual);
            actual = nodos.getOrDefault(actual, Collections.emptyMap()).get(destino);

            if (actual == null) {
                return Collections.emptyList();
            }
        }

        camino.add(destino);
        return camino;
    }

    /*
     * Retorna una vista no modificable de la lista de paradas incluidas
     * en el cálculo de Floyd-Warshall.
     *
     * Complejidad:
     *   Big O: O(1)
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @return Lista inmutable de paradas del grafo.
     */
    public List<Parada> getParadas() {
        return Collections.unmodifiableList(paradas);
    }

    /*
     * Retorna una vista no modificable de la matriz completa de distancias mínimas.
     *
     * Complejidad:
     *   Big O: O(1) — solo crea una vista envolvente del mapa existente.
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @return Mapa inmutable de parada → (parada → distancia mínima).
     */
    public Map<Parada, Map<Parada, Double>> getTodasDistancias() {
        return Collections.unmodifiableMap(aristas);
    }
}