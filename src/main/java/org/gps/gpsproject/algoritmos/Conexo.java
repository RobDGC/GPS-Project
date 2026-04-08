package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.util.*;

/*
 * Verifica si el grafo de transporte es fuertemente conexo.
 *
 * Un grafo dirigido es fuertemente conexo cuando existe un camino
 * dirigido entre cualquier par de vértices en ambas direcciones. Esto garantiza
 * que desde cualquier parada se puede llegar a cualquier otra parada de la red.
 *
 * La verificación se realiza en dos pasos mediante DFS (búsqueda en profundidad):
 * 
 *   DFS sobre el grafo original para comprobar que todos los nodos son alcanzables
 *       desde el nodo de origen.
 *   DFS sobre el grafo con aristas invertidas para comprobar que todos los nodos
 *       pueden llegar al nodo de origen.
 *
 * Complejidad general:
 *   Big O: O(V + E) — dos recorridos DFS sobre el grafo.
 *   Theta: Θ(V + E)
 *   Omega: Ω(V) — si el primer DFS ya revela que el grafo no es conexo.
 * 
 * Donde V = número de paradas y E = número de rutas.
 */
public class Conexo {

    /*
     * Determina si el grafo de transporte es fuertemente conexo.
     *
     * Ejecuta DFS desde un nodo arbitrario sobre el grafo original y sobre
     * el grafo invertido. Si en ambos casos se alcanzan todos los vértices,
     * el grafo es fuertemente conexo.
     *
     * Un grafo vacío se considera conexo por convención.
     *
     * Complejidad:
     *   Big O: O(V + E) — inversión del grafo O(V + E) + DFS original O(V + E)
     *       + DFS invertido O(V + E) = O(V + E).
     *   Theta: Θ(V + E)
     *   Omega: Ω(V) — si el primer DFS no alcanza todos los nodos, retorna
     *       boolean false sin ejecutar el segundo recorrido.
     *
     * @param grafo El grafo de transporte a analizar.
     * @return boolean true si el grafo es fuertemente conexo, boolean false en caso contrario.
     */
    public static boolean esConexo(GrafoTransporte grafo) {
        Map<Parada, List<Ruta>> mapa = grafo.getGrafo();
        if (mapa.isEmpty()) return true;

        Parada origen = mapa.keySet().iterator().next();

        // Pasada 1: DFS sobre el grafo original
        if (!grafoOriginal(mapa, origen)) return false;

        // Pasada 2: DFS sobre el grafo con aristas invertidas
        Map<Parada, List<Parada>> invertido = invertirGrafo(mapa);
        return grafoInvertido(invertido, origen, mapa.size());
    }

    /*
     * Realiza un DFS iterativo sobre el grafo original desde Parada origen
     * y verifica que todos los vértices sean alcanzables.
     *
     * Complejidad:
     *   Big O: O(V + E) — cada vértice se apila una vez y cada arista se examina una vez.
     *   Theta: Θ(V + E)
     *   Omega: Ω(V) — si los nodos no tienen aristas salientes, solo se procesan vértices.
     * 
     *
     * @param mapa   Estructura del grafo original (parada → lista de rutas).
     * @param origen Parada desde donde inicia el DFS.
     * @return {@code true} si todos los vértices son alcanzables desde {@code origen}.
     */
    private static boolean grafoOriginal(Map<Parada, List<Ruta>> mapa, Parada origen) {
        Set<Parada> visitados = new HashSet<>();
        Deque<Parada> pila = new ArrayDeque<>();
        pila.push(origen);
        visitados.add(origen);

        while (!pila.isEmpty()) {
            Parada actual = pila.pop();
            for (Ruta r : mapa.getOrDefault(actual, Collections.emptyList())) {
                if (visitados.add(r.getDestino())) {
                    pila.push(r.getDestino());
                }
            }
        }
        return visitados.size() == mapa.size();
    }

    /*
     * Construye una copia del grafo con todas sus aristas invertidas.
     *
     * Este grafo invertido se usa en el segundo DFS para verificar que
     * todos los vértices pueden alcanzar el nodo origen (equivalente a que
     * el origen puede alcanzarlos en el grafo invertido).
     *
     * Complejidad:
     *   Big O: O(V + E) — se recorren todos los vértices y todas las aristas
     *       para construir la lista de adyacencia invertida.
     *   Theta: Θ(V + E)
     *   Omega: Ω(V) — si el grafo no tiene aristas, solo se crean las listas vacías.
     *
     * @param mapa Estructura del grafo original (parada → lista de rutas).
     * @return Nuevo mapa con las aristas en dirección opuesta (parada → lista de paradas origen).
     */
    private static Map<Parada, List<Parada>> invertirGrafo(Map<Parada, List<Ruta>> mapa) {
        Map<Parada, List<Parada>> invertido = new HashMap<>();
        for (Parada p : mapa.keySet()) invertido.put(p, new ArrayList<>());

        for (Map.Entry<Parada, List<Ruta>> entry : mapa.entrySet()) {
            Parada origen = entry.getKey();
            for (Ruta r : entry.getValue()) {
                invertido.get(r.getDestino()).add(origen);
            }
        }
        return invertido;
    }

    /*
     * Realiza un DFS iterativo sobre el grafo invertido desde {@code origen}
     * y verifica que todos los vértices sean alcanzables.
     *
     * Complejidad:
     * 
     *   Big O: O(V + E) — misma lógica que el DFS sobre el grafo original.
     *   Theta: Θ(V + E)
     *   Omega: Ω(V) — si los nodos no tienen aristas en el grafo invertido.
     * 
     *
     * @param invertido Grafo con aristas invertidas (parada → lista de predecesores).
     * @param origen    Parada desde donde inicia el DFS.
     * @param total     Número total de vértices esperados para validar conectividad.
     * @return boolean true si todos los vértices son alcanzables desde Parada origen
     *         en el grafo invertido.
     */
    private static boolean grafoInvertido(
            Map<Parada, List<Parada>> invertido, Parada origen, int total) {
        Set<Parada> visitados = new HashSet<>();
        Deque<Parada> pila = new ArrayDeque<>();
        pila.push(origen);
        visitados.add(origen);

        while (!pila.isEmpty()) {
            Parada actual = pila.pop();
            for (Parada vecino : invertido.getOrDefault(actual, Collections.emptyList())) {
                if (visitados.add(vecino)) {
                    pila.push(vecino);
                }
            }
        }
        return visitados.size() == total;
    }
}