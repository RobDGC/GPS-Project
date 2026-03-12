package org.gps.gpsproject.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;

public class PrincipalControlador {

    @FXML private Pane inicioPanel;
    @FXML private Pane paradaPanel;
    @FXML private Pane rutaPanel;
    @FXML private Pane mapaPanel;
    @FXML private Pane subMenuPrincipal;

    private Pane panelSelected;

    private Pane vistaInicio;
    private Pane vistaParadas;
    private Pane vistaRutas;
    private Pane vistaMapa;
    private MapaControlador mapaControlador;

    @FXML
    public void initialize() {
        panelSelected = inicioPanel;
        panelSelected.setStyle("-fx-background-color: gray;");

        vistaInicio  = cargarVista("Inicio-view.fxml");
        vistaParadas = cargarVista("Paradas-view.fxml");
        vistaRutas   = cargarVista("Rutas-view.fxml");

        try {
            FXMLLoader loaderMapa = new FXMLLoader(getClass().getResource("/gpsGrafo/Mapa-view.fxml"));
            vistaMapa = loaderMapa.load();
            vistaMapa.prefWidthProperty().bind(subMenuPrincipal.widthProperty());
            vistaMapa.prefHeightProperty().bind(subMenuPrincipal.heightProperty());
            mapaControlador = loaderMapa.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mostrarVista(vistaInicio);
    }

    @FXML
    private void mouseEntered(MouseEvent e) {
        Pane panel = (Pane) e.getSource();
        if (panel != panelSelected) panel.setStyle("-fx-background-color: #bdc3c7;");
        panel.setCursor(Cursor.HAND);
    }

    @FXML
    private void mouseExited(MouseEvent e) {
        Pane panel = (Pane) e.getSource();
        if (panel != panelSelected) panel.setStyle("-fx-background-color: white;");
        panel.setCursor(Cursor.DEFAULT);
    }

    @FXML
    private void mouseClicked(MouseEvent e) {
        Pane panel = (Pane) e.getSource();

        if (panelSelected != null) panelSelected.setStyle("-fx-background-color: white;");
        panelSelected = panel;
        panelSelected.setStyle("-fx-background-color: gray;");

        if (panel == inicioPanel)  mostrarVista(vistaInicio);
        if (panel == paradaPanel)  mostrarVista(vistaParadas);
        if (panel == rutaPanel)    mostrarVista(vistaRutas);
        if (panel == mapaPanel) {
            mostrarVista(vistaMapa);
            mapaControlador.refrescar();
        }
    }

    private Pane cargarVista(String fxml) {
        try {
            Pane vista = FXMLLoader.load(getClass().getResource("/gpsGrafo/" + fxml));
            vista.prefWidthProperty().bind(subMenuPrincipal.widthProperty());
            vista.prefHeightProperty().bind(subMenuPrincipal.heightProperty());
            return vista;
        } catch (Exception e) {
            e.printStackTrace();
            return new Pane();
        }
    }

    private void mostrarVista(Pane vista) {
        subMenuPrincipal.getChildren().setAll(vista);
    }
}