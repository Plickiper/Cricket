package com.pecenio.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javafx.application.Platform;

public class CreateTicketController {

    @FXML private ComboBox<String> typeCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField clientNameField;
    @FXML private TextField accountNumberField;
    @FXML private TextField concernTitleField;
    @FXML private TextArea detailsArea;
    @FXML private Label notificationLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @FXML
    public void initialize() {
        fetchDropdownOptions("http://localhost:8081/api/types", typeCombo);
        clearFields();
    }

    private void fetchDropdownOptions(String url, ComboBox<String> comboBox) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String[] options = objectMapper.readValue(response.body(), String[].class);
                comboBox.getItems().addAll(options);
            } else {
                showSimpleAlert(Alert.AlertType.ERROR, "Failed to load options from " + url);
            }
        } catch (Exception e) {
            showSimpleAlert(Alert.AlertType.ERROR, "Error loading dropdown: " + e.getMessage());
        }
    }

    private void showNotification(String message, boolean success) {
        notificationLabel.setText(message);
        notificationLabel.setStyle(success
            ? "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8; -fx-background-radius: 8;"
            : "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8; -fx-background-radius: 8;");
        notificationLabel.setVisible(true);
        new Thread(() -> {
            try { Thread.sleep(2500); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> notificationLabel.setVisible(false));
        }).start();
    }

    private void highlightField(Control field, boolean error) {
        if (error) {
            field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-background-radius: 8px;");
        } else {
            field.setStyle("");
        }
    }

    @FXML
    private void handleSubmit() {
        try {
            String type = typeCombo.getValue();
            String clientName = clientNameField.getText();
            String accountNumber = accountNumberField.getText();
            String concernTitle = concernTitleField.getText();
            String concernDetails = detailsArea.getText();
            String dateReported = datePicker.getValue() != null
                    ? datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    : "";

            boolean hasError = false;
            if (type == null || type.trim().isEmpty()) {
                highlightField(typeCombo, true);
                hasError = true;
            } else {
                highlightField(typeCombo, false);
            }
            if (clientName == null || clientName.trim().isEmpty()) {
                highlightField(clientNameField, true);
                hasError = true;
            } else {
                highlightField(clientNameField, false);
            }
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                highlightField(accountNumberField, true);
                hasError = true;
            } else {
                highlightField(accountNumberField, false);
            }
            if (concernTitle == null || concernTitle.trim().isEmpty()) {
                highlightField(concernTitleField, true);
                hasError = true;
            } else {
                highlightField(concernTitleField, false);
            }
            if (concernDetails == null || concernDetails.trim().isEmpty()) {
                highlightField(detailsArea, true);
                hasError = true;
            } else {
                highlightField(detailsArea, false);
            }
            if (dateReported.isEmpty()) {
                highlightField(datePicker, true);
                hasError = true;
            } else {
                highlightField(datePicker, false);
            }
            if (hasError) {
                showNotification("Please fill in all fields correctly.", false);
                return;
            }
            // Combine all fields into a single details string
            String details = "Client Name: " + clientName + "\n" +
                             "Account Number: " + accountNumber + "\n" +
                             "Concern Title: " + concernTitle + "\n" +
                             "Concern Details: " + concernDetails;
            Map<String, Object> payload = Map.of(
                    "type", type,
                    "status", "Open",
                    "details", details,
                    "dateReported", dateReported
            );
            String json = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8081/api/tickets"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201 || response.statusCode() == 200) {
                javafx.application.Platform.runLater(() -> {
                    showNotification("Ticket created successfully.", true);
                    closeWindow();
                    if (com.pecenio.frontend.controller.MainController.instance != null) {
                        com.pecenio.frontend.controller.MainController.instance.reloadTickets();
                    }
                });
            } else {
                showNotification("Failed to create ticket. Status: " + response.statusCode(), false);
            }
        } catch (Exception e) {
            showNotification("Error: " + e.getMessage(), false);
        }
    }

    private void closeWindow() {
        // Get the current stage and close it
        Stage stage = (Stage) typeCombo.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onBackToMain(ActionEvent event) {
        closeWindow();
    }

    private void showSimpleAlert(Alert.AlertType type, String message) {
        String title;
        switch (type) {
            case INFORMATION: title = "Success"; break;
            case WARNING:     title = "Warning"; break;
            case ERROR:       title = "Error"; break;
            default:          title = "Notice";
        }
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        typeCombo.getSelectionModel().clearSelection();
        clientNameField.clear();
        accountNumberField.clear();
        concernTitleField.clear();
        detailsArea.clear();
        datePicker.setValue(null);
    }
}