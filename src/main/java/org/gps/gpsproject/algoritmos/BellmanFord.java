package org.gps.gpsproject.algoritmos;

import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Criterio;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.util.*;

public class BellmanFord {

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

        // Detección de ciclos negativos (opcional pero robusto)
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

        // Reconstruir camino
        return reconstruir(nodos, origen, destino);
    }

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

    private static double getPeso(Ruta ruta, Criterio criterio) {
        return switch (criterio) {
            case TIEMPO      -> ruta.getTiempo();
            case COSTO       -> ruta.getCosto();
            case DISTANCIA   -> ruta.getDistancia();
            case TRANSBORDOS -> ruta.getTransbordo();
        };
    }
}