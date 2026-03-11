module org.gps.gpsproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.brunomnsilva.smartgraph;
    requires java.desktop;

    opens org.gps.gpsproject.controladores to javafx.fxml;

    exports org.gps.gpsproject;
}
