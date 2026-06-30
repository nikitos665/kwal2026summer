package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RepairRequest {
    private int id;
    private int clientId;
    private String clientName;
    private String clientPhone;
    private String deviceType;
    private String deviceModel;
    private String serialNumber;
    private String problemDescription;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String technicianNotes;
    private double repairCost;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public RepairRequest() {}

    public RepairRequest(String clientName, String clientPhone, String deviceType,
                         String deviceModel, String serialNumber, String problemDescription) {
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.deviceType = deviceType;
        this.deviceModel = deviceModel;
        this.serialNumber = serialNumber;
        this.problemDescription = problemDescription;
        this.status = Status.NEW;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.repairCost = 0.0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getProblemDescription() { return problemDescription; }
    public void setProblemDescription(String problemDescription) { this.problemDescription = problemDescription; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getTechnicianNotes() { return technicianNotes; }
    public void setTechnicianNotes(String technicianNotes) { this.technicianNotes = technicianNotes; }

    public double getRepairCost() { return repairCost; }
    public void setRepairCost(double repairCost) { this.repairCost = repairCost; }

    public String getFormattedCreatedAt() {
        return createdAt != null ? createdAt.format(FORMATTER) : "";
    }

    public String getFormattedUpdatedAt() {
        return updatedAt != null ? updatedAt.format(FORMATTER) : "";
    }
    public int getClientId() { return clientId; }              // ← НОВЫЙ
    public void setClientId(int clientId) { this.clientId = clientId; }  // ← НОВЫЙ


    public String getShortDescription() {
        return problemDescription != null && problemDescription.length() > 50
                ? problemDescription.substring(0, 50) + "..."
                : problemDescription;
    }
}