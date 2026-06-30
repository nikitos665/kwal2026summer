package dao;

import database.Database;
import model.RepairRequest;
import model.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepairRequestDAO {

    public void create(RepairRequest request) throws SQLException {
        String sql = """
        INSERT INTO repair_requests 
        (client_id, client_name, client_phone, device_type, device_model, serial_number, 
         problem_description, status, technician_notes, repair_cost)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // client_id — если 0, вставляем NULL (чтобы не было ошибки)
            if (request.getClientId() > 0) {
                pstmt.setInt(1, request.getClientId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }

            pstmt.setString(2, request.getClientName());
            pstmt.setString(3, request.getClientPhone());
            pstmt.setString(4, request.getDeviceType());
            pstmt.setString(5, request.getDeviceModel());
            pstmt.setString(6, request.getSerialNumber());
            pstmt.setString(7, request.getProblemDescription());

            // Гарантированно статус, если вдруг null
            pstmt.setString(8, request.getStatus() != null ? request.getStatus().name() : "NEW");

            pstmt.setString(9, request.getTechnicianNotes());
            pstmt.setDouble(10, request.getRepairCost());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    request.setId(rs.getInt(1));
                }
            }
        }
    }

    public List <RepairRequest> getAll() throws SQLException {
        List <RepairRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM repair_requests ORDER BY created_at DESC";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                requests.add(mapResultSet(rs));
            }
        }
        return requests;
    }
    public List <RepairRequest> getByClientId(int clientId) throws SQLException {
        List <RepairRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM repair_requests WHERE client_id = ? ORDER BY created_at DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSet(rs));
                }
            }
        }
        return requests;
    }

    public RepairRequest getById(int id) throws SQLException {
        String sql = "SELECT * FROM repair_requests WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        }
        return null;
    }

    public void update(RepairRequest request) throws SQLException {
        String sql = """
            UPDATE repair_requests 
            SET client_name = ?, client_phone = ?, device_type = ?, device_model = ?,
                serial_number = ?, problem_description = ?, status = ?,
                updated_at = CURRENT_TIMESTAMP, technician_notes = ?, repair_cost = ?
            WHERE id = ?
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, request.getClientName());
            pstmt.setString(2, request.getClientPhone());
            pstmt.setString(3, request.getDeviceType());
            pstmt.setString(4, request.getDeviceModel());
            pstmt.setString(5, request.getSerialNumber());
            pstmt.setString(6, request.getProblemDescription());
            pstmt.setString(7, request.getStatus().name());
            pstmt.setString(8, request.getTechnicianNotes());
            pstmt.setDouble(9, request.getRepairCost());
            pstmt.setInt(10, request.getId());

            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM repair_requests WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List <RepairRequest> search(String query) throws SQLException {
        List <RepairRequest> requests = new ArrayList<>();
        String sql = """
            SELECT * FROM repair_requests 
            WHERE client_name LIKE ? OR device_model LIKE ? OR serial_number LIKE ?
            ORDER BY created_at DESC
            """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + query + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSet(rs));
                }
            }
        }
        return requests;
    }

    private RepairRequest mapResultSet(ResultSet rs) throws SQLException {
        RepairRequest req = new RepairRequest();
        req.setId(rs.getInt("id"));
        req.setClientName(rs.getString("client_name"));
        req.setClientPhone(rs.getString("client_phone"));
        req.setDeviceType(rs.getString("device_type"));
        req.setDeviceModel(rs.getString("device_model"));
        req.setSerialNumber(rs.getString("serial_number"));
        req.setProblemDescription(rs.getString("problem_description"));
        req.setStatus(Status.valueOf(rs.getString("status")));
        req.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        req.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        req.setTechnicianNotes(rs.getString("technician_notes"));
        req.setRepairCost(rs.getDouble("repair_cost"));
        return req;
    }
}