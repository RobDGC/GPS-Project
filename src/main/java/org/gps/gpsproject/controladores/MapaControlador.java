package org.gps.gpsproject.controladores;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartRandomPlacementStrategy;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.gps.gpsproject.algoritmos.Conexo;
import org.gps.gpsproject.algoritmos.Dijkstra;
import org.gps.gpsproject.algoritmos.ResultadoDijkstra;
import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Criterio;
import org.gps.gpsproject.modelo.FiltroActual;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class MapaControlador {

    @FXML private AnchorPane panelGrafo;
    @FXML private ComboBox<Parada> cbOrigen;
    @FXML private ComboBox<Parada> cbDestino;
    @FXML private ComboBox<String> cbFiltro;
    @FXML private Label lblRuta;
    @FXML private Label lblTiempo;
    @FXML private Label lblDistancia;
    @FXML private Label lblCosto;
    @FXML private Label lblTransbordo;
    @FXML private Label lblConexo;

    private Map<Parada, double[]> posicionesGuardadas = new HashMap<>();
    private SmartGraphPanel<Parada, Ruta> graphViewActual;
    private Digraph<Parada, Ruta> smartGraphActual;

    private GrafoTransporte grafo = GrafoTransporte.getInstance();
    private boolean esConexo = Conexo.esConexo(grafo);

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
        lblConexo.setText(esConexo ? "Es conexo" : "No es conexo");
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

            Platform.runLater(() -> Platform.runLater(() ->
                    Platform.runLater(() -> inicializarGraphView(graphView))
            ));

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

        // Esperar 300ms para que SmartGraph termine de aplicar sus estilos CSS
        Timeline delay = new Timeline(new KeyFrame(Duration.millis(300), event -> {
            if (!esConexo) {
                colorearTodosLosNodosNoAlcanzables();
            }
        }));
        delay.play();
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
        Parada origen  = cbOrigen.getValue();
        Parada destino = cbDestino.getValue();
        String filtro  = cbFiltro.getValue();

        if (origen == null || destino == null || filtro == null) return;

        // Validación: grafo no conexo
        if (!esConexo) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Red no conexa");
            alert.setHeaderText("No se puede buscar rutas");
            alert.setContentText("La red de transporte no es completamente conexa. " +
                    "Algunos nodos están desconectados (marcados en rojo). " +
                    "Agrega rutas para conectar todos los nodos antes de buscar.");
            alert.showAndWait();
            return;
        }

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
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sin ruta");
            alert.setHeaderText(null);
            alert.setContentText("No existe un camino entre " + origen + " y " + destino + ".");
            alert.showAndWait();
            return;
        }

        // Calcular totales
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

        // Resaltar camino y marcar nodos no alcanzables en rojo
        Platform.runLater(() -> {
            resaltarCamino(camino);
            colorearNodosNoAlcanzables(origen, criterio);
        });
    }

    /**
     * Corre Dijkstra desde el origen y pinta en rojo los nodos
     * que no tienen camino desde ese origen (distancia == MAX_VALUE).
     */
    private void colorearNodosNoAlcanzables(Parada origen, Criterio criterio) {
        if (graphViewActual == null || smartGraphActual == null) return;

        ResultadoDijkstra resultado = Dijkstra.dijkstra(grafo, origen, criterio);

        smartGraphActual.vertices().forEach(v -> {
            Parada p = v.element();
            double dist = resultado.getDistancia(p);

            if (dist == Double.MAX_VALUE) {
                graphViewActual.getStylableVertex(v)
                        .setStyleInline("-fx-fill: #e74c3c; -fx-stroke: #c0392b;");
            }
        });
    }

    /**
     * Recorre todos los nodos del grafo y pinta en rojo los que
     * están aislados (no pueden alcanzar ni ser alcanzados por otros nodos).
     */
    private void colorearTodosLosNodosNoAlcanzables() {
        if (graphViewActual == null || smartGraphActual == null) return;

        Criterio criterio = Criterio.TIEMPO;

        // Construir mapa de entrantes: cuántos nodos tienen ruta hacia cada parada
        Map<Parada, Long> entrantesMap = new HashMap<>();
        for (Parada origen : grafo.getGrafo().keySet()) {
            ResultadoDijkstra resultado = Dijkstra.dijkstra(grafo, origen, criterio);
            for (Parada destino : grafo.getGrafo().keySet()) {
                if (!origen.equals(destino) && resultado.getDistancia(destino) < Double.MAX_VALUE) {
                    entrantesMap.merge(destino, 1L, Long::sum);
                }
            }
        }

        smartGraphActual.vertices().forEach(v -> {
            Parada p = v.element();

            // Salientes: cuántos nodos puede alcanzar este nodo
            ResultadoDijkstra resultado = Dijkstra.dijkstra(grafo, p, criterio);
            long salientes = resultado.getTodasDistancias().values().stream()
                    .filter(d -> d < Double.MAX_VALUE)
                    .count() - 1; // restar él mismo

            // Entrantes: cuántos nodos tienen ruta hacia este nodo
            long entrantes = entrantesMap.getOrDefault(p, 0L);

            // Rojo si no tiene al menos una entrada Y una salida
            if (entrantes == 0 || salientes == 0) {
                graphViewActual.getStylableVertex(v)
                        .setStyleInline("-fx-fill: #e74c3c; -fx-stroke: #c0392b;");
            }
        });
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
        esConexo = Conexo.esConexo(grafo);
        lblConexo.setText(esConexo ? "Es conexo" : "No es conexo");
        construirGrafo();
    }

    @FXML
    private void actualizarGrafo() {
        construirGrafo();
    }
}