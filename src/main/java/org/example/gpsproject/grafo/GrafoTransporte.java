package org.example.gpsproject.grafo;

import org.example.gpsproject.modelo.Parada;
import org.example.gpsproject.modelo.Ruta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrafoTransporte {

    private Map<Parada, List<Ruta>> grafo;

    public GrafoTransporte() {
        grafo = new HashMap<>();
    }

    //Añade una parada solo si no se ha creado la parada anteriormente.
    public void addParada(Parada aux){
        grafo.putIfAbsent(aux, new ArrayList<>());// putIfAbsent solo agrega si no existe ya en el mapa.
    }

    //Borra una parada y desvincula cualquier ruta de esa parada.
    public void deleteParada(Parada parada){
        grafo.remove(parada);

        for(List<Ruta> r: grafo.values()){ //Obtiene cada lista de rutas del mapa.
            r.removeIf(ruta-> ruta.getDestino().equals(parada)); //Elimina si el destino de la ruta es igual a la parada que se quiere eliminar.
        }
    }

    //Agrega una ruta tomando en cuenta dos paradas
    public void addRuta(Parada origen, Parada destino, int tiempo, int costo,int distancia ,int transbordo){
        //añade las paradas de origen y destino en caso de que no esten en el mapa
        addParada(destino);
        addParada(origen);

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
}
