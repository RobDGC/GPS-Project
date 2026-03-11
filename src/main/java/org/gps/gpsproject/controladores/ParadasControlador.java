    package org.gps.gpsproject.controladores;

    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.scene.layout.AnchorPane;
    import org.gps.gpsproject.grafo.GrafoTransporte;
    import org.gps.gpsproject.modelo.Parada;

    public class ParadasControlador {

        @FXML
        private TableView<Parada> tableParadas;

        @FXML
        private TableColumn<Parada, String> colId;

        @FXML
        private TableColumn<Parada, String> colNombre;

        @FXML
        private Button btnAgregar;

        private ObservableList<Parada> listaParadas = FXCollections.observableArrayList();

        private GrafoTransporte grafo = GrafoTransporte.getInstance();

        @FXML
        public void initialize() {
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

            tableParadas.setItems(listaParadas);

            btnAgregar.setOnAction(e -> agregarParada());
        }

        private void agregarParada() {

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Agregar Parada");
            dialog.setHeaderText("Ingrese el nombre de la parada");

            dialog.showAndWait().ifPresent(nombre -> {

                nombre = nombre.trim();

                if(!nombre.isEmpty()){

                    Parada nueva = grafo.addParada(nombre);

                    listaParadas.add(nueva);

                } else {

                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "El nombre no puede estar vacío.");
                    alert.showAndWait();
                }
            });
        }

    }
