package com.pecenio.frontend.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.net.URL;

public class SceneNavigator {

    /**
     * Switch scene using a direct Stage reference (useful in startup/Main class).
     *
     * @param stage    The current Stage.
     * @param fxmlPath Path to the FXML file (relative to /resources, e.g., "main.fxml").
     * @param title    Window title.
     */
    public static void switchScene(Stage stage, String fxmlPath, String title) {
        try {
            URL resource = SceneNavigator.class.getClassLoader().getResource(fxmlPath);
            if (resource == null) {
                showError("FXML file not found: " + fxmlPath);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to load scene: " + fxmlPath);
        }
    }

    /**
     * Switch scene from a UI element's ActionEvent (like button press).
     *
     * @param fxmlFile File name of the FXML file (e.g., "create_ticket.fxml").
     * @param event    The ActionEvent from a UI component.
     */
    public static void switchTo(String fxmlFile, ActionEvent event) {
        try {
            URL resource = SceneNavigator.class.getClassLoader().getResource(fxmlFile.trim());
            if (resource == null) {
                showError("FXML file not found: " + fxmlFile);
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Cricket Help Desk");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to load scene: " + fxmlFile);
        }
    }

    public static void loadMainScene() {
        try {
            URL resource = SceneNavigator.class.getClassLoader().getResource("main.fxml");
            if (resource == null) {
                showError("FXML file not found: main.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Cricket Help Desk");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Unable to load main scene");
        }
    }

    /**
     * Shows a simple error alert dialog.
     *
     * @param message Error message to display.
     */
    private static void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Scene Navigation Error");
        alert.setHeaderText("Scene Switch Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
