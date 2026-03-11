package org.gps.gpsproject.controladores;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartRandomPlacementStrategy;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

public class MapaControlador {

    @FXML
    private AnchorPane panelGrafo;

    private GrafoTransporte grafo = GrafoTransporte.getInstance();

    @FXML
    public void initialize() {

        construirGrafo();

    }

    private void construirGrafo() {
        Digraph<Parada, Ruta> smartGraph = grafo.toSmartGraph(); // <-- Digraph aquí

        SmartPlacementStrategy strategy = new SmartRandomPlacementStrategy();

        SmartGraphPanel<Parada, Ruta> graphView =
                new SmartGraphPanel<>(smartGraph, strategy);

        panelGrafo.getChildren().clear();
        panelGrafo.getChildren().add(graphView);

        javafx.application.Platform.runLater(graphView::init);
    }

    @FXML
    private void actualizarGrafo(){
        construirGrafo();
    }

}
