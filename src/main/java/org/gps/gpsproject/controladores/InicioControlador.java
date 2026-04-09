package org.gps.gpsproject.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.time.Year;

public class InicioControlador {

    @FXML private Label lblIntegrante1;
    @FXML private Label lblIntegrante2;
    @FXML private Label lblAnio;

    @FXML
    public void initialize() {
        lblIntegrante1.setText("Robert Garcia");
        lblIntegrante2.setText("Julian Espinal");
        lblAnio.setText(String.valueOf(Year.now().getValue()));
    }
}