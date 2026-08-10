package com.example.demo1.pages;
import com.example.demo1.Animation.Border;
import com.example.demo1.datatypes.Course;
import com.example.demo1.Animation.Fade;
import com.example.demo1.datatypes.User;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Dashboard implements Initializable {
    public AnchorPane contentArea;
    public ListView<Object> sideListView;
    public TextField SearchField;
    public User selectedUser=null;
    public Object loadView(String fileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo1/" + fileName));
            Parent view = fxmlLoader.load();
            contentArea.getChildren().setAll(view);
            FadeTransition ft = new FadeTransition(Duration.millis(500), view);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
            return fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void showProfile(ActionEvent actionEvent) {
        contentArea.getChildren().clear();
        Object controller = loadView("Profile.fxml");
        if(controller instanceof ProfileController) {
            ((ProfileController) controller).initData(User.getCurrentUser());
        }
    }
    public void showTeams(ActionEvent actionEvent) throws IOException {
        contentArea.getChildren().clear();
        if(!User.getCurrentUser().getCourses().isEmpty()) {
            User.getCurrentUser().getCourses().clear();
            User.getCurrentUser().loadCourses();
        }
        for(Course course : User.getCurrentUser().getCourses()) {
            System.out.println("Course: " + course.getCourseId() + ", Name: " + course.getCourseName());
        }
        showCourses(User.getCurrentUser().getCourses());
    }
    public void showCourses(List<Course> courses) throws IOException {
        Fade.ListViewSwap(sideListView, User.getCurrentUser().getChats(), User.getCurrentUser().getCourses(), "Course", 200);
        sideListView.setPrefHeight(sideListView.getFixedCellSize() * sideListView.getItems().size());
        sideListView.getStyleClass().add("chat-list-view");
        sideListView.setFixedCellSize(-1);
        sideListView.setCellFactory(ListView -> new ListCell<Object>() {
            VBox content = new VBox();
            Label nameLabel = new Label();
            Label userIdLabel = new Label();
            {
                content.getChildren().addAll(nameLabel, userIdLabel);
                content.getStyleClass().add("user-cell");
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            @Override
            protected void updateItem(Object course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                    setGraphic(null);
                    setOnMouseClicked(null);
                } else if( course instanceof Course course_) {
                    nameLabel.setText(course_.getCourseId().replace("_", " "));
                    userIdLabel.setText(course_.getCourseName().replace("_", " "));
                    if (isSelected() || isHover()) {
                        nameLabel.setStyle("-fx-text-fill: #51a3d8;");
                        userIdLabel.setStyle("-fx-text-fill: #51a3d8;");
                    } else {
                        nameLabel.setStyle("-fx-text-fill: #ecf0f1;");
                        userIdLabel.setStyle("-fx-text-fill: #ecf0f1;");
                    }
                    setGraphic(content);
                    setOnMouseClicked(event -> handleShowCourse(event, course_));
                }
            }

            private void handleShowCourse(MouseEvent event, Course course) {
                contentArea.getChildren().clear();
                Object controller = loadView("quiz_page.fxml");
                if (controller instanceof QuizPage quizController) {
                    quizController.initData(course, contentArea);
                }
            }

        });
    }
    public void showChatUsers(List<User> users) {
        Fade.ListViewSwap(sideListView, User.getCurrentUser().getChats(), User.getCurrentUser().getCourses(), "User", 200);
        sideListView.getStyleClass().add("chat-list-view");

        sideListView.setCellFactory(ListView -> new ListCell<Object>() {
            final VBox content = new VBox();
            final Label nameLabel = new Label();
            final Label userIdLabel = new Label();
            {
                content.getChildren().addAll(nameLabel, userIdLabel);
                content.getStyleClass().add("user-cell");
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            protected void updateItem(Object user1, boolean empty) {
                super.updateItem(user1, empty);
                if (empty || user1 == null) {
                    setText(null);
                    setGraphic(null);
                    setOnMouseClicked(null);
                } else if(user1 instanceof User user) {
                    nameLabel.setText(user.getName());
                    userIdLabel.setText(user.getUserId());
                    if (isSelected() || isHover()) {
                        nameLabel.setStyle("-fx-text-fill: #51a3d8;");
                        userIdLabel.setStyle("-fx-text-fill: #51a3d8;");
                    } else {
                        nameLabel.setStyle("-fx-text-fill: #ecf0f1;");
                        userIdLabel.setStyle("-fx-text-fill: #ecf0f1;");
                    }
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(content);
                    setOnMouseClicked(event -> handleShowChat(event, user));
                }
            }
        });

        if (selectedUser != null && users.contains(selectedUser)) {
            int index = users.indexOf(selectedUser);
            sideListView.getSelectionModel().select(index);
            sideListView.scrollTo(index);
            handleShowChat(null, selectedUser);
        }

        sideListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(selectedUser) && newVal instanceof User) {
                selectedUser = (User) newVal;
                handleShowChat(null, selectedUser);
            }
        });
    }

    private void handleShowChat(MouseEvent event, User user) {
        selectedUser=user;
        int index = sideListView.getItems().indexOf(user);
        sideListView.getSelectionModel().select(index);
        sideListView.scrollTo(index);
        contentArea.getChildren().clear();
        Object controller = loadView("chat_window.fxml");
        if (controller instanceof ChatWindow chatWindowController) {
            chatWindowController.initData(User.getCurrentUser(), user);
        }
    }

    // show messages on the right side content area
    public void showChats(ActionEvent actionEvent) throws IOException {
        contentArea.getChildren().clear();
        //User.getCurrentUser().loadChats();
        System.out.println("Showing chats for user: " + User.getCurrentUser().getChats().isEmpty());
        Platform.runLater(() -> {
            showChatUsers(User.getCurrentUser().getChats());
        });
    }

    // log out.
    public void logout(ActionEvent actionEvent) {
        if(User.getCurrentUser() != null) {
            User.setcurrentUser(null);
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/login_page.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) contentArea.getScene().getWindow();
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/demo1/styles.css")).toExternalForm());
            stage.setScene(scene);
        } catch (java.io.IOException e) {
            System.out.println("error loading login page: " + e.getMessage());
        }
    }

    public void searchUser(ActionEvent actionEvent) throws IOException {
        String searchText = SearchField.getText().trim();
        selectedUser = User.findUser(searchText);
        if(User.getCurrentUser().getChats().contains(selectedUser)) {
            showChats(null);
            SearchField.clear();
        } else if(selectedUser != null) {
            User.getCurrentUser().getChats().add(selectedUser);
            showChats(null);
            SearchField.clear();
        } else {
            SearchField.clear();
            SearchField.setText("Error: User not found");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            User.getCurrentUser().loadChats();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        User.getCurrentUser().loadCourses();
    }
}
