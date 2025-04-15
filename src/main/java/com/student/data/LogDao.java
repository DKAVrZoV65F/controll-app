package com.student.data;

import com.student.db.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogDao {
    public void logEvent(int userId, int doorId, String result) {
        String sql = "INSERT INTO AccessLogs (user_id, door_id, log_time, result) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, doorId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, result);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getLogs() {
        List<String> logs = new ArrayList<>();
        String sql = "SELECT * FROM AccessLogs ORDER BY log_time DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("log_time");
                int userId = rs.getInt("user_id");
                String doorStr = (rs.getObject("door_id") == null) ? "-" : String.valueOf(rs.getInt("door_id"));
                String res = rs.getString("result");
                logs.add(ts.toString() + " (UserID: " + userId + ", DoorID: " + doorStr + ") - " + res);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }
}