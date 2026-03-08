module org.gps.gpsproject {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.gps.gpsproject.controladores to javafx.fxml;

    exports org.gps.gpsproject;
}
