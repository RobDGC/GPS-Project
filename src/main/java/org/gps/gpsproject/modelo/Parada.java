package org.gps.gpsproject.modelo;

/*
 * Representa una parada dentro de la red de transporte.
 *
 * Una parada es un vértice del grafo de transporte. Cada parada tiene
 * un identificador único inmutable String id generado automáticamente
 * por GrafoTransporte} y un nombre
 * descriptivo modificable.
 */
public class Parada {

    /* Identificador único e inmutable de la parada. */
    private String id;

    /* Nombre descriptivo de la parada, visible en la interfaz de usuario. */
    private String nombre;

    /*
     * Construye una nueva parada con el identificador y nombre dados.
     *
     * Complejidad:
     *   Big O: O(1)
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @param id     Identificador único de la parada.
     * @param nombre Nombre descriptivo de la parada.
     */
    public Parada(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    /*
     * Retorna el identificador único de la parada.
     *
     * Complejidad:
     *   Big O: O(1)
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @return El ID único de la parada.
     */
    public String getId() {
        return id;
    }

    /*
     * Retorna el nombre descriptivo de la parada.
     *
     * Complejidad:
     *   Big O: O(1)
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @return El nombre de la parada.
     */
    public String getNombre() {
        return nombre;
    }

    /*
     * Actualiza el nombre descriptivo de la parada.
     *
     * Complejidad:
     *   Big O: O(1)
     *   Theta: Θ(1)
     *   Omega: Ω(1)
     *
     * @param nombre El nuevo nombre de la parada.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    /*
     * Compara esta parada con otro objeto por igualdad de ID.
     *
     * Dos paradas son iguales si y solo si tienen el mismo String id,
     * independientemente de su nombre.
     *
     * Complejidad: 
     *   Big O: O(L) — donde L es la longitud del String ID (típicamente constante y corto).
     *   Theta: Θ(L)
     *   Omega: Ω(1) — si los IDs difieren en el primer carácter.
     * 
     *
     * @param aux El objeto a comparar.
     * @return boolean true si Object aux es una Parada con el mismo ID.
     */
    @Override
    public boolean equals(Object aux){
        if(this == aux) return true;
        if(!(aux instanceof Parada)) return false;
        Parada p = (Parada) aux;
        return id.equals(p.id);
    }

    /*
     * Retorna el hash code de la parada basado únicamente en su ID.
     *
     * Consistente con equals: dos paradas iguales producen
     * el mismo hash code.
     *
     * Complejidad: 
     *   Big O: O(L) — donde L es la longitud del String ID.
     *   Theta: Θ(L)
     *   Omega: Ω(L)
     *
     * @return El hash code del ID de la parada.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /*
     * Retorna una representación textual de la parada en formato "nombre (id)".
     *
     * Usado en los ComboBox de la interfaz para mostrar las paradas al usuario.
     *
     * Complejidad:
     *   Big O: O(L) — donde L es la longitud combinada de nombre e id.
     *   Theta: Θ(L)
     *   Omega: Ω(L) 
     *
     * @return Cadena en formato "nombre (id)".
     */
    @Override
    public String toString() {
        return nombre + " (" + id + ")";
    }
}