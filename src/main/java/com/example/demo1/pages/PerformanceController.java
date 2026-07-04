package com.example.demo1.pages;

import com.example.demo1.datatypes.Course;
import com.example.demo1.datatypes.Quiz;
import com.example.demo1.datatypes.User;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerformanceController {
    public Button backButton;
    private AnchorPane contentArea;
    @FXML private Label titleLabel;
    @FXML private Label quizNameLabel;
    @FXML private Label timeLabel;
    @FXML private TableView<PerformanceRow> performanceTable;
    @FXML private TableColumn<PerformanceRow, Number> numberCol;
    @FXML private TableColumn<PerformanceRow, String> nameCol;
    @FXML private TableColumn<PerformanceRow, String> userIdCol;
    @FXML private TableColumn<PerformanceRow, String> markCol;

    private Course currentCourse;
    private Quiz currentQuiz;

    @FXML
    public void initialize() {
        numberCol.setCellValueFactory(data -> data.getValue().numberProperty());
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        userIdCol.setCellValueFactory(data -> data.getValue().userIdProperty());
        markCol.setCellValueFactory(data -> data.getValue().markProperty());
        performanceTable.setItems(FXCollections.observableArrayList());
        performanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    public void init(Course course, Quiz quiz, AnchorPane contentArea) {
        this.contentArea = contentArea;
        this.currentCourse = course;
        this.currentQuiz = quiz;
        this.titleLabel.setText(course.getCourseId() + " _ " + course.getCourseName());
        this.quizNameLabel.setText(quiz.getTopicName());
        this.timeLabel.setText("Time: " + quiz.getTimeLimit() + " seconds");
        loadPerformanceData();
    }

    private void loadPerformanceData() {
        List<PerformanceRow> rowData = new ArrayList<>();
        List<String> list = currentQuiz.getResult(currentCourse, currentQuiz.getQuizId());
        int counter = 1;
        for (String str: list) {
            String[] parts = str.split("_", 2);
            if (parts.length == 2) {
                User user = User.findUser(parts[0]);
                if (user != null) {
                    rowData.add(new PerformanceRow(counter++, user.getName(), user.getUserId(), parts[1]));
                }
            }
        }
        performanceTable.setItems(FXCollections.observableArrayList(rowData));
    }

    public void handleBackButtonAction(ActionEvent actionEvent) {
        Object controller = loadView("quiz_page.fxml");
        if (controller instanceof QuizPage quizController) {
            quizController.initData(currentCourse, contentArea);
        }
    }
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

    public static class PerformanceRow {
        private final SimpleIntegerProperty number;
        private final SimpleStringProperty name;
        private final SimpleStringProperty userId;
        private final SimpleStringProperty mark;

        public PerformanceRow(int number, String name, String userId, String mark) {
            this.number = new SimpleIntegerProperty(number);
            this.name = new SimpleStringProperty(name);
            this.userId = new SimpleStringProperty(userId);
            this.mark = new SimpleStringProperty(mark);
        }
        public SimpleIntegerProperty numberProperty() { return number; }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty userIdProperty() { return userId; }
        public SimpleStringProperty markProperty() { return mark; }
    }
}
