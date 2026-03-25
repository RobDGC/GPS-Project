package org.gps.gpsproject.gestorDatos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.io.*;
import java.util.*;

public class GestorDatos {

    private static final String ARCHIVO = "transporte.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void save(){

        GrafoTransporte grafo = GrafoTransporte.getInstance();

        GrafoJson datos = new GrafoJson();
        datos.paradas = new ArrayList<>();
        datos.rutas = new ArrayList<>();

        for(Parada p: grafo.getGrafo().keySet()){
            ParadaJson pj = new ParadaJson();
            pj.id = p.getId();
            pj.nombre = p.getNombre();
            datos.paradas.add(pj);

            for(Ruta r: grafo.getVecinos(p)){
                RutaJson rj = new RutaJson();
                rj.origen    = p.getId();
                rj.destino   = r.getDestino().getId();
                rj.tiempo    = r.getTiempo();
                rj.distancia = r.getDistancia();
                rj.costo     = r.getCosto();
                rj.transbordo = r.getTransbordo();
                datos.rutas.add(rj);
            }
        }

        try(Writer w = new FileWriter(ARCHIVO)){
            gson.toJson(datos,w);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void load() {
        File archivo = new File(ARCHIVO);

        if(!archivo.exists()) {
            System.out.println("Error al cargar el archivo");
            return;
        }

        GrafoTransporte grafo = GrafoTransporte.getInstance();

        try(Reader r= new FileReader(archivo)){
            GrafoJson datos = gson.fromJson(r, GrafoJson.class);

            Map<String, Parada> mapa = new HashMap<>();

            for(ParadaJson pj: datos.paradas){
                Parada p = grafo.addParada(pj.nombre);
                mapa.put(pj.id, p);
            }

            for(RutaJson rj: datos.rutas){
                Parada origen  = mapa.get(rj.origen);
                Parada destino = mapa.get(rj.destino);

                if(origen != null && destino!= null){
                    grafo.addRuta(origen, destino, rj.tiempo, rj.costo, rj.distancia, rj.transbordo);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


}
