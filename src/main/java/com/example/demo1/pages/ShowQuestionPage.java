package com.example.demo1.pages;

import com.example.demo1.datatypes.*;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class ShowQuestionPage {
    @FXML
    private ListView<Question> questionListView;
    @FXML
    private Button submitButton;
    private AnchorPane mainContentArea;
    private Course currentCourse;
    private Quiz currentQuiz;
    private int quiztime;
    static private Timeline quizTimer;
    static private int time_second;
    @FXML
    private Label timeLabel;
    private HashMap<Question, Option> optionMap;
    ObservableList<Question> questions = FXCollections.observableArrayList();
    public static void setTime(int time) {
        ShowQuestionPage.time_second = time;
    }
    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void startQuizTimer() {
        if(quizTimer!=null){
            quizTimer.stop();
            quizTimer=null;
        }
        timeLabel.setText(formatTime(time_second)); // Set initial value
        quizTimer = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    time_second--;
                    timeLabel.setText(formatTime(time_second));
                    if (time_second <= 0) {
                        quizTimer.stop();
                        timeLabel.setText("time finished.");
                        try {
                            handleSubmitQuiz(null);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
        );
        quizTimer.setCycleCount(Timeline.INDEFINITE);
        quizTimer.play();
    }
    public void initialize(Course currentCourse, Quiz currentQuiz, AnchorPane mainContentArea) throws FileNotFoundException {
        this.currentCourse = currentCourse;
        this.currentQuiz = currentQuiz;
        this.mainContentArea = mainContentArea;
        this.quiztime= currentQuiz.getTimeLimit();
        if (mainContentArea != null) {
            mainContentArea.getChildren().clear();
        }
        loadQuestions();
        assert mainContentArea != null;
        mainContentArea.getChildren().add(questionListView);
        mainContentArea.getChildren().add(submitButton);
        mainContentArea.getChildren().add(timeLabel);
        timeLabel.setText(formatTime(quiztime));
        submitButton.setText("Submit");
        startQuizTimer();
        optionMap=new HashMap<>();
        for(Question question : questions) {
            optionMap.put(question, null);
        }
    }
    private void loadQuestions() {
        if (currentQuiz == null){
            System.out.println("Current quiz is null, cannot load questions.");
            return;
        }
        if(!questions.isEmpty()){
            questions.clear();
        }
        questions.addAll(currentQuiz.getQuestions());
        questionListView.getItems().clear();
        questionListView.setItems(questions);
        questionListView.setCellFactory(list-> new QuestionCell());
    }
    public void handleSubmitQuiz(ActionEvent actionEvent) throws IOException {
        int mark=0;
        System.out.println("Submitting quiz...");
        for(Question question : questions) {
            Option selectedOption = optionMap.get(question);
            if (selectedOption != null) {
                System.out.println("Question: " + question.getQuestionText() + ", Selected Option: " + selectedOption.getText());
                if(selectedOption.isCorrect()){
                    mark++;
                }
            } else {
                System.out.println("Question: " + question.getQuestionText() + ", No option selected.");
            }
        }
        System.out.println("Quiz submitted. Total marks: " + mark + "/" + questions.size());
        currentQuiz.writeResult(User.getCurrentUser(), currentCourse, currentQuiz.getQuizId(), mark);
        Object controller = loadView("quiz_page.fxml");
        if (controller instanceof QuizPage quizController) {
            quizController.initData(currentCourse, mainContentArea);
        }
        if(quizTimer!=null){
            quizTimer.stop();
            quizTimer=null;
        }
    }

    public class QuestionCell extends ListCell<Question> {
        private VBox container = new VBox();
        private Label questionLabel = new Label();
        private VBox optionsContainer = new VBox();
        private ToggleButton optionButton1 = new ToggleButton();
        private ToggleButton optionButton2 = new ToggleButton();
        private ToggleButton optionButton3 = new ToggleButton();
        private ToggleButton optionButton4 = new ToggleButton();
        private ToggleGroup toggleGroup = new ToggleGroup();

        public QuestionCell() {
            // questionListView.getStyleClass().add("question-list-view"); // Commented out: undefined here; move to your ListView setup if needed
            questionLabel.setWrapText(true);

            optionButton1.setToggleGroup(toggleGroup);
            optionButton2.setToggleGroup(toggleGroup);
            optionButton3.setToggleGroup(toggleGroup);
            optionButton4.setToggleGroup(toggleGroup);

            optionsContainer.getChildren().addAll(optionButton1, optionButton2, optionButton3, optionButton4);
            optionsContainer.setSpacing(5);
            optionsContainer.setPadding(new Insets(8, 0, 0, 16));

            container.getChildren().addAll(questionLabel, optionsContainer);
            questionLabel.getStyleClass().add("question-label");
            container.setSpacing(8);
            container.setPadding(new Insets(12));

            optionButton1.getStyleClass().add("option-label");
            optionButton2.getStyleClass().add("option-label");
            optionButton3.getStyleClass().add("option-label");
            optionButton4.getStyleClass().add("option-label");
            optionsContainer.getStyleClass().add("options-container");
            container.getStyleClass().add("question-cell");
        }

        @Override
        protected void updateItem(Question question, boolean empty) {
            super.updateItem(question, empty);

            if (empty || question == null) {
                setGraphic(null);
            } else {
                questionLabel.setText("Q: " + question.getQuestionText());
                toggleGroup.selectToggle(null); // Deselect all to start fresh
                optionButton1.setText("A) " + question.getOption1().getText());
                optionButton2.setText("B) " + question.getOption2().getText());
                optionButton3.setText("C) " + question.getOption3().getText());
                optionButton4.setText("D) " + question.getOption4().getText());

                optionButton1.setOnAction(event -> handleOptionSelected(question, question.getOption1(), optionButton1));
                optionButton2.setOnAction(event -> handleOptionSelected(question, question.getOption2(), optionButton2));
                optionButton3.setOnAction(event -> handleOptionSelected(question, question.getOption3(), optionButton3));
                optionButton4.setOnAction(event -> handleOptionSelected(question, question.getOption4(), optionButton4));

                setGraphic(container);
            }
        }

        private void handleOptionSelected(Question question, Option selectedOption, ToggleButton optionButton) {
            if (optionButton.isSelected()) {
                System.out.println("Selected option for question '" + question.getQuestionText() + "': " + selectedOption.getText());
                optionMap.put(question, selectedOption);
            } else {
                System.out.println("Deselected option for question '" + question.getQuestionText() + "': " + selectedOption.getText());
            }
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