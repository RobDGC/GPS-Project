package org.gps.gpsproject.grafo;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class GrafoTransporte {

    private static GrafoTransporte instancia;
    private Map<Parada, List<Ruta>> grafo;

    public static AtomicInteger paradaGen = new AtomicInteger(); // manejarlo con atomic para mejorar recurrencia

    public GrafoTransporte() {
        grafo = new HashMap<>();
    }


    public static GrafoTransporte getInstance() {
        if (instancia == null) {
            instancia = new GrafoTransporte();
        }
        return instancia;
    }

    //Añade una parada solo si no se ha creado la parada anteriormente.
    public Parada addParada(String nombre){

        Parada aux = new Parada("P"+paradaGen.incrementAndGet(), nombre);
        grafo.putIfAbsent(aux, new ArrayList<>());// putIfAbsent solo agrega si no existe ya en el mapa.
        return aux;
    }

    //Borra una parada y desvincula cualquier ruta de esa parada.
    public void deleteParada(Parada parada){
        grafo.remove(parada);

        for(List<Ruta> r: grafo.values()){ //Obtiene cada lista de rutas del mapa.
            r.removeIf(ruta-> ruta.getDestino().equals(parada)); //Elimina si el destino de la ruta es igual a la parada que se quiere eliminar.
        }
    }

    //Agrega una ruta tomando en cuenta dos paradas
    public void addRuta(Parada origen, Parada destino, double tiempo, double costo,double distancia ,int transbordo){

        //Crea la ruta y la inserta en la lista del del nodo origen, pero no en la lista de nodo destino para hacerlo dirigido
        Ruta r = new Ruta(destino, tiempo, distancia, costo,transbordo);
        grafo.get(origen).add(r);
    }

    //Elimina una ruta entre un nodo Origen y destino
    public void deleteRuta(Parada origen, Parada destino){
        if(grafo.containsKey(origen)){//Verifica que el mapa contenga el origen que se quiere eliminar
            grafo.get(origen).removeIf(ruta -> ruta.getDestino().equals(destino)); //Elimina de la lista de ruta de origen si coincide con el destino
        }
    }

    //Devuelve la lista de vecinos que tiene una parada
    public List<Ruta> getVecinos(Parada aux){
        return grafo.getOrDefault(aux, new ArrayList<>()); //Si aux existe, retorna los vecinos de aux. En caso contrario una lista vacia.
    }

    //Comprueba si existe una ruta directa entre el origen y la parada
    public boolean existeRuta(Parada origen, Parada destino){
        if(!grafo.containsKey(origen)) return false;

        for(Ruta r: grafo.get(origen)){
            if(r.getDestino().equals(destino)) return true;
        }
        return false;
    }

    public Map<Parada, List<Ruta>> getGrafo(){
        return grafo;
    }

    public Digraph<Parada, Ruta> toSmartGraph() {

        Digraph<Parada, Ruta> smartGrafo = new DigraphEdgeList<>();
        Map<Parada, Vertex<Parada>> vertices = new HashMap<>();


        for (Parada p : grafo.keySet()) {
            vertices.put(p, smartGrafo.insertVertex(p));
        }

        for (Parada origen : grafo.keySet()) {
            for (Ruta r : grafo.get(origen)) {
                smartGrafo.insertEdge(vertices.get(origen), vertices.get(r.getDestino()), r);
            }
        }

        return smartGrafo;
    }
}
