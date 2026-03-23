package com.pecenio.frontend.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DeleteTicketController {

    @FXML
    private TextField ticketIdField;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String BASE_URL = "http://localhost:8081/api/tickets";

    @FXML
    private void handleDelete() {
        String id = ticketIdField.getText();

        if (id == null || id.isEmpty()) {
            showAlert("Validation Error", "Please enter a valid Ticket ID.");
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + id))
                    .DELETE()
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            showAlert("Success", "Ticket deleted successfully.");
                        } else if (response.statusCode() == 404) {
                            showAlert("Error", "Ticket not found.");
                        } else {
                            showAlert("Error", "Failed to delete ticket.");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while deleting the ticket.");
        }
    }

    @FXML
    private void onBackToMain(ActionEvent event) {
        SceneNavigator.switchTo("main.fxml", event);
    }

    private void showAlert(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
