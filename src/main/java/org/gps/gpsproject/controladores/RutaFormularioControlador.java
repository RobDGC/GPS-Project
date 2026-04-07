package org.gps.gpsproject.controladores;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.gps.gpsproject.grafo.GrafoTransporte;
import org.gps.gpsproject.modelo.Parada;
import org.gps.gpsproject.modelo.Ruta;

import java.util.ArrayList;

public class RutaFormularioControlador {

    @FXML private Label lblTitulo;
    @FXML private ComboBox<Parada> cbOrigen;
    @FXML private ComboBox<Parada> cbDestino;
    @FXML private TextField txtTiempo;
    @FXML private TextField txtDistancia;
    @FXML private TextField txtCosto;
    @FXML private TextField txtTransbordo;
    @FXML private Button btnGuardar;

    private GrafoTransporte grafo = GrafoTransporte.getInstance();
    private RutasControlador rutasControlador;

    // Para modo edición
    private boolean modoEdicion = false;
    private Parada origenOriginal;
    private Parada destinoOriginal;

    @FXML
    public void initialize() {
        ArrayList<Parada> paradas = new ArrayList<>(grafo.getGrafo().keySet());
        cbOrigen.setItems(FXCollections.observableArrayList(paradas));
        cbDestino.setItems(FXCollections.observableArrayList(paradas));
    }

    public void setRutasControlador(RutasControlador rc) {
        this.rutasControlador = rc;
    }

    // Llamar desde RutasControlador para modo edicion
    public void cargarRuta(Parada origen, Parada destino, double tiempo,
                           double distancia, double costo, int transbordo) {
        modoEdicion = true;
        origenOriginal = origen;
        destinoOriginal = destino;

        lblTitulo.setText("Modificar Ruta");
        cbOrigen.setValue(origen);
        cbDestino.setValue(destino);
        txtTiempo.setText(String.valueOf(tiempo));
        txtDistancia.setText(String.valueOf(distancia));
        txtCosto.setText(String.valueOf(costo));
        txtTransbordo.setText(String.valueOf(transbordo));
    }

    @FXML
    private void guardar() {
        Parada origen  = cbOrigen.getValue();
        Parada destino = cbDestino.getValue();

        if (origen == null || destino == null) {
            mostrarError("Selecciona origen y destino.");
            return;
        }

        if (origen.equals(destino)) {
            mostrarError("El origen y destino no pueden ser iguales.");
            return;
        }

        try {
            double tiempo     = Double.parseDouble(txtTiempo.getText().trim());
            double distancia  = Double.parseDouble(txtDistancia.getText().trim());
            double costo      = Double.parseDouble(txtCosto.getText().trim());
            int    transbordo = Integer.parseInt(txtTransbordo.getText().trim());

            // Tiempo, distancia y transbordo nunca pueden ser negativos
            if (tiempo < 0 || distancia < 0 || transbordo < 0) {
                mostrarError("Tiempo, distancia y transbordos no pueden ser negativos.");
                return;
            }

            // El costo SÍ puede ser negativo (descuento/subsidio en la ruta)
            // Se muestra un aviso informativo pero no bloquea el guardado
            if (costo < 0) {
                Alert aviso = new Alert(Alert.AlertType.INFORMATION);
                aviso.setTitle("Costo negativo");
                aviso.setHeaderText("Ruta con descuento detectada");
                aviso.setContentText("Esta ruta tiene un costo negativo ($" + costo + ").\n" +
                        "Esto representa un subsidio o descuento especial.\n" +
                        "Usa Bellman-Ford para calcular rutas que incluyan esta ruta.");
                aviso.showAndWait();
            }

            if (modoEdicion) {
                grafo.deleteRuta(origenOriginal, destinoOriginal);
            }

            grafo.addRuta(origen, destino, tiempo, costo, distancia, transbordo);

            if (rutasControlador != null) rutasControlador.cargarDatos();

            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarError("Tiempo, distancia, costo y transbordo deben ser números válidos.");
        }
    }
    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
    }
}