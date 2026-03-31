package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.util.*;

public class Conexo {

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

    // DFS iterativo sobre el grafo original
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

    // Invierte todas las aristas del grafo
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

    // DFS iterativo sobre el grafo invertido
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