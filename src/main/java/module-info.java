module org.gps.gpsproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires smartgraph;
    requires com.google.gson;

    opens org.gps.gpsproject.controladores to javafx.fxml;

    exports org.gps.gpsproject;

    opens org.gps.gpsproject.modelo to javafx.base, javafx.fxml;
    opens org.gps.gpsproject.gestorDatos to com.google.gson;

    exports org.gps.gpsproject.modelo;
    exports org.gps.gpsproject.controladores;
}
