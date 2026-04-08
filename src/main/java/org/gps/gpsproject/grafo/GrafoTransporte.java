package org.gps.gpsproject.grafo;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import org.gps.gpsproject.modelo.Criterio;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Representa el grafo dirigido de transporte del sistema GPS.
 *
 * Implementa el patrón Singleton para garantizar una única instancia compartida
 * del grafo a lo largo de toda la aplicación. El grafo se modela internamente
 * como un mapa de listas de adyacencia (Map<Parada, List<Ruta>>),
 * donde cada parada es un vértice y cada ruta es una arista dirigida.
 *
 * Las paradas se identifican con un ID único generado automáticamente
 * mediante un contador AtomicInteger para garantizar
 * seguridad en entornos concurrentes.
 */
public class GrafoTransporte {

    /* Instancia única del grafo. */
    private static GrafoTransporte instancia;

    /* Estructura principal del grafo: parada origen → lista de rutas salientes. */
    private Map<Parada, List<Ruta>> grafo;

    /*
     * Contador atómico para la generación de IDs únicos de paradas.
     * Se usa AtomicInteger para garantizar unicidad en contextos concurrentes.
     */
    public static AtomicInteger paradaGen = new AtomicInteger();

    /*
     * Constructor privado. Inicializa el mapa de adyacencia vacío.
     * Solo se invoca internamente desde getInstance().
     */
    public GrafoTransporte() {
        grafo = new HashMap<>();
    }

    /*
     * Retorna la instancia única del grafo de transporte.
     *
     * Complejidad:
     *   Big O: O(1)
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @return La única instancia de GrafoTransporte}.
     */
    public static GrafoTransporte getInstance() {
        if (instancia == null) {
            instancia = new GrafoTransporte();
        }
        return instancia;
    }

    /*
     * Agrega una nueva parada al grafo con un ID único generado automáticamente.
     *
     * Si ya existe una parada con el mismo ID (lo cual no ocurre con IDs
     * autogenerados) y putIfAbsent evita duplicados silenciosamente.
     *
     * Complejidad:
     *   Big O: O(1) amortizado — inserción en HashMap.
     *   Theta: Θ(1) amortizado
     *   Omega: Ω(1)
     *
     * @param nombre El nombre descriptivo de la parada.
     * @return La nueva Parada creada e insertada en el grafo.
     */
    public Parada addParada(String nombre){
        Parada aux = new Parada("P"+paradaGen.incrementAndGet(), nombre);
        grafo.putIfAbsent(aux, new ArrayList<>());
        return aux;
    }

    /*
     * Elimina una parada del grafo y todas las rutas que la tienen como destino.
     *
     * La eliminación tiene dos pasos: eliminar la parada como clave del mapa,
     * y barrer todas las listas de adyacencia para eliminar rutas que apunten
     * a la parada eliminada.
     *
     * Complejidad:
     *   Big O: O(V + E) — se recorren todas las listas de adyacencia
     *       para eliminar rutas entrantes a la parada.
     *   Theta: Θ(V + E)
     *   Omega: Ω(1) — si el grafo tiene una sola parada sin rutas.
     *
     * @param parada La parada a eliminar del grafo.
     */
    public void deleteParada(Parada parada){
        grafo.remove(parada);

        for(List<Ruta> r: grafo.values()){
            r.removeIf(ruta -> ruta.getDestino().equals(parada));
        }
    }

    /*
     * Agrega una ruta dirigida entre dos paradas con sus atributos de peso.
     *
     * La ruta solo se agrega en la lista de adyacencia del nodo origen,
     * manteniendo la naturaleza dirigida del grafo.
     *
     * Complejidad:
     *   Big O: O(1) amortizado — creación del objeto Ruta y al ArrayList.
     *   Theta: Θ(1) amortizado
     *   Omega: Ω(1)
     *
     * @param origen     Parada de inicio de la ruta.
     * @param destino    Parada de destino de la ruta.
     * @param tiempo     Tiempo de viaje en minutos.
     * @param costo      Costo económico del trayecto (puede ser negativo si hay subsidio).
     * @param distancia  Distancia en kilómetros.
     * @param transbordo Número de transbordos requeridos.
     */
    public void addRuta(Parada origen, Parada destino, double tiempo, double costo, double distancia, int transbordo){
        Ruta r = new Ruta(destino, tiempo, distancia, costo, transbordo);
        grafo.get(origen).add(r);
    }

    /*
     * Elimina la ruta dirigida entre Parada origen y Parada destino.
     *
     * Si no existe una ruta con ese destino, la operación no tiene efecto.
     *
     * Complejidad:
     *   Big O: O(E_origen) — se recorre la lista de rutas del nodo origen,
     *       donde E_origen es el grado de salida de la parada origen.
     *   Theta: Θ(E_origen)
     *   Omega: Ω(1) — si la primera ruta en la lista es la que se desea eliminar.
     *
     * @param origen  Parada de inicio de la ruta a eliminar.
     * @param destino Parada de llegada de la ruta a eliminar.
     */
    public void deleteRuta(Parada origen, Parada destino){
        if(grafo.containsKey(origen)){
            grafo.get(origen).removeIf(ruta -> ruta.getDestino().equals(destino));
        }
    }

    /*
     * Retorna la lista de rutas salientes (vecinos) de una parada dada.
     *
     * Si la parada no existe en el grafo, retorna una lista vacía
     * sin lanzar excepción.
     *
     * Complejidad:
     *   Big O: O(1) — acceso directo al HashMap.
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @param aux La parada de la que se quieren obtener los vecinos.
     * @return Lista de Ruta salientes desde la parada, o lista vacía si no existe.
     */
    public List<Ruta> getVecinos(Parada aux){
        return grafo.getOrDefault(aux, new ArrayList<>());
    }

    /*
     * Verifica si existe una ruta dirigida directa entre {@code origen} y {@code destino}.
     *
     * Complejidad:
     *   Big O: O(E_origen) — recorre la lista de rutas del nodo origen
     *       hasta encontrar el destino o agotarla.
     *   Theta: Θ(E_origen / 2) — en promedio, se examina la mitad de las rutas.
     *   Omega: Ω(1) — si la primera ruta en la lista ya apunta al destino buscado.
     *
     *
     * @param origen  Parada de inicio de la ruta a verificar.
     * @param destino Parada de llegada de la ruta a verificar.
     * @return {@code true} si existe una ruta directa de origen a destino, {@code false} en caso contrario.
     */
    public boolean existeRuta(Parada origen, Parada destino){
        if(!grafo.containsKey(origen)) return false;

        for(Ruta r: grafo.get(origen)){
            if(r.getDestino().equals(destino)) return true;
        }
        return false;
    }

    /*
     * Verifica si existe alguna ruta en el grafo con peso negativo según el criterio dado.
     *
     * Usado principalmente para detectar costos negativos antes de ejecutar
     * Dijkstra (que no funciona con pesos negativos) y redirigir a Bellman-Ford.
     *
     * Complejidad:
     *   Big O: O(V + E) — recorre todos los vértices y todas sus rutas.
     *   Theta: Θ(V + E) — en el caso promedio recorre la mayoría de las aristas.
     *   Omega: Ω(1) — si la primera ruta examinada ya tiene peso negativo.
     *
     * @param c El criterio de peso a verificar (TIEMPO, COSTO, DISTANCIA, TRANSBORDOS).
     * @return boolean true si existe al menos una ruta con peso negativo, boolean false en caso contrario.
     */
    public boolean existeRutaNegativa(Criterio c){
        for(Parada p: grafo.keySet()){
            for(Ruta r: grafo.get(p)){
                Double peso = switch (c){
                    case TIEMPO -> r.getTiempo();
                    case COSTO -> r.getCosto();
                    case DISTANCIA -> r.getDistancia();
                    case TRANSBORDOS -> (double)r.getTransbordo();
                };
                if(peso < 0) return true;
            }
        }
        return false;
    }

    /*
     * Retorna el mapa completo de adyacencia del grafo.
     *
     * Complejidad:
     *   Big O: O(1)
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @return Mapa mutable de parada → lista de rutas salientes.
     */
    public Map<Parada, List<Ruta>> getGrafo(){
        return grafo;
    }

    /*
     * Convierte el grafo interno al formato Digraph de la biblioteca SmartGraph
     * para su visualización gráfica interactiva.
     *
     * Crea nuevos vértices y aristas en la estructura de SmartGraph a partir
     * de las paradas y rutas del grafo interno.
     *
     * Complejidad:
     *   Big O: O(V + E) — se recorren todos los vértices para insertarlos
     *       y todas las aristas para conectarlos.
     *   Theta: Θ(V + E)
     *   Omega: Ω(V) — si el grafo no tiene aristas.
     *
     * @return Un Digraph de SmartGraph listo para ser renderizado visualmente.
     */
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