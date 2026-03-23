package com.pecenio.frontend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URI;
import java.net.http.*;
import java.time.LocalDate;
import java.util.Map;
import com.pecenio.frontend.model.Ticket;

public class EditTicketController {

    @FXML private TextField ticketIdField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField clientNameField;
    @FXML private TextField accountNumberField;
    @FXML private TextField concernTitleField;
    @FXML private TextArea detailsArea;
    @FXML private ComboBox<String> statusCombo;
    @FXML private DatePicker datePicker;
    @FXML private Label notificationLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String BASE_URL = "http://localhost:8081/api/tickets";

    private Ticket currentTicket;

    @FXML
    public void initialize() {
        fetchDropdownOptions("http://localhost:8081/api/types", typeCombo);
        ticketIdField.setEditable(false);
    }

    private void fetchDropdownOptions(String url, ComboBox<String> comboBox) {
        if (comboBox == statusCombo) return; // Prevent statusCombo population here
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(json -> {
                        try {
                            String[] options = objectMapper.readValue(json, String[].class);
                            Platform.runLater(() -> comboBox.getItems().addAll(options));
                        } catch (Exception e) {
                            showSimpleAlert(Alert.AlertType.ERROR, "Failed to load options from " + url);
                        }
                    });
        } catch (Exception e) {
            showSimpleAlert(Alert.AlertType.ERROR, "Error loading dropdown: " + e.getMessage());
        }
    }

    public void setTicketData(Ticket ticket) {
        this.currentTicket = ticket;
        Platform.runLater(() -> {
            ticketIdField.setText(String.valueOf(ticket.getId()));
            typeCombo.setValue(ticket.getType());
            String details = ticket.getDetails();
            String[] lines = details != null ? details.split("\n", 4) : new String[0];
            clientNameField.setText(lines.length > 0 ? lines[0].replaceFirst("Client Name: ", "") : "");
            accountNumberField.setText(lines.length > 1 ? lines[1].replaceFirst("Account Number: ", "") : "");
            concernTitleField.setText(lines.length > 2 ? lines[2].replaceFirst("Concern Title: ", "") : "");
            detailsArea.setText(lines.length > 3 ? lines[3].replaceFirst("Concern Details: ", "") : "");
            datePicker.setValue(LocalDate.parse(ticket.getDateReported()));
            statusCombo.getItems().clear();
            String currentStatus = ticket.getStatus();
            if (currentStatus.equalsIgnoreCase("Open")) {
                statusCombo.getItems().addAll("Open", "In Progress", "Resolved");
            } else if (currentStatus.equalsIgnoreCase("In Progress")) {
                statusCombo.getItems().addAll("In Progress", "Resolved");
            } else if (currentStatus.equalsIgnoreCase("Resolved")) {
                statusCombo.getItems().add("Resolved");
            }
            statusCombo.setValue(currentStatus);
        });
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
    private void handleUpdate() {
        try {
            String type = typeCombo.getValue();
            String status = statusCombo.getValue();
            String clientName = clientNameField.getText();
            String accountNumber = accountNumberField.getText();
            String concernTitle = concernTitleField.getText();
            String concernDetails = detailsArea.getText();
            String dateReported = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
            boolean hasError = false;
            if (type == null || type.trim().isEmpty()) {
                highlightField(typeCombo, true);
                hasError = true;
            } else {
                highlightField(typeCombo, false);
            }
            if (status == null || status.trim().isEmpty()) {
                highlightField(statusCombo, true);
                hasError = true;
            } else {
                highlightField(statusCombo, false);
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
            // Confirmation for Resolved
            if (status.equalsIgnoreCase("Resolved")) {
                javafx.scene.control.Alert confirmation = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Resolve Ticket Confirmation");
                confirmation.setHeaderText(null);
                confirmation.setContentText("Are you sure you want to resolve this ticket? This action cannot be undone.");
                java.util.Optional<javafx.scene.control.ButtonType> result = confirmation.showAndWait();
                if (!(result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK)) {
                    return;
                }
            }
            String details = "Client Name: " + clientName + "\n" +
                             "Account Number: " + accountNumber + "\n" +
                             "Concern Title: " + concernTitle + "\n" +
                             "Concern Details: " + concernDetails;
            Map<String, Object> payload = Map.of(
                "id", Long.parseLong(ticketIdField.getText()),
                "type", type,
                "status", status,
                "details", details,
                "dateReported", dateReported
            );
            String endpoint = BASE_URL + "/" + currentTicket.getId();
            if (status.equalsIgnoreCase("Resolved")) {
                endpoint += "/resolve";
            }
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> javafx.application.Platform.runLater(() -> {
                        if (status.equalsIgnoreCase("Resolved")) {
                            showNotification("Ticket has been resolved and moved to history.", true);
                        } else {
                            showNotification("Ticket has been updated.", true);
                        }
                        closeWindowAndReturnToMain();
                        if (com.pecenio.frontend.controller.MainController.instance != null) {
                            com.pecenio.frontend.controller.MainController.instance.reloadTickets();
                        }
                    }));
        } catch (Exception e) {
            showNotification("Error: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleCloseTicket() {
        javafx.scene.control.Alert confirmation = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Close Ticket Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to close this ticket? This action cannot be undone.");
        java.util.Optional<javafx.scene.control.ButtonType> result = confirmation.showAndWait();
        if (!(result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK)) {
            return;
        }
        try {
            String details = "Client Name: " + clientNameField.getText() + "\n" +
                             "Account Number: " + accountNumberField.getText() + "\n" +
                             "Concern Title: " + concernTitleField.getText() + "\n" +
                             "Concern Details: " + detailsArea.getText();
            String dateReported = datePicker.getValue() != null ? datePicker.getValue().toString() : "";
            Map<String, Object> payload = Map.of(
                "id", Long.parseLong(ticketIdField.getText()),
                "type", typeCombo.getValue(),
                "status", "Closed",
                "details", details,
                "dateReported", dateReported
            );
            String json = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + currentTicket.getId() + "/close"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> javafx.application.Platform.runLater(() -> {
                        showNotification("Ticket has been closed and moved to history.", true);
                        closeWindowAndReturnToMain();
                        if (com.pecenio.frontend.controller.MainController.instance != null) {
                            com.pecenio.frontend.controller.MainController.instance.reloadTickets();
                        }
                    }));
        } catch (Exception e) {
            showNotification("Error: " + e.getMessage(), false);
        }
    }

    private void closeWindowAndReturnToMain() {
        Stage stage = (Stage) ticketIdField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onBackToMain(ActionEvent event) {
        closeWindowAndReturnToMain();
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
}
