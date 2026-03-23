package com.pecenio.frontend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pecenio.frontend.model.Ticket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MainController {

    public static MainController instance;

    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Ticket, Long> idColumn;
    @FXML private TableColumn<Ticket, String> typeColumn;
    @FXML private TableColumn<Ticket, String> detailsColumn;
    @FXML private TableColumn<Ticket, String> statusColumn;
    @FXML private TableColumn<Ticket, String> dateReportedColumn;
    @FXML private Label notificationLabel;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button viewButton;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String BASE_URL = "http://localhost:8081/api/tickets";

    private Stage createTicketStage;
    private Stage editTicketStage;
    private Stage viewTicketStage;
    private Stage historyStage;

    @FXML
    public void initialize() {
        instance = this;
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
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

        loadTickets();
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        viewButton.setDisable(true);
        ticketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean disable = (newSel == null);
            editButton.setDisable(disable);
            deleteButton.setDisable(disable);
            viewButton.setDisable(disable);
        });

        ticketTable.setRowFactory(tv -> {
            TableRow<Ticket> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty()) {
                    ticketTable.getSelectionModel().clearSelection();
                }
            });
            return row;
        });
    }

    private void loadTickets() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        List<Ticket> tickets = new ObjectMapper().readValue(json, new TypeReference<>() {});
                        ObservableList<Ticket> data = FXCollections.observableArrayList(tickets);
                        ticketTable.getItems().setAll(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showSimpleAlert(Alert.AlertType.ERROR, "Failed to load tickets.");
                    }
                });
    }

    @FXML
    private void onCreateTicket(ActionEvent event) {
        if (createTicketStage != null && createTicketStage.isShowing()) {
            createTicketStage.toFront();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("create_ticket.fxml"));
            Parent root = loader.load();
            createTicketStage = new Stage();
            createTicketStage.setTitle("Create New Ticket");
            createTicketStage.setScene(new Scene(root));
            createTicketStage.show();
            createTicketStage.setOnHiding(e -> {
                loadTickets();
                createTicketStage = null;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditTicket(ActionEvent event) {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            showNotification("Please select a ticket to edit.", false);
            return;
        }
        if (editTicketStage != null && editTicketStage.isShowing()) {
            editTicketStage.toFront();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getClassLoader().getResource("edit_ticket.fxml"));
            Parent root = loader.load();
            EditTicketController controller = loader.getController();
            controller.setTicketData(selectedTicket);
            editTicketStage = new Stage();
            editTicketStage.setTitle("Edit Ticket");
            editTicketStage.setScene(new Scene(root));
            editTicketStage.show();
            editTicketStage.setOnHiding(e -> {
                loadTickets();
                editTicketStage = null;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteTicket(ActionEvent event) {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            showNotification("Please select a ticket to delete.", false);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete Ticket ID: " + selectedTicket.getId() + "?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteTicket(selectedTicket.getId());
            }
        });
    }

    private void deleteTicket(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        javafx.application.Platform.runLater(() -> {
                            showNotification("Ticket deleted successfully.", true);
                            loadTickets();
                        });
                    } else if (response.statusCode() == 404) {
                        javafx.application.Platform.runLater(() -> showSimpleAlert(Alert.AlertType.ERROR, "Ticket not found."));
                    } else {
                        javafx.application.Platform.runLater(() -> showSimpleAlert(Alert.AlertType.ERROR, "Failed to delete ticket."));
                    }
                });
    }

    @FXML
    private void onViewTicket(ActionEvent event) {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            showNotification("Please select a ticket to view.", false);
            return;
        }
        if (viewTicketStage != null && viewTicketStage.isShowing()) {
            viewTicketStage.toFront();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view_ticket.fxml"));
            Parent root = loader.load();
            ViewTicketController controller = loader.getController();
            controller.setTicket(selectedTicket);
            viewTicketStage = new Stage();
            viewTicketStage.setTitle("View Ticket");
            viewTicketStage.setScene(new Scene(root));
            viewTicketStage.setResizable(false);
            viewTicketStage.show();
            viewTicketStage.setOnHiding(e -> viewTicketStage = null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onHistory(ActionEvent event) {
        if (historyStage != null && historyStage.isShowing()) {
            historyStage.toFront();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("history.fxml"));
            Parent root = loader.load();
            historyStage = new Stage();
            historyStage.setTitle("Ticket History");
            historyStage.setScene(new Scene(root));
            historyStage.show();
            historyStage.setOnHiding(e -> historyStage = null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadTickets() {
        loadTickets();
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
}
