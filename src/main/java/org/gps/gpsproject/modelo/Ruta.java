package org.gps.gpsproject.modelo;

/*
 * Representa una ruta dirigida entre dos paradas de la red de transporte.
 *
 * Una ruta es una arista del grafo de transporte. Almacena la parada de
 * destino y cuatro atributos de peso que pueden ser usados como criterio
 * de optimización por los distintos algoritmos del sistema:
 * 
 *   Tiempo: duración del trayecto en minutos.
 *   Distancia: longitud del trayecto en kilómetros.
 *   Costo: costo económico del trayecto (puede ser negativo si hay subsidio).
 *   Transbordo: número de transbordos requeridos.
 *
 * El metodo toString() adapta su salida según el filtro global
 * activo en FiltroActual, permitiendo que la etiqueta de la arista
 * en el mapa visual muestre el atributo relevante.
 */
public class Ruta {

    /* Parada de destino de esta ruta. */
    private Parada destino;

    /* Tiempo de viaje en minutos. */
    private double tiempo;

    /* Distancia del trayecto en kilómetros. */
    private double distancia;

    /* Costo económico del trayecto. Puede ser negativo (subsidio). */
    private double costo;

    /* Número de transbordos requeridos en este trayecto. */
    private int transbordo;

    /*
     * Construye una nueva ruta con todos sus atributos de peso.
     *
     * Complejidad:
     *   Big O: O(1)
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @param destino    Parada a la que llega esta ruta.
     * @param tiempo     Tiempo del trayecto en minutos.
     * @param distancia  Distancia del trayecto en kilómetros.
     * @param costo      Costo económico del trayecto.
     * @param transbordo Número de transbordos requeridos.
     */
    public Ruta(Parada destino, double tiempo, double distancia, double costo, int transbordo) {
        this.destino = destino;
        this.tiempo = tiempo;
        this.distancia = distancia;
        this.costo = costo;
        this.transbordo = transbordo;
    }

    /*
     * Retorna una representación textual del peso de la ruta según el filtro
     * actualmente seleccionado en FiltroActual.
     *
     * Este valor se muestra como etiqueta sobre las aristas del mapa visual
     * (SmartGraph), adaptándose dinámicamente al criterio de búsqueda activo.
     *
     * Complejidad:
     *   Big O: O(1)
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @return Cadena con el valor del peso activo y su unidad (ej: "15 min", "8.5 km", "$25").
     */
    @Override
    public String toString() {
        return switch (FiltroActual.getFiltro()) {
            case "Distancia"  -> distancia + " km";
            case "Costo"      -> "$" + costo;
            case "Transbordo" -> transbordo + " transbordos";
            default           -> tiempo + " min";
        };
    }

    /*
     * Retorna la parada de destino de esta ruta.
     *
     * Complejidad: O(1) / Θ(1) / Ω(1)
     *
     * @return La Parada de destino.
     */
    public Parada getDestino() { return destino; }

    /*
     * Retorna el tiempo de viaje de esta ruta en minutos.
     *
     * Complejidad: O(1) / Θ(1) / Ω(1)
     *
     * @return Tiempo en minutos.
     */
    public double getTiempo()     { return tiempo; }

    /*
     * Retorna la distancia de esta ruta en kilómetros.
     *
     * Complejidad: O(1) / Θ(1) / Ω(1)
     *
     * @return Distancia en kilómetros.
     */
    public double getDistancia()  { return distancia; }

    /*
     * Retorna el costo económico de esta ruta.
     *
     * Complejidad: O(1) / Θ(1) / Ω(1)
     *
     * @return Costo del trayecto (puede ser negativo si hay subsidio).
     */
    public double getCosto()      { return costo; }

    /*
     * Retorna el número de transbordos requeridos en esta ruta.
     *
     * Complejidad: O(1) / Θ(1) / Ω(1)
     *
     * @return Número de transbordos.
     */
    public int getTransbordo() { return transbordo; }
}