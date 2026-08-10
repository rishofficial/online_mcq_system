package com.example.demo1.Animation;
import com.example.demo1.datatypes.Course;
import com.example.demo1.datatypes.User;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.List;

public class Fade {
    public static void animateLabel(HBox parent, String message, double interval, double fadeMillis) {
        parent.getChildren().clear();
        final int[] index = {0};

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(interval), event -> {
            if (index[0] < message.length()) {
                String letter = String.valueOf(message.charAt(index[0]));
                Label letterLabel = new Label(letter);
                letterLabel.setOpacity(0.0);
                parent.getChildren().add(letterLabel);

                FadeTransition fade = new FadeTransition(Duration.millis(fadeMillis), letterLabel);
                fade.setFromValue(0.0);
                fade.setToValue(1.0);
                fade.play();

                index[0]++;
            }
        }));
        timeline.setCycleCount(message.length());
        timeline.play();
    }
    public static void ListViewSwap(ListView<Object> listView, List<User> userList, List<Course> courseList, String type, double fadeMillis) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(fadeMillis), listView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            listView.getItems().clear();
            listView.refresh();
            if(type.equals("User") && !userList.isEmpty()) {
                listView.getItems().addAll(userList);
            } else if (type.equals("Course") && !courseList.isEmpty()) {
                listView.getItems().addAll(courseList);
            }

            FadeTransition fadeIn = new FadeTransition(Duration.millis(fadeMillis), listView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }
}
