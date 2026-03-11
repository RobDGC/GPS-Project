package org.gps.gpsproject.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import java.io.IOException;

public class PrincipalControlador {

    @FXML private Pane inicioPanel;
    @FXML private Pane paradaPanel;
    @FXML private Pane rutaPanel;
    @FXML private Pane buscarRutaPanel;
    @FXML private Pane mapaPanel;
    @FXML private Pane subMenuPrincipal;

    private Pane panelSelected;

    @FXML
    public void initialize() {
        panelSelected = inicioPanel;
        panelSelected.setStyle("-fx-background-color: gray;");
        cargarVista("Inicio-view.fxml");
    }

    @FXML
    private void mouseEntered(MouseEvent e) {
        Pane panel = (Pane) e.getSource();
        if (panel != panelSelected) {
            panel.setStyle("-fx-background-color: #bdc3c7;");
        }
        panel.setCursor(Cursor.HAND);
    }

    @FXML
    private void mouseExited(MouseEvent e) {
        Pane panel = (Pane) e.getSource();
        if (panel != panelSelected) {
            panel.setStyle("-fx-background-color: white;");
        }
        panel.setCursor(Cursor.DEFAULT);
    }

    @FXML
    private void mouseClicked(MouseEvent e) {
        Pane panel = (Pane) e.getSource();

        if (panelSelected != null) {
            panelSelected.setStyle("-fx-background-color: white;");
        }

        panelSelected = panel;
        panelSelected.setStyle("-fx-background-color: gray;");

        if (panel == inicioPanel)      cargarVista("Inicio-view.fxml");
        if (panel == paradaPanel)      cargarVista("Paradas-view.fxml");
        if (panel == rutaPanel)        cargarVista("Rutas-view.fxml");
        if (panel == buscarRutaPanel)  cargarVista("Buscar-view.fxml");
        if (panel == mapaPanel)        cargarVista("Mapa-view.fxml");
    }

    private void cargarVista(String fxml) {
        try {
            Pane vista = FXMLLoader.load(
                    getClass().getResource("/gpsGrafo/" + fxml)
            );

            vista.prefWidthProperty().bind(subMenuPrincipal.widthProperty());
            vista.prefHeightProperty().bind(subMenuPrincipal.heightProperty());

            subMenuPrincipal.getChildren().setAll(vista);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}