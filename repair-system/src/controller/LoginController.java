package controller;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Role;
import model.User;
import util.SessionManager;

public class LoginController {
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;

    @FXML private VBox registerBox;
    @FXML private TextField regLoginField;
    @FXML private PasswordField regPasswordField;
    @FXML private TextField regNameField;
    @FXML private TextField regPhoneField;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        if (registerBox != null) registerBox.setVisible(false);
    }

    @FXML
    private void login() {
        String login = loginField.getText().trim();
        String pass = passwordField.getText().trim();

        if (login.isEmpty() || pass.isEmpty()) {
            showAlert("Заполните логин и пароль");
            return;
        }

        try {
            User user = userDAO.authenticate(login, pass);
            if (user == null) {
                showAlert("Неверный логин или пароль");
                return;
            }

            SessionManager.setCurrentUser(user);
            openMainWindow();

        } catch (Exception e) {
            showAlert("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showRegister() {
        registerBox.setVisible(true);
    }

    @FXML
    private void register() {
        String login = regLoginField.getText().trim();
        String pass = regPasswordField.getText().trim();
        String name = regNameField.getText().trim();
        String phone = regPhoneField.getText().trim();

        if (login.isEmpty() || pass.isEmpty() || name.isEmpty()) {
            showAlert("Заполните логин, пароль и имя");
            return;
        }

        User user = new User();
        user.setUsername(login);
        user.setPassword(pass);
        user.setRole(Role.CLIENT);
        user.setFullName(name);
        user.setPhone(phone);

        try {
            userDAO.create(user);
            showAlert("Аккаунт создан! Теперь войдите");
            registerBox.setVisible(false);
            loginField.setText(login);
        } catch (Exception e) {
            showAlert("Ошибка регистрации (возможно, логин занят): " + e.getMessage());
        }
    }

    private void openMainWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Система учёта ремонта техники");
        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();

        Stage loginStage = (Stage) loginField.getScene().getWindow();
        loginStage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}