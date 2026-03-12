package org.gps.gpsproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.gps.gpsproject.prueba.DatosDePrueba;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        DatosDePrueba.cargarDatos(); // ← carga los datos antes de mostrar la ventana

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/gpsGrafo/Principal-view.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Sistema de GPS");
        stage.setScene(scene);

        stage.setMaximized(true);

        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}