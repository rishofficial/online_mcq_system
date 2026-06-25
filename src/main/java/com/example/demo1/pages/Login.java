package com.example.demo1.pages;

import com.example.demo1.datatypes.Course;
import com.example.demo1.Animation.Fade;
import com.example.demo1.datatypes.Info;
import com.example.demo1.datatypes.User;
import com.example.demo1.threads.socketWrap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class Login implements Initializable {
    @FXML
    public Label errorLabel;
    public Button clearButton;
    public HBox appNameLabel;
    public HBox appMotoLabel;
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
        userIdField.requestFocus();
        Fade.animateLabel(appNameLabel, "BUET HUB", 0.1, 200);
        Fade.animateLabel(appMotoLabel, "Connecting Minds Building Futures", 0.05,100);
    }
    @FXML
    void handleLoginButtonAction(ActionEvent event) {
        userIdField.setText("1905078");
        passwordField.setText("25253535");
        String userId = userIdField.getText();
        String password = passwordField.getText();
        String role = studentCheckBox.isSelected() ? "Student" : "Teacher";
        loginButton.setDisable(true);
        if (userId.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Required fields are empty.");
            loginButton.setDisable(false);
            return;
        }else{
            String response="NO";
            try (socketWrap socket=new socketWrap(Info.ip , Info.port)) {
                socket.writeLine("LOGIN");
                socket.writeLine(userId);
                socket.writeLine(password);
                socket.writeLine(role);

                response = socket.readLine();
                if(response.equals("YES")){
                    errorLabel.setText("Login successful!");
                    try {
                        User.loadUsers();
                        System.out.println("Load user"+ User.getUsers().isEmpty());
                        User.setcurrentUser(User.findUser(userId));
                        Course.loadCourses();
                        System.out.println("Courses loaded" + Course.getCourses().isEmpty());
                        User.getCurrentUser().loadChats();
                        System.out.println("chats loaded" + User.getCurrentUser().getChats().isEmpty());
                        User.getCurrentUser().loadCourses();
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo1/Dashboard.fxml"));
                        Parent root = fxmlLoader.load();
                        Stage stage = (Stage) createAccountLink.getScene().getWindow();
                        Dashboard dashboard = fxmlLoader.getController();
                        System.out.println("courses loaded of the user" + User.getCurrentUser().getCourses().isEmpty());
                        if(User.getCurrentUser() != null){
                            dashboard.showProfile(null);
                        }else{
                            System.out.println("Current user is null, loading default dashboard.");
                        }
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/demo1/styles.css")).toExternalForm());
                        stage.setScene(scene);

                    } catch (IOException e) {
                        errorLabel.setText("Failed to load dashboard page.");
                    }
                }else{
                    errorLabel.setText("Invalid Info.");
                }

            } catch (IOException e) {
                errorLabel.setText("Server connection failed.");
            } catch (Exception e) {
                throw new RuntimeException(e);
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
    public void UserPressEnter(ActionEvent actionEvent) {
        if (actionEvent.getSource() == userIdField) {
            passwordField.requestFocus();
        } else if (actionEvent.getSource() == passwordField) {
            handleLoginButtonAction(actionEvent);
        }
    }
}
