package com.example.demo1.pages;

import com.example.demo1.datatypes.Course;
import com.example.demo1.datatypes.User;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;


public class ProfileController {
    public GridPane enrolledCoursesGridPane;
    @FXML
    private Label nameLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label userIdLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private AnchorPane contentArea;

    private User currentUser;

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

    public void initData(User currentUser) {
        this.currentUser = currentUser;
        nameLabel.setText(currentUser.getName());
        ageLabel.setText(String.valueOf(currentUser.getAge()));
        userIdLabel.setText(currentUser.getUserId());
        roleLabel.setText(currentUser.getRole());
        handleViewCourses();
    }

    public void handleViewCourses() {
        currentUser.loadCourses();
        List<Course> courses;
        if(enrolledCoursesGridPane.getChildren().size()>1){
            enrolledCoursesGridPane.getChildren().remove(1, enrolledCoursesGridPane.getChildren().size());
        }
        courses = new java.util.ArrayList<>(new java.util.HashSet<>(currentUser.getCourses()));
        if(!courses.isEmpty()) {
            int row=0;
            for(Course course : courses) {
                Label courseLabel = new Label(course.getCourseName() + "       " + course.getCourseId() );
                enrolledCoursesGridPane.add(courseLabel, 1, row++);
            }
        } else {
            enrolledCoursesGridPane.add(new Label("No courses enrolled."), 1, 0);
        }
    }

    public void handleAddDropCourses(ActionEvent actionEvent) {
        Object controller =  loadView("AddDropCourses.fxml");
        if(controller instanceof AddDropCoursesController) {
            ((AddDropCoursesController) controller).initData(currentUser, contentArea);
        }
    }
}
