package com.example.demo1.pages;

import com.example.demo1.datatypes.Course;
import com.example.demo1.datatypes.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

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
    private VBox addDropCoursesArea; // This is the empty area

    private User currentUser;

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
        List<Course> courses = currentUser.getCourses();
        if(enrolledCoursesGridPane.getChildren().size()>1){
            enrolledCoursesGridPane.getChildren().remove(1, enrolledCoursesGridPane.getChildren().size());
        }
        if(courses!=null && !courses.isEmpty()) {
            int row=0;
            for(Course course : courses) {
                Label courseLabel = new Label(course.getCourseName() + "       " + course.getCourseID() );
                enrolledCoursesGridPane.add(courseLabel, 1, row++);
            }
        } else {
            enrolledCoursesGridPane.add(new Label("No courses enrolled."), 0, 0);
        }
    }

    public void handleAddDropCourses(ActionEvent actionEvent) {
    }
}
