package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.util.*;

public class RutaAlternativa {

    /*
     * Devuelve el camino con la menor cantidad de paradas
     * entre origen y destino, sin importar pesos de aristas.
     */
    public static List<Parada> caminoMenosParadas(GrafoTransporte grafo,
                                                  Parada origen,
                                                  Parada destino) {
        if (origen.equals(destino)) return Collections.emptyList();

        Queue<Parada> cola = new LinkedList<>();
        // Mapa: parada -> de dónde vine (para reconstruir el camino)
        Map<Parada, Parada> padre = new HashMap<>();
        // Conjunto de visitados
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

                // Llegamos al destino: reconstruir y devolver
                if (vecino.equals(destino)) {
                    return reconstruir(padre, origen, destino);
                }

                cola.add(vecino);
            }
        }

        return Collections.emptyList();
    }

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