package com.pecenio.frontend.controller;

import com.pecenio.frontend.model.Ticket;
import com.pecenio.frontend.model.TicketHistory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ViewTicketController {

    @FXML private TextField idField;
    @FXML private TextField typeField;
    @FXML private TextField clientNameField;
    @FXML private TextField accountNumberField;
    @FXML private TextField concernTitleField;
    @FXML private TextArea detailsArea;
    @FXML private TextField statusField;
    @FXML private TextField dateField;

    private Ticket ticket;

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
        try {
            populateFields();
        } catch (Exception e) {
            showAlert("Error", "Failed to load ticket details: " + e.getMessage());
        }
    }

    public void setTicketHistory(TicketHistory ticket) {
        try {
            idField.setText(String.valueOf(ticket.getOriginalTicketId()));
            typeField.setText(ticket.getType());
            String details = ticket.getDetails();
            String[] lines = details != null ? details.split("\n", 4) : new String[0];
            clientNameField.setText(lines.length > 0 ? lines[0].replaceFirst("Client Name: ", "") : "");
            accountNumberField.setText(lines.length > 1 ? lines[1].replaceFirst("Account Number: ", "") : "");
            concernTitleField.setText(lines.length > 2 ? lines[2].replaceFirst("Concern Title: ", "") : "");
            detailsArea.setText(lines.length > 3 ? lines[3].replaceFirst("Concern Details: ", "") : "");
            statusField.setText(ticket.getStatus());
            dateField.setText(ticket.getDateReported());
        } catch (Exception e) {
            showAlert("Error", "Failed to load ticket details: " + e.getMessage());
        }
    }

    private void populateFields() {
        if (ticket != null) {
            idField.setText(String.valueOf(ticket.getId()));
            typeField.setText(ticket.getType());
            String details = ticket.getDetails();
            String[] lines = details != null ? details.split("\n", 4) : new String[0];
            clientNameField.setText(lines.length > 0 ? lines[0].replaceFirst("Client Name: ", "") : "");
            accountNumberField.setText(lines.length > 1 ? lines[1].replaceFirst("Account Number: ", "") : "");
            concernTitleField.setText(lines.length > 2 ? lines[2].replaceFirst("Concern Title: ", "") : "");
            detailsArea.setText(lines.length > 3 ? lines[3].replaceFirst("Concern Details: ", "") : "");
            statusField.setText(ticket.getStatus());
            dateField.setText(ticket.getDateReported());
        }
    }

    @FXML
    private void onBackToMain(ActionEvent event) {
        ((Stage) idField.getScene().getWindow()).close();
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

    @FXML
    public void initialize() {
        // Remove: formCard.layoutBoundsProperty().addListener(...)
    }
}
