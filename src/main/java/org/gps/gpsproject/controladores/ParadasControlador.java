package org.gps.gpsproject.controladores;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;

import java.util.ArrayList;

public class ParadasControlador {

    @FXML private TableView<Parada> tableParadas;
    @FXML private TableColumn<Parada, String> colId;
    @FXML private TableColumn<Parada, String> colNombre;
    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnModificar;


    private ObservableList<Parada> listaParadas = FXCollections.observableArrayList();
    private GrafoTransporte grafo = GrafoTransporte.getInstance();
    private RutasControlador rutasControlador;

    public void setRutasControlador(RutasControlador rc) {
        this.rutasControlador = rc;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tableParadas.setItems(listaParadas);
        btnAgregar.setOnAction(e -> agregarParada());
        btnEliminar.setOnAction(e -> eliminarParada());
        btnModificar.setOnAction(e -> modificarParada());
        cargarDatos();
    }

    private void eliminarParada() {
        Parada seleccionada = tableParadas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona una parada para eliminar.").showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar Parada");
        confirmacion.setHeaderText("¿Estás seguro?");
        confirmacion.setContentText("Se eliminará la parada \"" + seleccionada.getNombre() + "\" y todas sus rutas asociadas.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                grafo.deleteParada(seleccionada);
                listaParadas.setAll(new ArrayList<>(grafo.getGrafo().keySet()));
                if (rutasControlador != null) rutasControlador.cargarDatos();
            }
        });
    }

    private void agregarParada() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Agregar Parada");
        dialog.setHeaderText("Ingrese el nombre de la parada");

        dialog.showAndWait().ifPresent(nombre -> {
            nombre = nombre.trim();
            if (!nombre.isEmpty()) {
                Parada nueva = grafo.addParada(nombre);
                listaParadas.add(nueva);
            } else {
                new Alert(Alert.AlertType.ERROR, "El nombre no puede estar vacío.").showAndWait();
            }
        });
    }

    private void modificarParada() {
        Parada seleccionada = tableParadas.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona una parada para modificar.").showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog(seleccionada.getNombre());
        dialog.setTitle("Modificar Parada");
        dialog.setHeaderText("Ingrese el nuevo nombre de la parada");

        dialog.showAndWait().ifPresent(nombre -> {
            nombre = nombre.trim();
            if (!nombre.isEmpty()) {
                seleccionada.setNombre(nombre);
                tableParadas.refresh();
                if (rutasControlador != null) rutasControlador.cargarDatos();
            } else {
                new Alert(Alert.AlertType.ERROR, "El nombre no puede estar vacío.").showAndWait();
            }
        });
    }

    private void cargarDatos() {
        listaParadas.clear();
        for (Parada p : grafo.getGrafo().keySet()) {
            listaParadas.add(p);
        }
    }
}