package com.pecenio.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the splash screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/splash_screen.fxml"));
        Scene scene = new Scene(loader.load());
        
        // Configure the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cricket Help Desk");
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
