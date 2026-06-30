package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML private StackPane contentPane;
    @FXML private Button btnNewRequest;
    @FXML private Button btnRequests;
    @FXML private Button btnExit;

    @FXML
    public void initialize() {
        showRequestList();
    }

    @FXML
    private void showNewRequest() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_request.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Новая заявка на ремонт");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);

            CreateRequestController controller = loader.getController();
            controller.setOnSaveCallback(this::showRequestList);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showRequestList() {
        loadView("/fxml/request_list.fxml");
    }

    @FXML
    private void exitApp() {
        System.exit(0);
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}