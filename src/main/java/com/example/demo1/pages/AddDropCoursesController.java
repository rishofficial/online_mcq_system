package com.example.demo1.pages;

import com.example.demo1.datatypes.Course;
import com.example.demo1.datatypes.Info;
import com.example.demo1.datatypes.User;
import com.example.demo1.threads.socketWrap;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class AddDropCoursesController {

    public BorderPane contentArea;
    @FXML
    private ListView<Course> coursesListView;
    @FXML
    private Button updateButton;
    private AnchorPane mainContentArea;
    @FXML
    private User currentUser;
    private List<Course> allCourses = new ArrayList<>();
    private Set<Course> selectedCourses = new HashSet<>();

    public void initData(User user, AnchorPane contentArea) {
        this.mainContentArea=contentArea;
        this.currentUser = user;
        currentUser.loadCourses();
        this.selectedCourses = new HashSet<>(currentUser.getCourses());
        allCourses=Course.getCourses();
        coursesListView.setMaxWidth(Double.MAX_VALUE);
        displayCourses();
    }

    public void displayCourses() {
        coursesListView.getItems().clear();
        allCourses = new ArrayList<>(new HashSet<>(Course.getCourses()));
        coursesListView.getItems().addAll(allCourses);
        coursesListView.setCellFactory(ListView-> new ListCell<Course>() {
            private VBox content=new VBox();
            private Label courseNameLabel = new Label();
            private Label courseIdLabel = new Label();
            {
                content.getChildren().addAll(courseNameLabel, courseIdLabel);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    courseNameLabel.setText(course.getCourseName());
                    courseIdLabel.setText(course.getCourseId());
                    content.getStyleClass().add("course-cell");
                    setGraphic(content);
                    if (selectedCourses.contains(course)) {
                        courseNameLabel.getStyleClass().add("course-selected");
                        courseIdLabel.getStyleClass().remove("course-not-selected");
                    } else {
                        courseNameLabel.getStyleClass().add("course-not-selected");
                        courseIdLabel.getStyleClass().remove("course-selected");
                    }
                    setOnMouseClicked(event -> handleCourseSelection(event, course));
                }
            }

            void handleCourseSelection(MouseEvent event, Course course) {
                if (selectedCourses.contains(course)) {
                    selectedCourses.remove(course);
                    getStyleClass().remove("course-selected");
                    getStyleClass().add("course-not-selected");
                } else {
                    selectedCourses.add(course);
                    getStyleClass().remove("course-not-selected");
                    getStyleClass().add("course-selected");
                }
            }
        });
    }

    public void handleUpdateCourses(ActionEvent actionEvent) {
        StringBuilder hello= new StringBuilder();
        for(Course course : selectedCourses) {
            hello.append(course.getCourseId()).append(",");
        }
        try(socketWrap socketWrap = new socketWrap(Info.ip , Info.port)) {
            socketWrap.writeLine("SHOW_COURSES");
            socketWrap.writeLine(currentUser.getUserId());
            socketWrap.writeLine(hello.toString());
            socketWrap.writeLine(currentUser.getRole());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        for(Course course : selectedCourses) {
            if(!course.getStudents().contains(currentUser)) {
                course.getStudents().add(User.getCurrentUser());
            }
            if(!course.getTeachers().contains(currentUser)) {
                course.getTeachers().add(User.getCurrentUser());
            }
            if (!currentUser.getCourses().contains(course)) {
                currentUser.getCourses().add(course);
            }
        }
        List<Course> coursesToIterate = new ArrayList<>(currentUser.getCourses());
        for(Course course : coursesToIterate) {
            if (!selectedCourses.contains(course)) {
                if(course.getStudents().contains(currentUser)) {
                    course.getStudents().remove(currentUser);
                }
                if(course.getTeachers().contains(currentUser)) {
                    course.getTeachers().remove(currentUser);
                }
                currentUser.getCourses().remove(course);
            }
        }
        Object object = loadView("Profile.fxml");
        if (object instanceof ProfileController profileController) {
            profileController.initData(currentUser);
        }
    }
    public Object loadView(String fileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo1/" + fileName));
            Parent view = fxmlLoader.load();
            mainContentArea.getChildren().setAll(view);
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
}