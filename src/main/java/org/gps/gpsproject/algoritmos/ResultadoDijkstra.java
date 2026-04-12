package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.modelo.Parada;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * Encapsula el resultado de una ejecución del algoritmo de Dijkstra.
 *
 * Almacena las distancias mínimas desde el origen hasta cada parada
 * alcanzable, y el mapa de predecesores necesario para reconstruir
 * el camino óptimo hacia cualquier destino.
 */
public class ResultadoDijkstra {

    /* Mapa de distancias mínimas: parada → costo acumulado desde el origen. */
    private final Map<Parada, Double> aristas;

    /* Mapa de predecesores: parada → parada desde la que se llegó con menor costo. */
    private final Map<Parada, Parada> nodos;

    /* Parada desde la que se ejecutó Dijkstra. */
    private final Parada origen;

    /*
     * Construye un resultado de Dijkstra con los mapas de distancias y predecesores.
     *
     * Complejidad:
     *   Big O: O(1) — solo asignación de referencias.
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     * </ul>
     *
     * param aristas Mapa parada → distancia mínima desde el origen.
     * param nodos   Mapa parada → predecesor en el camino óptimo.
     * param origen  Parada de inicio del algoritmo.
     */
    public ResultadoDijkstra(Map<Parada, Double> aristas,
                             Map<Parada, Parada> nodos,
                             Parada origen) {
        this.aristas = aristas;
        this.nodos = nodos;
        this.origen = origen;
    }

    /*
     * Reconstruye el camino óptimo desde el origen hasta la parada {@code destino}
     * recorriendo el mapa de predecesores en sentido inverso.
     *
     * Si el destino no es alcanzable (distancia infinita), retorna una lista vacía.
     *
     * Complejidad:
     *   Big O: O(V) — en el peor caso el camino pasa por todos los nodos.
     *   Theta: Θ(k) — donde k es la longitud real del camino encontrado.
     *   Omega: Ω(1) — si el destino no es alcanzable, retorna inmediatamente.
     *
     * param destino La parada a la que se quiere llegar.
     * return Lista ordenada de paradas desde el origen hasta el destino,
     *         o lista vacía si no hay camino.
     */
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

    /*
     * Retorna la distancia mínima calculada desde el origen hasta {@code destino}.
     *
     * Complejidad:
     *   Big O: O(1) — acceso directo al mapa hash.
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     *
     * param destino La parada de la que se quiere conocer el costo mínimo.
     * return El costo mínimo acumulado, o Double.MAX_VALUE si no es alcanzable.
     */
    public double getDistancia(Parada destino) {
        return aristas.getOrDefault(destino, Double.MAX_VALUE);
    }

    /*
     * Retorna una vista no modificable del mapa completo de distancias mínimas
     * desde el origen hacia todas las paradas del grafo.
     *
     * Complejidad:
     *   Big O: O(1) — solo crea una vista envolvente del mapa existente.
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * return Mapa inmutable de parada → distancia mínima.
     */
    public Map<Parada, Double> getTodasDistancias() {
        return Collections.unmodifiableMap(aristas);
    }
}