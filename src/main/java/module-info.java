module org.gps.gpsproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.brunomnsilva.smartgraph;
    requires java.desktop;

    opens org.gps.gpsproject.controladores to javafx.fxml;

    exports org.gps.gpsproject;

    opens org.gps.gpsproject.modelo to javafx.base, javafx.fxml;

    exports org.gps.gpsproject.modelo;
    exports org.gps.gpsproject.controladores;
}
