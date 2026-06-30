package controller;

import dao.RepairRequestDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.RepairRequest;
import model.Role;
import model.Status;
import model.User;
import util.SessionManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RequestDetailController implements Initializable {
    @FXML private Label lblId;
    @FXML private Label lblCreated;
    @FXML private Label lblUpdated;
    @FXML private TextField clientNameField;
    @FXML private TextField clientPhoneField;
    @FXML private TextField deviceTypeField;
    @FXML private TextField deviceModelField;
    @FXML private TextField serialNumberField;
    @FXML private TextArea problemArea;
    @FXML private ComboBox <Status> statusCombo;
    @FXML private TextArea notesArea;
    @FXML private TextField costField;
    @FXML private Button btnSave;
    @FXML private Button btnClose;

    private RepairRequest request;
    private Runnable onSaveCallback;
    private final RepairRequestDAO dao = new RepairRequestDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusCombo.getItems().setAll(Status.values());
    }

    public void setRequest(RepairRequest request) {
        this.request = request;
        fillFields();

        User current = SessionManager.getCurrentUser();
        boolean isMaster = current != null && current.getRole() == Role.MASTER;

        if (!isMaster) {
            // Клиент только просматривает
            statusCombo.setDisable(true);
            notesArea.setDisable(true);
            costField.setDisable(true);
            btnSave.setDisable(true);

            clientNameField.setEditable(false);
            clientPhoneField.setEditable(false);
            deviceTypeField.setEditable(false);
            deviceModelField.setEditable(false);
            serialNumberField.setEditable(false);
            problemArea.setEditable(false);
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    private void fillFields() {
        lblId.setText("Заявка #" + request.getId());
        lblCreated.setText("Создана: " + request.getFormattedCreatedAt());
        lblUpdated.setText("Обновлена: " + request.getFormattedUpdatedAt());

        clientNameField.setText(request.getClientName());
        clientPhoneField.setText(request.getClientPhone());
        deviceTypeField.setText(request.getDeviceType());
        deviceModelField.setText(request.getDeviceModel());
        serialNumberField.setText(request.getSerialNumber());
        problemArea.setText(request.getProblemDescription());
        statusCombo.setValue(request.getStatus());
        notesArea.setText(request.getTechnicianNotes());
        costField.setText(String.valueOf(request.getRepairCost()));
    }

    @FXML
    private void saveChanges() {
        try {
            request.setClientName(clientNameField.getText().trim());
            request.setClientPhone(clientPhoneField.getText().trim());
            request.setDeviceType(deviceTypeField.getText().trim());
            request.setDeviceModel(deviceModelField.getText().trim());
            request.setSerialNumber(serialNumberField.getText().trim());
            request.setProblemDescription(problemArea.getText().trim());
            request.setStatus(statusCombo.getValue());
            request.setTechnicianNotes(notesArea.getText().trim());

            try {
                request.setRepairCost(Double.parseDouble(costField.getText().trim()));
            } catch (NumberFormatException e) {
                request.setRepairCost(0.0);
            }

            dao.update(request);

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Успех");
            alert.setHeaderText("Изменения сохранены");
            alert.showAndWait();

            closeWindow();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}