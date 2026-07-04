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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;

public class CreateQuizPage {
    public CheckBox optionCheckBox1;
    public CheckBox optionCheckBox2;
    public CheckBox optionCheckBox3;
    public CheckBox optionCheckBox4;
    private AnchorPane anchorPane;
    public Course selectedCourse;
    public TextField quizNameField;
    public TextField quizTopicField;
    public TextField quizDurationField;
    public TextField quizMarksField;
    public Button createQuizButton;
    @FXML
    private TextField questionField;
    @FXML
    private TextField optionField1;
    @FXML
    private TextField optionField2;
    @FXML
    private TextField optionField3;
    @FXML
    private TextField optionField4;
    @FXML
    private Button nextButton;
    public int questionCount = 0;
    public int counter=1;
    @FXML
    public void initialize() {
        questionField.setVisible(false);
        optionField1.setVisible(false);
        optionField2.setVisible(false);
        optionField3.setVisible(false);
        optionField4.setVisible(false);
        nextButton.setVisible(false);
        optionCheckBox2.setVisible(false);
        optionCheckBox3.setVisible(false);
        optionCheckBox4.setVisible(false);
        optionCheckBox1.setVisible(false);
        quizNameField.requestFocus();
    }
    public void setAnchorPane(AnchorPane anchorPane) {
        this.anchorPane = anchorPane;
    }

    public void setCourse(Course course) {
        this.selectedCourse = course;
    }
    @FXML
    private void handleNextButton(ActionEvent actionEvent) throws Exception {
        String question = questionField.getText();
        String option1 = optionField1.getText();
        String option2 = optionField2.getText();
        String option3 = optionField3.getText();
        String option4 = optionField4.getText();
        String path = "assets/course/" + selectedCourse.getCourseId() + "/" + quizNameField.getText() + "/quiz.txt";
        try(socketWrap socket = new socketWrap(Info.ip, Info.port)) {
            socket.writeLine("ADD_QUESTION");
            socket.writeLine(path);
            System.out.println(path);
            socket.writeLine(counter+ "q_" + question);
            System.out.println("Question sent: " + question);
            System.out.println(getOptionLine(optionCheckBox1, option1, counter, 1));
            socket.writeLine(getOptionLine(optionCheckBox1, option1, counter, 1) );
            System.out.println(getOptionLine(optionCheckBox2, option2, counter, 2));
            socket.writeLine(getOptionLine(optionCheckBox2, option2, counter, 2) );
            System.out.println(getOptionLine(optionCheckBox3, option3, counter, 3));
            socket.writeLine(getOptionLine(optionCheckBox3, option3, counter, 3) );
            System.out.println(getOptionLine(optionCheckBox4, option4, counter, 4));
            socket.writeLine(getOptionLine(optionCheckBox4, option4, counter, 4));
            counter++;
        }
        questionField.clear();
        optionField1.clear();
        optionField2.clear();
        optionField3.clear();
        optionField4.clear();
        questionField.requestFocus();
        System.out.println("Question and options saved: " + question + ", " + option1 + ", " + option2 + ", " + option3 + ", " + option4);
        questionCount--;
        System.out.println("Remaining questions to add: " + questionCount);
        if(questionCount == 0) {
            Object controller = loadView("quiz_page.fxml");
            if(controller instanceof QuizPage quizController) {
                quizController.initData(selectedCourse, anchorPane);
            }
        }
    }
    public String getOptionLine(CheckBox checkBox, String option, int index, int optionCount) {
        if(checkBox.isSelected()) {
            return index + "o" + optionCount + "_true_" + option ;
        }else{
            return index + "o" + optionCount + "_false_" + option ;
        }
    }
    public void constructQuiz(ActionEvent actionEvent) {
        String quizName = quizNameField.getText();
        String quizTopic = quizTopicField.getText();
        int quizDuration = Integer.parseInt(quizDurationField.getText());
        int quizMarks = Integer.parseInt(quizMarksField.getText());
        questionCount=quizMarks;
        String path="assets/course/" + selectedCourse.getCourseId() + "/"+ quizName;
        String chatpath = "assets/course/" + selectedCourse.getCourseId() + "/chats.txt";
        try(socketWrap socket = new socketWrap(Info.ip, Info.port)) {
            socket.writeLine("CREATE_QUIZ");
            socket.writeLine(path);
            socket.writeLine(quizName + "_" + quizTopic + "_" + quizMarks + "_" + quizDuration);
            socket.writeLine(chatpath);
        } catch (Exception e) {
            System.out.println("Error creating quiz: " + e.getMessage());
        }
        createQuizButton.setDisable(true);
        questionField.setVisible(true);
        optionField1.setVisible(true);
        optionField2.setVisible(true);
        optionField3.setVisible(true);
        optionField4.setVisible(true);
        nextButton.setVisible(true);
        optionCheckBox2.setVisible(true);
        optionCheckBox3.setVisible(true);
        optionCheckBox4.setVisible(true);
        optionCheckBox1.setVisible(true);
    }
    public Object loadView(String fileName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo1/" + fileName));
            Parent view = fxmlLoader.load();
            anchorPane.getChildren().setAll(view);
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
    public void handleCheckButton1(ActionEvent actionEvent) {
        if (optionCheckBox1.isSelected()) {
            System.out.println("CheckButton1 is selected");
        }
        optionCheckBox2.setSelected(false);
        optionCheckBox3.setSelected(false);
        optionCheckBox4.setSelected(false);
    }
    public void handleCheckButton2(ActionEvent actionEvent) {
        if (optionCheckBox2.isSelected()) {
            System.out.println("CheckButton2 is selected");
        }
        optionCheckBox1.setSelected(false);
        optionCheckBox3.setSelected(false);
        optionCheckBox4.setSelected(false);
    }
    public void handleCheckButton3(ActionEvent actionEvent) {
        if (optionCheckBox3.isSelected()) {
            System.out.println("CheckButton3 is selected");
        }
        optionCheckBox1.setSelected(false);
        optionCheckBox2.setSelected(false);
        optionCheckBox4.setSelected(false);
    }
    public void handleCheckButton4(ActionEvent actionEvent) {
        if (optionCheckBox4.isSelected()) {
            System.out.println("CheckButton4 is selected");
        }
        optionCheckBox1.setSelected(false);
        optionCheckBox2.setSelected(false);
        optionCheckBox3.setSelected(false);
    }
    public void UserPressEnter(ActionEvent actionEvent) {
        if(actionEvent.getSource().equals(quizNameField)) {
            quizTopicField.requestFocus();
        } else if(actionEvent.getSource().equals(quizTopicField)) {
            quizDurationField.requestFocus();
        } else if(actionEvent.getSource().equals(quizDurationField)) {
            quizMarksField.requestFocus();
        } else if(actionEvent.getSource().equals(quizMarksField)) {
            constructQuiz(actionEvent);
        }
    }
}
