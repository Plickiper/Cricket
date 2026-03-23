package com.pecenio.frontend.model;

import javafx.beans.property.*;

public class Ticket {

    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty details = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty dateReported = new SimpleStringProperty();

    // Property accessors
    public LongProperty idProperty() { return id; }
    public StringProperty typeProperty() { return type; }
    public StringProperty detailsProperty() { return details; }
    public StringProperty statusProperty() { return status; }
    public StringProperty dateReportedProperty() { return dateReported; }

    // Value accessors (these are required for JavaFX)
    public Long getId() { return id.get(); }
    public void setId(Long id) { this.id.set(id); }
    public String getType() { return type.get(); }
    public void setType(String type) { this.type.set(type); }
    public String getDetails() { return details.get(); }
    public void setDetails(String details) { this.details.set(details); }
    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }
    public String getDateReported() { return dateReported.get(); }
    public void setDateReported(String date) { this.dateReported.set(date); }
}
