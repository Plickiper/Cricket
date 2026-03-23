package com.pecenio.frontend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pecenio.frontend.model.TicketHistory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.event.ActionEvent;
import javafx.scene.control.TableRow;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HistoryController {
    @FXML private TableView<TicketHistory> historyTable;
    @FXML private TableColumn<TicketHistory, Long> idColumn;
    @FXML private TableColumn<TicketHistory, String> typeColumn;
    @FXML private TableColumn<TicketHistory, String> detailsColumn;
    @FXML private TableColumn<TicketHistory, String> statusColumn;
    @FXML private TableColumn<TicketHistory, String> dateReportedColumn;
    @FXML private Button deleteButton;
    @FXML private Button restoreButton;
    @FXML private Button backButton;
    @FXML private Label notificationLabel;
    @FXML private Button viewButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String BASE_URL = "http://localhost:8081/api/history";

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("originalTicketId"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        detailsColumn.setCellValueFactory(cellData -> {
            String details = cellData.getValue().getDetails();
            if (details != null) {
                String concern = "";
                String[] lines = details.split("\n");
                for (String line : lines) {
                    if (line.startsWith("Concern Title:")) concern = line.replaceFirst("Concern Title: ", "");
                }
                return new javafx.beans.property.SimpleStringProperty(concern);
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateReportedColumn.setCellValueFactory(new PropertyValueFactory<>("dateReported"));
        loadHistory();
        // Disable action buttons if no row is selected
        deleteButton.setDisable(true);
        restoreButton.setDisable(true);
        viewButton.setDisable(true);
        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean disable = (newSel == null);
            deleteButton.setDisable(disable);
            restoreButton.setDisable(disable);
            viewButton.setDisable(disable);
        });
        historyTable.setRowFactory(tv -> {
            TableRow<TicketHistory> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty()) {
                    historyTable.getSelectionModel().clearSelection();
                }
            });
            return row;
        });
    }

    private void loadHistory() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            List<TicketHistory> historyList = objectMapper.readValue(response.body(), new TypeReference<List<TicketHistory>>() {});
            ObservableList<TicketHistory> observableList = FXCollections.observableArrayList(historyList);
            historyTable.setItems(observableList);
        } catch (Exception e) {
            e.printStackTrace();
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

    @FXML
    private void handleDelete() {
        TicketHistory selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showNotification("Please select a ticket from the list first.", false);
            return;
        }
        javafx.scene.control.Alert confirmation = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Permanently deleting this data is irreversible and cannot be undone. Are you sure?");
        java.util.Optional<javafx.scene.control.ButtonType> result = confirmation.showAndWait();
        if (!(result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK)) {
            return;
        }
        try {
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/" + selected.getId()))
                    .DELETE()
                    .build();
            httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            loadHistory();
            showNotification("Ticket successfully deleted from the system.", true);
        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Failed to delete ticket from history.", false);
        }
    }

    @FXML
    private void handleRestore() {
        TicketHistory selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showNotification("Please select a ticket from the list first.", false);
            return;
        }
        javafx.scene.control.Alert confirmation = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Restore Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to restore this ticket? It will be reopened as an active ticket.");
        java.util.Optional<javafx.scene.control.ButtonType> result = confirmation.showAndWait();
        if (!(result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK)) {
            return;
        }
        try {
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/restore/" + selected.getId()))
                    .POST(java.net.http.HttpRequest.BodyPublishers.noBody())
                    .build();
            httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            loadHistory();
            javafx.application.Platform.runLater(() -> {
                if (com.pecenio.frontend.controller.MainController.instance != null) {
                    com.pecenio.frontend.controller.MainController.instance.reloadTickets();
                }
                showNotification("Restored data will be reopened.", true);
                Stage thisStage = (Stage) historyTable.getScene().getWindow();
                thisStage.close();
            });
        } catch (Exception e) {
            e.printStackTrace();
            showNotification("Failed to restore ticket.", false);
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void handleViewTicket() {
        TicketHistory selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openViewTicketWindow(selected);
        } else {
            showNotification("Please select a ticket to view.", false);
        }
    }

    private void openViewTicketWindow(TicketHistory ticket) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view_ticket.fxml"));
            Parent root = loader.load();
            ViewTicketController controller = loader.getController();
            controller.setTicketHistory(ticket);
            Stage stage = new Stage();
            stage.setTitle("View Ticket");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 