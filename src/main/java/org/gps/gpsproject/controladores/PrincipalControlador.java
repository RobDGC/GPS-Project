package org.gps.gpsproject.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;


public class PrincipalControlador {

    @FXML
    private Pane panelCentral;

    @FXML
    private Pane inicioPanel;

    @FXML
    private Pane paradaPanel;

    @FXML
    private Pane rutaPanel;

    @FXML
    private Pane buscarRutaPanel;

    @FXML
    private Pane mapaPanel;

    @FXML
    private Pane inicioVista;

    @FXML
    private Pane paradasVista;

    @FXML
    private Pane rutasVista;

    @FXML
    private Pane buscarVista;

    @FXML
    private Pane mapaVista;


    private Pane panelSelected;


    @FXML
    public void initialize(){
        System.out.println("Menu cargado correctamente");

        panelSelected = inicioPanel;
        panelSelected.setStyle("-fx-background-color: gray;");
    }


    @FXML
    private void mouseEntered(MouseEvent e){
        Pane panel = (Pane) e.getSource();

        if(panel != panelSelected){
            panel.setStyle("-fx-background-color: #bdc3c7;");
        }
        panel.setCursor(Cursor.HAND);
    }

    @FXML
    void mouseExited(MouseEvent e){
        Pane panel = (Pane)e.getSource();

        if(panel != panelSelected){
            panel.setStyle("-fx-background-color: white;");
        }
        panel.setCursor(Cursor.DEFAULT);

    }

    @FXML
    private void mouseClicked(MouseEvent e){

        Pane panel = (Pane) e.getSource();

        if(panelSelected != null){
            panelSelected.setStyle("-fx-background-color: white;");
        }

        panelSelected = panel;
        panelSelected.setStyle("-fx-background-color: gray;");

        if(panel == inicioPanel){
            cargarVista("inicio-view.fxml");
        }

        if(panel == paradaPanel){
            cargarVista("paradas-view.fxml");
        }

        if(panel == rutaPanel){
            cargarVista("rutas-view.fxml");
        }

        if(panel == buscarRutaPanel){
            cargarVista("buscar-view.fxml");
        }

        if(panel == mapaPanel){
            cargarVista("mapa-view.fxml");
        }

    }


    private void cargarVista(String fxml){

        try{

            Pane vista = FXMLLoader.load(
                    getClass().getResource("/gpsGrafo/" + fxml)
            );

            vista.setPrefWidth(panelCentral.getWidth());
            vista.setPrefHeight(panelCentral.getHeight());

            panelCentral.getChildren().setAll(vista);

        }catch(Exception e){
            e.printStackTrace();
        }

    }




}
