package controller;

import dao.RepairRequestDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.RepairRequest;
import model.Role;
import model.Status;
import model.User;
import util.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RequestListController implements Initializable {
    @FXML private TableView <RepairRequest> tableView;
    @FXML private TableColumn <RepairRequest, Integer> colId;
    @FXML private TableColumn <RepairRequest, String> colClient;
    @FXML private TableColumn <RepairRequest, String> colDevice;
    @FXML private TableColumn <RepairRequest, Status> colStatus;
    @FXML private TableColumn <RepairRequest, String> colDate;
    @FXML private TextField searchField;
    @FXML private Label statusLabel;

    private final RepairRequestDAO dao = new RepairRequestDAO();
    private final ObservableList <RepairRequest> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        colDevice.setCellValueFactory(new PropertyValueFactory<>("deviceModel"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));

        colStatus.setCellFactory(column -> new TableCell <RepairRequest, Status>() {
            @Override
            protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.getDisplayName());
                    getStyleClass().removeAll("status-new", "status-progress", "status-ready", "status-cancelled");

                    switch (status) {
                        case NEW -> getStyleClass().add("status-new");
                        case ACCEPTED, DIAGNOSTICS, IN_PROGRESS, WAITING_PARTS -> getStyleClass().add("status-progress");
                        case READY, COMPLETED -> getStyleClass().add("status-ready");
                        case CANCELLED -> getStyleClass().add("status-cancelled");
                    }
                }
            }
        });

        tableView.setRowFactory(tv -> {
            TableRow <RepairRequest> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    openDetail(row.getItem());
                }
            });
            return row;
        });

        tableView.setItems(data);
    }

    private void loadData() {
        try {
            data.clear();
            User current = SessionManager.getCurrentUser();
            if (current != null && current.getRole() == Role.CLIENT) {
                data.addAll(dao.getByClientId(current.getId()));
            } else {
                data.addAll(dao.getAll());
            }
            statusLabel.setText("Всего заявок: " + data.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void search() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadData();
            return;
        }
        try {
            data.clear();
            User current = SessionManager.getCurrentUser();
            if (current != null && current.getRole() == Role.CLIENT) {
                for (RepairRequest r : dao.search(query)) {
                    if (r.getClientId() == current.getId()) {
                        data.add(r);
                    }
                }
            } else {
                data.addAll(dao.search(query));
            }
            statusLabel.setText("Найдено: " + data.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refresh() {
        searchField.clear();
        loadData();
    }

    @FXML
    private void deleteSelected() {
        RepairRequest selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Выберите заявку для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удалить заявку #" + selected.getId() + "?");
        confirm.setContentText("Это действие нельзя отменить");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                dao.delete(selected.getId());
                loadData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void openDetail(RepairRequest request) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/request_detail.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Заявка #" + request.getId());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);

            RequestDetailController controller = loader.getController();
            controller.setRequest(request);
            controller.setOnSaveCallback(this::loadData);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание");
        alert.setContentText(message);
        alert.showAndWait();
    }
}