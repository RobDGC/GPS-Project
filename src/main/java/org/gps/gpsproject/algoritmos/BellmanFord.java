package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Criterio;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.util.*;

/*
 * Implementación del algoritmo de Bellman-Ford para encontrar el camino
 * más corto desde un origen dado en el grafo de transporte.
 *
 * A diferencia de Dijkstra, Bellman-Ford soporta aristas con pesos negativos
 * y detecta la existencia de ciclos negativos, retornando una lista vacía en ese caso.
 * Se usa automáticamente cuando el criterio es COSTO y existen rutas con costo negativo.
 *
 * Complejidad general del algoritmo:
 *   Big O: O(V × E) — V-1 iteraciones, cada una recorre todas las aristas.
 *   Theta: Θ(V × E)
 *   Omega: Ω(E) — si el grafo converge en la primera iteración.
 *
 *  Donde V = número de paradas y E = número de rutas.
 */
public class BellmanFord {

    /*
     * Calcula el camino más corto entre {@code origen} y {@code destino}
     * utilizando el algoritmo de Bellman-Ford.
     *
     * El proceso se divide en tres fases:
     *   Inicialización de distancias en infinito (excepto el origen en 0).
     *   Relajación de todas las aristas repetida V-1 veces.
     *   Detección de ciclos negativos: si aún se puede relajar, hay ciclo negativo.
     *
     * Incluye una optimización de parada temprana: si en una iteración completa
     * no se actualiza ninguna distancia, el algoritmo termina antes de las V-1
     * iteraciones.
     *
     * Complejidad:
     *   Big O: O(V × E) — en el peor caso se realizan V-1 pasadas completas
     *       sobre todas las aristas, más una pasada extra para detectar ciclos negativos.
     *   Theta: Θ(V × E) — comportamiento esperado en grafos densos sin convergencia temprana.
     *   Omega: Ω(E) — si en la primera iteración ya no hay cambios (grafo trivial o
     *       conexión directa), la optimización de parada temprana detiene el algoritmo.
     *
     * @param grafo    El grafo de transporte sobre el que se calcula el camino.
     * @param origen   Parada de inicio de la búsqueda.
     * @param destino  Parada de destino de la búsqueda.
     * @param criterio Criterio de peso a minimizar (TIEMPO, COSTO, DISTANCIA, TRANSBORDOS).
     * @return Lista ordenada de paradas desde origen hasta destino,
     *         o lista vacía si no existe camino o se detectó un ciclo negativo.
     */
    public static List<Parada> caminoMasCorto(GrafoTransporte grafo,
                                              Parada origen,
                                              Parada destino,
                                              Criterio criterio) {
        Map<Parada, List<Ruta>> mapa = grafo.getGrafo();
        List<Parada> paradas = new ArrayList<>(mapa.keySet());
        int n = paradas.size();

        Map<Parada, Double> aristas = new HashMap<>();
        Map<Parada, Parada>  nodos = new HashMap<>();

        // Inicializar distancias en infinito
        for (Parada p : paradas) {
            aristas.put(p, Double.MAX_VALUE);
            nodos.put(p, null);
        }
        aristas.put(origen, 0.0);

        // Relajar aristas n-1 veces
        for (int i = 0; i < n - 1; i++) {
            boolean actualizado = false;

            for (Parada u : paradas) {
                if (aristas.get(u) == Double.MAX_VALUE) continue;

                for (Ruta r : grafo.getVecinos(u)) {
                    Parada v    = r.getDestino();
                    double peso = getPeso(r, criterio);

                    // Asegurar que v esté registrado (puede ser destino de arista no listado como clave)
                    aristas.putIfAbsent(v, Double.MAX_VALUE);

                    double nueva = aristas.get(u) + peso;
                    if (nueva < aristas.get(v)) {
                        aristas.put(v, nueva);
                        nodos.put(v, u);
                        actualizado = true;
                    }
                }
            }

            // Optimización: si no hubo cambios, terminar antes
            if (!actualizado) break;
        }

        // Detección de ciclos negativos
        for (Parada u : paradas) {
            if (aristas.get(u) == Double.MAX_VALUE) continue;
            for (Ruta r : grafo.getVecinos(u)) {
                Parada v    = r.getDestino();
                double peso = getPeso(r, criterio);
                aristas.putIfAbsent(v, Double.MAX_VALUE);
                if (aristas.get(u) + peso < aristas.get(v)) {
                    // Ciclo negativo detectado — devolver lista vacía
                    return Collections.emptyList();
                }
            }
        }

        return reconstruir(nodos, origen, destino);
    }

    /*
     * Reconstruye el camino desde {@code origen} hasta {@code destino}
     * siguiendo el mapa de predecesores en sentido inverso.
     *
     * Complejidad:
     *   Big O: O(V) — en el peor caso el camino recorre todos los vértices.
     *   Theta: Θ(k) — donde k es la longitud real del camino.
     *   Omega: Ω(1) — si origen y destino son adyacentes o no hay camino.
     *
     * @param prev    Mapa de predecesores generado por Bellman-Ford.
     * @param origen  Parada de inicio del camino.
     * @param destino Parada final del camino.
     * @return Lista ordenada de paradas del camino, o lista vacía si no existe.
     */
    private static List<Parada> reconstruir(Map<Parada, Parada> prev,
                                            Parada origen,
                                            Parada destino) {
        List<Parada> camino = new ArrayList<>();

        for (Parada actual = destino; actual != null; actual = prev.get(actual)) {
            camino.add(0, actual);
            if (actual.equals(origen)) break;
        }

        // Validar que el camino realmente llega desde el origen
        if (camino.isEmpty() || !camino.get(0).equals(origen)) {
            return Collections.emptyList();
        }

        return camino;
    }

    /*
     * Selecciona y retorna el peso de una ruta según el criterio indicado.
     *
     * Complejidad:
     *   Big O: O(1)
     *   Big Θ: Θ(1)
     *   Big Ω: Ω(1)
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
}