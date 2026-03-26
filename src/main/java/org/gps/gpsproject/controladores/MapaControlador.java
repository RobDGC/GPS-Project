package org.gps.gpsproject.controladores;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartRandomPlacementStrategy;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.gps.gpsproject.algoritmos.Dijkstra;
import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.FiltroActual;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;
import org.gps.gpsproject.modelo.Criterio;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapaControlador {

    @FXML private AnchorPane panelGrafo;
    @FXML private ComboBox<Parada> cbOrigen;
    @FXML private ComboBox<Parada> cbDestino;
    @FXML private ComboBox<String> cbFiltro;
    // Agregar estos @FXML en MapaControlador
    @FXML private Label lblRuta;
    @FXML private Label lblTiempo;
    @FXML private Label lblDistancia;
    @FXML private Label lblCosto;
    @FXML private Label lblTransbordo;
    @FXML private Label lblConexo;

    private Map<Parada, double[]> posicionesGuardadas = new HashMap<>();
    private SmartGraphPanel<Parada, Ruta> graphViewActual;
    private Digraph<Parada, Ruta> smartGraphActual;
    private boolean esConexo = true;

    private GrafoTransporte grafo = GrafoTransporte.getInstance();

    @FXML
    public void initialize() {
        List<Parada> paradas = new ArrayList<>(grafo.getGrafo().keySet());
        cbOrigen.setItems(FXCollections.observableArrayList(paradas));
        cbDestino.setItems(FXCollections.observableArrayList(paradas));

        cbFiltro.setItems(FXCollections.observableArrayList(
                "Tiempo", "Distancia", "Costo", "Transbordo"
        ));
        cbFiltro.setValue("Tiempo");
        cbFiltro.setOnAction(e -> {
            FiltroActual.setFiltro(cbFiltro.getValue());
            construirGrafo();
        });

        construirGrafo();
    }

    private void construirGrafo() {
        try {
            if (graphViewActual != null) guardarPosiciones(graphViewActual);

            Digraph<Parada, Ruta> smartGraph = grafo.toSmartGraph();

            URI cssUri        = getClass().getResource("/smartgraph.css").toURI();
            URI propertiesUri = getClass().getResource("/smartgraph.properties").toURI();

            SmartGraphProperties properties = new SmartGraphProperties(propertiesUri.toString());

            SmartGraphPanel<Parada, Ruta> graphView =
                    new SmartGraphPanel<>(smartGraph, properties, new SmartRandomPlacementStrategy(), cssUri);

            AnchorPane.setTopAnchor(graphView, 0.0);
            AnchorPane.setLeftAnchor(graphView, 0.0);
            AnchorPane.setRightAnchor(graphView, 0.0);
            AnchorPane.setBottomAnchor(graphView, 0.0);

            panelGrafo.getChildren().setAll(graphView);

            this.graphViewActual = graphView;
            this.smartGraphActual = smartGraph;

            // Doble runLater: primero espera la escena, luego espera el layout
            Platform.runLater(() -> {
                Platform.runLater(() -> {
                    Platform.runLater(() -> inicializarGraphView(graphView));
                });
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inicializarGraphView(SmartGraphPanel<Parada, Ruta> graphView) {
        graphView.init();

        if (!posicionesGuardadas.isEmpty()) {
            smartGraphActual.vertices().forEach(v -> {
                Parada p = v.element();
                if (posicionesGuardadas.containsKey(p)) {
                    double[] pos = posicionesGuardadas.get(p);
                    graphView.setVertexPosition(v, pos[0], pos[1]);
                }
            });
        }

        graphView.setOnMouseReleased(e -> guardarPosiciones(graphViewActual));
    }

    private void guardarPosiciones(SmartGraphPanel<Parada, Ruta> graphView) {
        if (graphView == null || smartGraphActual == null) return;
        smartGraphActual.vertices().forEach(v -> {
            Parada p = v.element();
            posicionesGuardadas.put(p, new double[]{
                    graphView.getVertexPositionX(v),
                    graphView.getVertexPositionY(v)
            });
        });
    }

    @FXML
    private void buscarRuta() {
        Parada origen = cbOrigen.getValue();
        Parada destino = cbDestino.getValue();
        String filtro = cbFiltro.getValue();

        if (origen == null || destino == null || filtro == null) return;

        FiltroActual.setFiltro(filtro);
        construirGrafo();

        Criterio criterio = switch (filtro) {
            case "Tiempo"     -> Criterio.TIEMPO;
            case "Distancia"  -> Criterio.DISTANCIA;
            case "Costo"      -> Criterio.COSTO;
            case "Transbordo" -> Criterio.TRANSBORDOS;
            default           -> Criterio.TIEMPO;
        };

        List<Parada> camino = Dijkstra.caminoMasCorto(grafo, origen, destino, criterio);

        if (camino.isEmpty()) {
            System.out.println("No hay camino entre " + origen + " y " + destino);
            return;
        }

        javafx.application.Platform.runLater(() -> resaltarCamino(camino));
        // Calcular totales recorriendo el camino
        double totalTiempo = 0, totalDistancia = 0, totalCosto = 0;
        int totalTransbordo = 0;

        for (int i = 0; i < camino.size() - 1; i++) {
            Parada a = camino.get(i);
            Parada b = camino.get(i + 1);
            for (Ruta r : grafo.getVecinos(a)) {
                if (r.getDestino().equals(b)) {
                    totalTiempo     += r.getTiempo();
                    totalDistancia  += r.getDistancia();
                    totalCosto      += r.getCosto();
                    totalTransbordo += r.getTransbordo();
                    break;
                }
            }
        }

        String rutaTexto = camino.stream()
                .map(Parada::getNombre)
                .collect(java.util.stream.Collectors.joining(" → "));


        lblRuta.setText(rutaTexto);
        lblTiempo.setText(totalTiempo + " min");
        lblDistancia.setText(totalDistancia + " km");
        lblCosto.setText("$" + totalCosto);
        lblTransbordo.setText(String.valueOf(totalTransbordo));
    }

    private void resaltarCamino(List<Parada> camino) {
        if (graphViewActual == null || smartGraphActual == null) return;

        smartGraphActual.vertices().forEach(v -> {
            Parada p = v.element();
            if (camino.contains(p)) {
                graphViewActual.getStylableVertex(v)
                        .setStyleInline("-fx-fill: #27ae60; -fx-stroke: #1e8449;");
            } else {
                graphViewActual.getStylableVertex(v)
                        .setStyleInline("-fx-fill: #1a1a1a; -fx-stroke: #1a1a1a;");
            }
        });

        smartGraphActual.edges().forEach(e -> {
            Parada orig = e.vertices()[0].element();
            Parada dest = e.vertices()[1].element();

            boolean enCamino = false;
            for (int i = 0; i < camino.size() - 1; i++) {
                if (camino.get(i).equals(orig) && camino.get(i + 1).equals(dest)) {
                    enCamino = true;
                    break;
                }
            }

            if (enCamino) {
                graphViewActual.getStylableEdge(e)
                        .setStyleInline("-fx-stroke: #27ae60; -fx-stroke-width: 3; -fx-fill: transparent;");
            } else {
                graphViewActual.getStylableEdge(e)
                        .setStyleInline("-fx-stroke: #1a1a1a; -fx-stroke-width: 1.5; -fx-fill: transparent;");
            }
        });
    }

    public void refrescar() {
        List<Parada> paradas = new ArrayList<>(grafo.getGrafo().keySet());
        cbOrigen.setItems(FXCollections.observableArrayList(paradas));
        cbDestino.setItems(FXCollections.observableArrayList(paradas));
        construirGrafo();
    }

    @FXML
    private void actualizarGrafo() {
        construirGrafo();
    }
}