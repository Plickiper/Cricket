package com.pecenio.frontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashScreenController {

    @FXML
    private Button proceedButton;

    @FXML
    private void onProceedClick() {
        // Backend health check
        boolean backendUp = false;
        try {
            URL url = new URL("http://localhost:8081/api/tickets");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000); // 2 seconds
            conn.connect();
            int code = conn.getResponseCode();
            if (code >= 200 && code < 500) { // 2xx or 4xx means backend is up
                backendUp = true;
            }
        } catch (Exception e) {
            backendUp = false;
        }
        if (!backendUp) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Backend Not Available");
            alert.setHeaderText(null);
            alert.setContentText("Backend is not up. Please start the backend server.");
            alert.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
            Parent mainRoot = loader.load();

            Stage stage = (Stage) proceedButton.getScene().getWindow();
            stage.setScene(new Scene(mainRoot));
            stage.setTitle("Cricket Help Desk");
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 