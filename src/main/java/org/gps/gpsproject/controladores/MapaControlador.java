package org.gps.gpsproject.controladores;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import com.brunomnsilva.smartgraph.graphview.SmartRandomPlacementStrategy;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.gps.gpsproject.algoritmos.*;
import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Criterio;
import org.gps.gpsproject.modelo.FiltroActual;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.net.URI;
import java.util.*;

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
    @FXML private Button btnReload;
    @FXML private CheckBox chkArrastre;
    @FXML private Pane overlayArrastre;
    @FXML private ComboBox<String> cbAlgoritmo;
    @FXML private Label lblAlgoritmo;
    @FXML private Button btnRutaAlternativa;

    private List<Parada> caminoPendiente = null;
    private Criterio criterioPendiente = null;

    private Map<Parada, double[]> posicionesGuardadas = new HashMap<>();
    private SmartGraphPanel<Parada, Ruta> graphViewActual;
    private Digraph<Parada, Ruta> smartGraphActual;
    private boolean layoutAplicado = false;
    private boolean arrastreHabilitado = true;

    private GrafoTransporte grafo = GrafoTransporte.getInstance();
    private boolean esConexo = Conexo.esConexo(grafo);

    @FXML
    public void initialize() {
        posicionesGuardadas.clear();
        layoutAplicado = false;

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

        cbAlgoritmo.setItems(FXCollections.observableArrayList("Dijkstra", "Floyd-Warshall", "Bellman-Ford"));
        cbAlgoritmo.setValue("Dijkstra");

        construirGrafo();
        lblConexo.setText(esConexo ? "Es conexo" : "No es conexo");
    }

    @FXML
    private void redistribuirLayout() {
        posicionesGuardadas.clear();  // limpiar ANTES de construir
        layoutAplicado = false;
        if (graphViewActual != null && smartGraphActual != null) {
            guardarPosicionesVacias(); // resetear posiciones en el mapa visual
        }
        construirGrafo();
    }

    private void guardarPosicionesVacias() {
        posicionesGuardadas.clear();
    }

    @FXML
    private void toggleArrastre() {
        arrastreHabilitado = chkArrastre.isSelected();
        btnReload.setDisable(!arrastreHabilitado);

        // Si arrastre deshabilitado, el overlay captura todos los eventos del mouse
        overlayArrastre.setVisible(!arrastreHabilitado);
        overlayArrastre.setMouseTransparent(arrastreHabilitado);
    }

    private void aplicarEstadoArrastre() {
        if (graphViewActual == null) return;

        if (arrastreHabilitado) {
            graphViewActual.setOnMouseReleased(e -> guardarPosiciones(graphViewActual));
        } else {
            graphViewActual.setOnMouseReleased(null);
        }
    }

    private void construirGrafo() {
        try {
            if (graphViewActual != null && layoutAplicado) guardarPosiciones(graphViewActual);

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
        } else if (!layoutAplicado) {
            layoutAplicado = true;
            Platform.runLater(() -> aplicarLayoutFuerzaDirigida(graphView));
        }

        // Aplicar estado de arrastre segun el checkbox
        aplicarEstadoArrastre();

        Timeline delayColor = new Timeline(new KeyFrame(Duration.millis(400), e -> {
            if (!esConexo) colorearTodosLosNodosNoAlcanzables();

            // Aplicar camino pendiente si hay uno
            if (caminoPendiente != null) {
                resaltarCamino(caminoPendiente);
                colorearNodosNoAlcanzables(caminoPendiente.get(0), criterioPendiente);
                caminoPendiente = null;
                criterioPendiente = null;
            }
        }));
        delayColor.play();
    }

    private void aplicarLayoutFuerzaDirigida(SmartGraphPanel<Parada, Ruta> graphView) {
        double ancho  = panelGrafo.getWidth()  > 0 ? panelGrafo.getWidth()  : 800;
        double alto   = panelGrafo.getHeight() > 0 ? panelGrafo.getHeight() : 600;
        double margen = 80;

        List<Vertex<Parada>> vertices = new ArrayList<>(smartGraphActual.vertices());
        int n = vertices.size();
        if (n == 0) return;

        Map<Vertex<Parada>, double[]> pos = new HashMap<>();
        double cx    = ancho / 2;
        double cy    = alto  / 2;
        double radio = Math.min(ancho, alto) * 0.35;

        for (int i = 0; i < n; i++) {
            double angulo = 2 * Math.PI * i / n;
            pos.put(vertices.get(i), new double[]{
                    cx + radio * Math.cos(angulo),
                    cy + radio * Math.sin(angulo)
            });
        }

        Map<Vertex<Parada>, Set<Vertex<Parada>>> adyacencia = new HashMap<>();
        for (Vertex<Parada> v : vertices) adyacencia.put(v, new HashSet<>());
        smartGraphActual.edges().forEach(e -> {
            Vertex<Parada>[] extremos = e.vertices();
            adyacencia.get(extremos[0]).add(extremos[1]);
            adyacencia.get(extremos[1]).add(extremos[0]);
        });

        int    iteraciones  = 300;
        double k            = Math.sqrt((ancho * alto) / n);
        double temperatura  = ancho / 2;
        double enfriamiento = temperatura / iteraciones;

        Map<Vertex<Parada>, double[]> desplazamiento = new HashMap<>();

        for (int iter = 0; iter < iteraciones; iter++) {

            for (Vertex<Parada> v : vertices) desplazamiento.put(v, new double[]{0, 0});

            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    Vertex<Parada> vi = vertices.get(i);
                    Vertex<Parada> vj = vertices.get(j);

                    double dx   = pos.get(vi)[0] - pos.get(vj)[0];
                    double dy   = pos.get(vi)[1] - pos.get(vj)[1];
                    double dist = Math.max(Math.sqrt(dx * dx + dy * dy), 0.01);

                    double fuerza = (k * k) / dist;
                    double fx = (dx / dist) * fuerza;
                    double fy = (dy / dist) * fuerza;

                    desplazamiento.get(vi)[0] += fx;
                    desplazamiento.get(vi)[1] += fy;
                    desplazamiento.get(vj)[0] -= fx;
                    desplazamiento.get(vj)[1] -= fy;
                }
            }

            smartGraphActual.edges().forEach(e -> {
                Vertex<Parada>[] extremos = e.vertices();
                Vertex<Parada> vi = extremos[0];
                Vertex<Parada> vj = extremos[1];

                double dx   = pos.get(vi)[0] - pos.get(vj)[0];
                double dy   = pos.get(vi)[1] - pos.get(vj)[1];
                double dist = Math.max(Math.sqrt(dx * dx + dy * dy), 0.01);

                double fuerza = (dist * dist) / k;
                double fx = (dx / dist) * fuerza;
                double fy = (dy / dist) * fuerza;

                desplazamiento.get(vi)[0] -= fx;
                desplazamiento.get(vi)[1] -= fy;
                desplazamiento.get(vj)[0] += fx;
                desplazamiento.get(vj)[1] += fy;
            });

            for (Vertex<Parada> v : vertices) {
                double dx   = desplazamiento.get(v)[0];
                double dy   = desplazamiento.get(v)[1];
                double dist = Math.max(Math.sqrt(dx * dx + dy * dy), 0.01);

                double nx = pos.get(v)[0] + (dx / dist) * Math.min(dist, temperatura);
                double ny = pos.get(v)[1] + (dy / dist) * Math.min(dist, temperatura);

                nx = Math.max(margen, Math.min(ancho - margen, nx));
                ny = Math.max(margen, Math.min(alto  - margen, ny));

                pos.get(v)[0] = nx;
                pos.get(v)[1] = ny;
            }

            temperatura -= enfriamiento;
        }

        for (Vertex<Parada> v : vertices) {
            graphView.setVertexPosition(v, pos.get(v)[0], pos.get(v)[1]);
        }

        guardarPosiciones(graphView);
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
        String algoritmo = cbAlgoritmo.getValue();

        // Validaciones
        if (origen == null && destino == null) {
            new Alert(Alert.AlertType.WARNING, "Debes seleccionar un origen y un destino.").showAndWait();
            return;
        }
        if (origen == null) {
            new Alert(Alert.AlertType.WARNING, "Debes seleccionar un origen.").showAndWait();
            return;
        }
        if (destino == null) {
            new Alert(Alert.AlertType.WARNING, "Debes seleccionar un destino.").showAndWait();
            return;
        }
        if (origen.equals(destino)) {
            new Alert(Alert.AlertType.WARNING, "El origen y el destino no pueden ser iguales.").showAndWait();
            return;
        }

        if (!esConexo) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Red no conexa");
            alert.setHeaderText("No se puede buscar rutas");
            alert.setContentText("La red de transporte no es completamente conexa.");
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

        // 🔥 Detectar pesos negativos
        boolean hayPesosNegativos = grafo.existeRutaNegativa(criterio);

        List<Parada> camino;

        // 🔥 FORZAR Bellman-Ford si hay costos negativos
        if (criterio == Criterio.COSTO && hayPesosNegativos) {

            if (algoritmo.equals("Dijkstra")) {
                new Alert(Alert.AlertType.WARNING,
                        "Dijkstra no funciona con costos negativos.\nSe usará Bellman-Ford automáticamente."
                ).showAndWait();
            }

            camino = BellmanFord.caminoMasCorto(grafo, origen, destino, criterio);
            lblAlgoritmo.setText("Bellman-Ford (auto)");

        } else {
            camino = switch (algoritmo) {
                case "Floyd-Warshall" -> Floyd_Warshall.caminoMasCorto(grafo, origen, destino, criterio);
                case "Bellman-Ford" -> BellmanFord.caminoMasCorto(grafo, origen, destino, criterio);
                default -> Dijkstra.caminoMasCorto(grafo, origen, destino, criterio);
            };

            lblAlgoritmo.setText(algoritmo);
        }

        // Si no hay camino
        if (camino.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sin ruta");
            alert.setHeaderText(null);
            alert.setContentText("No existe un camino entre " + origen + " y " + destino + ".");
            alert.showAndWait();
            return;
        }

        // Calcular métricas
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
                .map(Parada::getId)
                .collect(java.util.stream.Collectors.joining(" → "));

        lblRuta.setText(rutaTexto);
        lblTiempo.setText(totalTiempo + " min");
        lblDistancia.setText(totalDistancia + " km");
        lblCosto.setText("$" + totalCosto);
        lblTransbordo.setText(String.valueOf(totalTransbordo));

        caminoPendiente = camino;
        criterioPendiente = criterio;
    }

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

    private void colorearTodosLosNodosNoAlcanzables() {
        if (graphViewActual == null || smartGraphActual == null) return;

        Criterio criterio = Criterio.TIEMPO;

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

            ResultadoDijkstra resultado = Dijkstra.dijkstra(grafo, p, criterio);
            long salientes = resultado.getTodasDistancias().values().stream()
                    .filter(d -> d < Double.MAX_VALUE)
                    .count() - 1;

            long entrantes = entrantesMap.getOrDefault(p, 0L);

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
    private void buscarRutaAlternativa() {
        Parada origen  = cbOrigen.getValue();
        Parada destino = cbDestino.getValue();

        if (origen == null && destino == null) {
            new Alert(Alert.AlertType.WARNING, "Debes seleccionar un origen y un destino.").showAndWait();
            return;
        }
        if (origen == null) {
            new Alert(Alert.AlertType.WARNING, "Debes seleccionar un origen.").showAndWait();
            return;
        }
        if (destino == null) {
            new Alert(Alert.AlertType.WARNING, "Debes seleccionar un destino.").showAndWait();
            return;
        }
        if (origen.equals(destino)) {
            new Alert(Alert.AlertType.WARNING, "El origen y el destino no pueden ser iguales.").showAndWait();
            return;
        }

        if (!esConexo) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Red no conexa");
            alert.setHeaderText("No se puede buscar rutas");
            alert.setContentText("La red de transporte no es completamente conexa.");
            alert.showAndWait();
            return;
        }

        List<Parada> camino = RutaAlternativa.caminoMenosParadas(grafo, origen, destino);

        if (camino.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sin ruta");
            alert.setHeaderText(null);
            alert.setContentText("No existe un camino entre " + origen + " y " + destino + ".");
            alert.showAndWait();
            return;
        }

        // Calcular totales del camino encontrado
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
                .map(Parada::getId)
                .collect(java.util.stream.Collectors.joining(" → "));

        lblRuta.setText(rutaTexto);
        lblTiempo.setText(totalTiempo + " min");
        lblDistancia.setText(totalDistancia + " km");
        lblCosto.setText("$" + totalCosto);
        lblTransbordo.setText(String.valueOf(totalTransbordo));
        lblAlgoritmo.setText("BFS (menos paradas)");

        // Resaltar camino en el grafo
        caminoPendiente = camino;
        criterioPendiente = Criterio.TIEMPO; // criterio referencial para colorear no alcanzables
        construirGrafo();
    }

    @FXML
    private void actualizarGrafo() {
        construirGrafo();
    }
}