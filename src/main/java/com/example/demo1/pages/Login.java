package com.example.demo1.pages;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Login implements Initializable {

    @FXML
    public Label errorLabel;
    public HBox appNameHub;

    @FXML
    private TextField userIdField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox teacherCheckBox;

    @FXML
    private CheckBox studentCheckBox;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink createAccountLink;

    private final String[] roles = {"Teacher", "Student"};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        studentCheckBox.setSelected(true);
    }

    @FXML
    void handleLoginButtonAction(ActionEvent event) {
        String userId = userIdField.getText();
        String password = passwordField.getText();
        String role = studentCheckBox.isSelected() ? "Student" : "Teacher";
        loginButton.setDisable(true);

        if (userId.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Required fields are empty.");
            loginButton.setDisable(false);
            return;
        }else{
            try (Socket socket = new Socket("localhost", 6000);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out.println("LOGIN");
                out.println(userId);
                out.println(password);
                out.println(role);

                String response = in.readLine();
                if(response.equals("YES")){
                    errorLabel.setText("Login successful!");
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo1/Dashboard.fxml"));
                        Parent root = fxmlLoader.load();
                        Stage stage = (Stage) createAccountLink.getScene().getWindow();

                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/demo1/styles.css")).toExternalForm());

                        stage.setScene(scene);

                    } catch (IOException e) {
                        errorLabel.setText("Failed to load create account page.");
                    }
                }else{
                    errorLabel.setText("Invalid Info.");
                }

            } catch (IOException e) {
                errorLabel.setText("Server connection failed.");
            } finally {
                loginButton.setDisable(false);
            }
        }
    }

    @FXML
    void handleClearButtonAction(ActionEvent event) {
        userIdField.clear();
        passwordField.clear();
        errorLabel.setText("");
    }

    @FXML
    void handleCreateAccountLinkAction(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo1/create_account_page.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) createAccountLink.getScene().getWindow();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/demo1/styles.css")).toExternalForm());

            stage.setScene(scene);

        } catch (IOException e) {
            errorLabel.setText("Failed to load create account page.");
        }
    }

    @FXML
    void handlePersonIsTeacher(ActionEvent event) {
        if (teacherCheckBox.isSelected()) {
            studentCheckBox.setSelected(false);
        } else if (studentCheckBox.isSelected()) {
            teacherCheckBox.setSelected(false);
        }
    }

    @FXML
    void handlePersonIsStudent(ActionEvent event) {
        if (studentCheckBox.isSelected()) {
            teacherCheckBox.setSelected(false);
        } else if (teacherCheckBox.isSelected()) {
            studentCheckBox.setSelected(false);
        }
    }
}
