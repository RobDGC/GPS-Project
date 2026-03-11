package org.gps.gpsproject.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;

public class ParadasControlador {

    @FXML
    private TableView<Parada> tableParadas;

    @FXML
    private TableColumn<Parada, String> colId;

    @FXML
    private TableColumn<Parada, String> colNombre;

    @FXML
    private Button btnAgregar;

    private ObservableList<Parada> listaParadas = FXCollections.observableArrayList();

    private GrafoTransporte grafo = new GrafoTransporte();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        tableParadas.setItems(listaParadas);

        btnAgregar.setOnAction(e -> agregarParada());
    }

    private void agregarParada() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar Parada");
        dialog.setHeaderText("Ingrese los datos de la nueva parada (ID y Nombre separados por coma)");
        dialog.setContentText("Formato: ID,Nombre");

        dialog.showAndWait().ifPresent(input -> {
            String[] partes = input.split(",");
            if (partes.length == 2) {
                String id = partes[0].trim();
                String nombre = partes[1].trim();

                Parada nueva = new Parada(id, nombre);

                grafo.addParada(nueva);
                listaParadas.add(nueva);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Debe ingresar ID y Nombre separados por coma.");
                alert.showAndWait();
            }
        });
    }
}
