package controller;

import dao.RepairRequestDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.RepairRequest;
import model.User;
import util.SessionManager;

public class CreateRequestController {
    @FXML private TextField clientNameField;
    @FXML private TextField clientPhoneField;
    @FXML private TextField deviceTypeField;
    @FXML private TextField deviceModelField;
    @FXML private TextField serialNumberField;
    @FXML private TextArea problemDescriptionArea;

    private Runnable onSaveCallback;
    private final RepairRequestDAO dao = new RepairRequestDAO();

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    private void saveRequest() {
        if (!validate()) return;

        RepairRequest request = new RepairRequest(
                clientNameField.getText().trim(),
                clientPhoneField.getText().trim(),
                deviceTypeField.getText().trim(),
                deviceModelField.getText().trim(),
                serialNumberField.getText().trim(),
                problemDescriptionArea.getText().trim()
        );

        // Гарантированно ставим статус
        request.setStatus(model.Status.NEW);

        // Привязываем к текущему клиенту
        User current = util.SessionManager.getCurrentUser();
        if (current != null) {
            request.setClientId(current.getId());
            System.out.println("Клиент ID: " + current.getId()); // для проверки
        } else {
            System.out.println("ОШИБКА: пользователь не в сессии!");
        }

        try {
            dao.create(request);
            showAlert(Alert.AlertType.INFORMATION, "Успех", "Заявка создана",
                    "Идентификационный номер заявки: " + request.getId());

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            closeWindow();
        } catch (Exception e) {
            // ВАЖНО: печатаем полную ошибку в консоль
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка SQL", "Не удалось создать заявку",
                    e.getMessage());
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private boolean validate() {
        if (clientNameField.getText().trim().isEmpty() ||
                clientPhoneField.getText().trim().isEmpty() ||
                deviceTypeField.getText().trim().isEmpty() ||
                problemDescriptionArea.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Внимание", "Заполните обязательные поля",
                    "Имя клиента, телефон, тип устройства и описание проблемы обязательны");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) clientNameField.getScene().getWindow();
        stage.close();
    }
}