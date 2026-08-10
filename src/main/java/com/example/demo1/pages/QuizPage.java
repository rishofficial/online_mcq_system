package com.example.demo1.pages;

import com.example.demo1.datatypes.Course;
import com.example.demo1.datatypes.Info;
import com.example.demo1.datatypes.Quiz;
import com.example.demo1.datatypes.User;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.util.List;

public class QuizPage {

    public Button startQuizButton;
    @FXML
    private ListView<Quiz> quizListView;
    private ObservableList<Quiz> quizzes = FXCollections.observableArrayList();
    private Course selectedCourse;
    private AnchorPane container;

    public void initialize() {
        quizListView.setCellFactory(listView -> new QuizCell());
        quizListView.getStyleClass().add("quiz-list-view");
    }
    public void initData(Course course, AnchorPane container) {
        this.selectedCourse = course;
        loadQuizzesForCourse(course);
        course.loadQuizzes();
        quizListView.getItems().clear();
        quizListView.getItems().addAll(quizzes);
        this.container = container;
        if(User.getCurrentUser().isStudent()){
            startQuizButton.setVisible(false);
            startQuizButton.setDisable(true);
        } else {
            startQuizButton.setVisible(true);
            startQuizButton.setDisable(false);
        }
    }
    private void loadQuizzesForCourse(Course course) {
        quizzes.clear();
        List<Quiz> courseQuizzes = course.getQuizzes();
        if (courseQuizzes != null) {
            quizzes.addAll(courseQuizzes);
        }
    }
    public void createQuiz(ActionEvent actionEvent) {
        Object controller = loadView("create_quiz.fxml");
        if (controller instanceof CreateQuizPage createQuizPage) {
            createQuizPage.setAnchorPane(container);
            createQuizPage.setCourse(selectedCourse);
        }
    }
    private class QuizCell extends ListCell<Quiz> {

        private final Label topicLabel = new Label();
        private final Label questionCountLabel = new Label();
        private final Label timeLimitLabel = new Label();

        private final Button showPerformance = new Button("Show Performance");
        private final Label conditionLabel = new Label();
        private final VBox leftBox = new VBox();
        private final VBox rightBox = new VBox();
        private final Path divider = new Path();
        private final HBox cellBox = new HBox();

        public QuizCell() {
            topicLabel.getStyleClass().add("quiz-title");
            questionCountLabel.getStyleClass().add("quiz-qcount");
            timeLimitLabel.getStyleClass().add("quiz-timelimit");

            leftBox.getChildren().addAll(topicLabel, questionCountLabel, timeLimitLabel);
            leftBox.setSpacing(8);
            leftBox.setMinWidth(500); // Adjust width as needed
            leftBox.setPadding(new Insets(10,0,10,10));

            // Path divider as vertical line
            divider.getElements().addAll(
                    new MoveTo(0, 0),
                    new LineTo(0, 70)
            );
            divider.setStrokeWidth(2);
            divider.setStroke(Color.web("#31445b"));

            showPerformance.getStyleClass().add("show-performance-button");
            rightBox.getChildren().addAll(showPerformance, conditionLabel);
            rightBox.setSpacing(16);
            rightBox.setAlignment(Pos.CENTER);
            rightBox.setPadding(new Insets(0,20,0,20));

            cellBox.getChildren().addAll(leftBox, divider, rightBox);
            cellBox.setSpacing(32);
            cellBox.setAlignment(Pos.CENTER_LEFT);
            cellBox.setStyle("-fx-background-radius: 10;");
        }

        @Override
        protected void updateItem(Quiz quiz, boolean empty) {
            super.updateItem(quiz, empty);
            if (empty || quiz == null) {
                setGraphic(null);
            } else {
                List<String> list = quiz.getResult(selectedCourse, quiz.getQuizId());
                int mark = -1;
                for (String str: list) {
                    String[] parts = str.split("_", 2);
                    if (parts.length == 2) {
                        User user = User.findUser(parts[0]);
                        assert user != null;
                        if(user.getUserId().equals(User.getCurrentUser().getUserId())) {
                            mark= Integer.parseInt(parts[1]);
                        }
                    }
                }
                if(mark==-1){
                    conditionLabel.setText("Not Attempted");
                    conditionLabel.setTextFill(Color.RED);
                } else {
                    conditionLabel.setText("Marks: " + mark);
                    conditionLabel.setTextFill(Color.GREEN);
                }
                topicLabel.setText("Topic: " + quiz.getTopicName());
                questionCountLabel.setText("Questions: " + quiz.getQuestionCount());
                timeLimitLabel.setText("Time: " + quiz.getTimeLimit() + " min");

                showPerformance.setOnAction(event -> {
                    try {
                        Object controller = loadView("performance_view.fxml");
                        if(controller instanceof PerformanceController performanceView) {
                            performanceView.init(selectedCourse, quiz, container);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                setGraphic(cellBox);

                setOnMouseClicked(event -> {
                    try {
                        showQuizDetails(quiz);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }


    private void showQuizDetails(Quiz quiz) throws Exception {
        System.out.println("Showing details for quiz: " + quiz.getTopicName());
            quiz.loadQuestions(selectedCourse.getCourseId(), quiz.getQuizId());
        try{
            Object hello=loadView("show_question.fxml");
            if(hello instanceof ShowQuestionPage controller){
                controller.initialize(selectedCourse, quiz, container);
                ShowQuestionPage.setTime(quiz.getTimeLimit());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Object loadView(String fileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo1/" + fileName));
            Parent view = fxmlLoader.load();
            container.getChildren().setAll(view);
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
