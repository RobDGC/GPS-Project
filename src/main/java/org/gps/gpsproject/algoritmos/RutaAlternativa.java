package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.util.*;

/*
 * Proporciona un algoritmo alternativo de búsqueda de rutas basado en BFS
 *
 * A diferencia de Dijkstra, Bellman-Ford y Floyd-Warshall que minimizan
 * un criterio de peso, este algoritmo encuentra el camino con la
 * menor cantidad de paradas intermedias, ignorando completamente
 * los pesos de las aristas.
 *
 * Es útil cuando el usuario prefiere el camino más directo en términos
 * de trasbordos o paradas, sin importar tiempo, costo o distancia.
 *
 * Complejidad general del algoritmo:
 *   Big O: O(V + E) — BFS visita cada vértice y arista como máximo una vez.
 *   Theta: Θ(V + E)
 *   Omega: Ω(1) — si origen y destino son adyacentes directamente.
 * 
 * Donde V = número de paradas y E = número de rutas.
 */
public class RutaAlternativa {

    /*
     * Encuentra el camino con la menor cantidad de paradas entre Parada origen
     * y Parada destino usando BFS sobre el grafo de transporte.
     *
     * BFS garantiza que el primer camino encontrado hacia el destino
     * es el de menor número de saltos (paradas intermedias), ya que explora
     * los nodos por niveles de distancia creciente.
     *
     * Si origen y destino son iguales, retorna inmediatamente una lista vacía.
     *
     * Complejidad:
     *   Big O: O(V + E) — cada parada se encola como máximo una vez y cada
     *       ruta se examina como máximo una vez al procesar su parada origen.
     *   Theta: Θ(V + E) — en grafos conectados el BFS siempre recorre
     *       el conjunto completo de vértices y aristas alcanzables.
     *   Omega: Ω(1) — si origen == destino se retorna de inmediato sin
     *       recorrer el grafo.
     *
     * param grafo   El grafo de transporte sobre el que se ejecuta BFS.
     * param origen  Parada desde donde inicia la búsqueda.
     * param destino Parada que se desea alcanzar.
     * return Lista ordenada de paradas desde origen hasta destino con
     *         el mínimo número de saltos, o lista vacía si no existe camino
     *         o si origen y destino son la misma parada.
     */
    public static List<Parada> caminoMenosParadas(GrafoTransporte grafo,
                                                  Parada origen,
                                                  Parada destino) {
        if (origen.equals(destino)) return Collections.emptyList();

        Queue<Parada> cola = new LinkedList<>();
        Map<Parada, Parada> padre = new HashMap<>();
        Set<Parada> visitados = new HashSet<>();

        cola.add(origen);
        visitados.add(origen);
        padre.put(origen, null);

        while (!cola.isEmpty()) {
            Parada actual = cola.poll();

            for (Ruta ruta : grafo.getVecinos(actual)) {
                Parada vecino = ruta.getDestino();

                if (visitados.contains(vecino)) continue;

                visitados.add(vecino);
                padre.put(vecino, actual);

                if (vecino.equals(destino)) {
                    return reconstruir(padre, origen, destino);
                }

                cola.add(vecino);
            }
        }

        return Collections.emptyList();
    }

    /*
     * Reconstruye el camino desde Parada origen hasta Parada destino
     * siguiendo el mapa de padres generado por BFS en sentido inverso.
     *
     * Complejidad:
     *   Big O: O(V) — en el peor caso el camino pasa por todos los vértices.
     *   Theta: Θ(k) — donde k es la longitud real del camino encontrado.
     *   Omega: Ω(1) — si origen y destino son adyacentes, el camino tiene longitud 2.
     *
     * param padre   Mapa de predecesores generado por BFS (parada → parada desde la que se llegó).
     * param origen  Parada de inicio del camino.
     * param destino Parada de destino del camino.
     * return Lista ordenada de paradas del camino desde origen hasta destino.
     */
    private static List<Parada> reconstruir(Map<Parada, Parada> padre,
                                            Parada origen,
                                            Parada destino) {
        List<Parada> camino = new ArrayList<>();
        for (Parada p = destino; p != null; p = padre.get(p)) {
            camino.add(0, p);
        }
        return camino;
    }
}