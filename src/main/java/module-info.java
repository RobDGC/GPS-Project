module org.example.gpsproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.gpsproject to javafx.fxml;
    exports org.example.gpsproject;
}