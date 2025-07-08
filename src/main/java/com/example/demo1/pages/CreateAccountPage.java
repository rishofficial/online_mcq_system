package com.example.demo1.pages;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CreateAccountPage implements Initializable {
    public TextField nameField;
    public TextField ageField;
    public TextField userIdField;
    public PasswordField passwordField;
    public PasswordField confirmPasswordField;
    public CheckBox teacherCheckBox;
    public CheckBox studentCheckBox;
    public Button createAccountButton;
    public Label errorLabel;
    public Hyperlink backToLoginLink;

    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public void handlePersonIsTeacher(ActionEvent actionEvent) {
        if (teacherCheckBox.isSelected()) {
            studentCheckBox.setSelected(false);
        } else if (studentCheckBox.isSelected()) {
            teacherCheckBox.setSelected(false);
        }
    }

    public void handlePersonIsStudent(ActionEvent actionEvent) {
        if (studentCheckBox.isSelected()) {
            teacherCheckBox.setSelected(false);
        } else if (teacherCheckBox.isSelected()) {
            studentCheckBox.setSelected(false);
        }
    }

    public void handleCreateAccountButtonAction(ActionEvent actionEvent) {
        String name = nameField.getText();
        String age = ageField.getText();
        String userId = userIdField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = teacherCheckBox.isSelected() ? "Teacher" : "Student";
        if( name.isEmpty() || age.isEmpty() || userId.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }else if( !password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }
        System.out.println("Creating account for: " + name + ", Age: " + age + ", UserID: " + userId + ", Role: " + role);
        createAccountButton.setDisable(false);
         try (Socket socket = new Socket("localhost", 6000);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()))) {
             out.println("CREATE");
             out.println(name);
             out.println(age);
             out.println(userId);
             out.println(password);
             out.println(confirmPassword);
             out.println(role);

             String response = in.readLine();
             if(response.equals("YES")){
                errorLabel.setText("Account Successfully Created!");
             }else{
                errorLabel.setText("UserID already exist.");
             }

        } catch (IOException e) {
            errorLabel.setText("Server connection failed.");
        } finally {
            createAccountButton.setDisable(false);
        }
    }

    public void handleBackToLoginLinkAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/login_page.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) backToLoginLink.getScene().getWindow();
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/demo1/styles.css")).toExternalForm());
            stage.setScene(scene);
        } catch (java.io.IOException e) {
            errorLabel.setText("Failed to load login page.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        teacherCheckBox.setSelected(false);
        studentCheckBox.setSelected(true);
        errorLabel.setText("");
        createAccountButton.setDisable(false);
    }
}
