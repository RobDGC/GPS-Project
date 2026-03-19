package org.gps.gpsproject.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;

public class RutasControlador {

    @FXML
    private TableView<RutaTabla> tableRutas;

    @FXML
    private TableColumn<RutaTabla, String> colOrigen;

    @FXML
    private TableColumn<RutaTabla, String> colDestino;

    @FXML
    private TableColumn<RutaTabla, Double> colTiempo;

    @FXML
    private TableColumn<RutaTabla, Double> colDistancia;

    @FXML
    private TableColumn<RutaTabla, Double> colCosto;

    @FXML
    private TableColumn<RutaTabla, Integer> colTransbordo;

    @FXML
    private Button btnAgregarRuta;
    @FXML private Button btnEliminarRuta;

    private ObservableList<RutaTabla> listaRutas = FXCollections.observableArrayList();

    private GrafoTransporte grafo = GrafoTransporte.getInstance();

    @FXML
    public void initialize() {
        colOrigen.setCellValueFactory(new PropertyValueFactory<>("origen"));
        colDestino.setCellValueFactory(new PropertyValueFactory<>("destino"));
        colTiempo.setCellValueFactory(new PropertyValueFactory<>("tiempo"));
        colDistancia.setCellValueFactory(new PropertyValueFactory<>("distancia"));
        colCosto.setCellValueFactory(new PropertyValueFactory<>("costo"));
        colTransbordo.setCellValueFactory(new PropertyValueFactory<>("transbordo"));
        tableRutas.setItems(listaRutas);
        btnAgregarRuta.setOnAction(e -> agregarRuta());
        btnEliminarRuta.setOnAction(e -> eliminarRuta()); // ← agregar
        cargarDatos();
    }

    private void eliminarRuta() {
        RutaTabla seleccionada = tableRutas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona una ruta para eliminar.").showAndWait();
            return;
        }

        Parada origen  = buscarParada(seleccionada.getOrigen());
        Parada destino = buscarParada(seleccionada.getDestino());

        if (origen != null && destino != null) {
            grafo.deleteRuta(origen, destino);
            cargarDatos();
        }
    }

    public void cargarDatos() {
        listaRutas.clear();
        grafo.getGrafo().forEach((origen, rutas) -> {
            rutas.forEach(ruta -> {
                listaRutas.add(new RutaTabla(
                        origen.getId(),
                        ruta.getDestino().getId(),
                        ruta.getTiempo(),
                        ruta.getDistancia(),
                        ruta.getCosto(),
                        ruta.getTransbordo()
                ));
            });
        });
    }

    private void agregarRuta() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar Ruta");
        dialog.setHeaderText("Ingrese los datos de la nueva ruta");
        dialog.setContentText("Formato: OrigenID,DestinoID,Tiempo,Distancia,Costo,Transbordo");

        dialog.showAndWait().ifPresent(input -> {

            String[] partes = input.split(",");

            if (partes.length == 6) {

                try {

                    String idOrigen = partes[0].trim();
                    String idDestino = partes[1].trim();

                    double tiempo = Double.parseDouble(partes[2].trim());
                    double distancia = Double.parseDouble(partes[3].trim());
                    double costo = Double.parseDouble(partes[4].trim());
                    int transbordo = Integer.parseInt(partes[5].trim());

                    Parada origen = buscarParada(idOrigen);
                    Parada destino = buscarParada(idDestino);

                    if (origen == null || destino == null) {

                        Alert alert = new Alert(Alert.AlertType.ERROR,
                                "Una de las paradas no existe.");
                        alert.showAndWait();
                        return;
                    }

                    grafo.addRuta(origen, destino, tiempo, costo, distancia, transbordo);

                    RutaTabla nuevaRuta = new RutaTabla(
                            origen.getId(),
                            destino.getId(),
                            tiempo,
                            distancia,
                            costo,
                            transbordo
                    );

                    listaRutas.add(nuevaRuta);

                } catch (NumberFormatException e) {

                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Tiempo, distancia, costo y transbordo deben ser números.");
                    alert.showAndWait();
                }

            } else {

                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Debe ingresar los datos en el formato correcto.");
                alert.showAndWait();
            }
        });
    }

    private Parada buscarParada(String id) {

        for (Parada p : grafo.getGrafo().keySet()) {
            if (p.getId().equals(id)) {
                return p;
            }
        }

        return null;
    }

    public static class RutaTabla {

        private String origen;
        private String destino;
        private double tiempo;
        private double distancia;
        private double costo;
        private int transbordo;

        public RutaTabla(String origen, String destino, double tiempo, double distancia, double costo, int transbordo) {
            this.origen = origen;
            this.destino = destino;
            this.tiempo = tiempo;
            this.distancia = distancia;
            this.costo = costo;
            this.transbordo = transbordo;
        }

        public String getOrigen() {
            return origen;
        }

        public String getDestino() {
            return destino;
        }

        public double getTiempo() {
            return tiempo;
        }

        public double getDistancia() {
            return distancia;
        }

        public double getCosto() {
            return costo;
        }

        public int getTransbordo() {
            return transbordo;
        }
    }


}
