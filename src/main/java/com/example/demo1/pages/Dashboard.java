package com.example.demo1.pages;

import com.example.demo1.datatypes.Course;
import com.example.demo1.datatypes.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Dashboard implements Initializable {

    private User currentUser;

    public VBox profileView;
    public AnchorPane contentArea;
    public ListView<String> sideListView;

    public void loadView(String fileName)  {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo1/" + fileName));
            AnchorPane view = fxmlLoader.load();
            if ("Profile.fxml".equals(fileName)) {
                ProfileController controller = fxmlLoader.getController();
                controller.initData(currentUser);
            }
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showProfile(ActionEvent actionEvent) {
        loadView("Profile.fxml");
    }

    public void showTeams(ActionEvent actionEvent) {
        contentArea.getChildren().clear();
    }

    public void showChats(ActionEvent actionEvent) {
        contentArea.getChildren().clear();
    }

    public void logout(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User.loadUsers();
        Course.loadCourses();
        currentUser = User.findUser("2305078");
        showProfile(null);
        System.out.println("Current User: " + currentUser.getName());
    }
}
