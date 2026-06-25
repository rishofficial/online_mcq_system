package com.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class BuetHub extends Application {
    @Override
    public void start(Stage stage) throws IOException{
        {
            FXMLLoader fxmlLoader = new FXMLLoader(BuetHub.class.getResource("login_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);

            String css=Objects.requireNonNull(this.getClass().getResource("styles.css")).toExternalForm();
            scene.getStylesheets().add(css);

            stage.setTitle("BUET HUB - Login");
            stage.setScene(scene);
        }
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
