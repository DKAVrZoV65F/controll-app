package com.student.data;

import com.student.db.DBConnection;
import com.student.model.Door;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoorDao {
    public void createDoor(Door door) {
        String sql = "INSERT INTO Doors (name, location, status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, door.getName());
            pstmt.setString(2, door.getLocation());
            pstmt.setString(3, door.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Door getDoorByName(String name) {
        String sql = "SELECT * FROM Doors WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Door door = new Door();
                door.setId(rs.getInt("id"));
                door.setName(rs.getString("name"));
                door.setLocation(rs.getString("location"));
                door.setStatus(rs.getString("status"));
                return door;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Door> getAllDoors() {
        List<Door> doors = new ArrayList<>();
        String sql = "SELECT * FROM Doors";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Door door = new Door();
                door.setId(rs.getInt("id"));
                door.setName(rs.getString("name"));
                door.setLocation(rs.getString("location"));
                door.setStatus(rs.getString("status"));
                doors.add(door);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doors;
    }

    public void updateDoor(Door door) {
        String sql = "UPDATE Doors SET name = ?, location = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, door.getName());
            pstmt.setString(2, door.getLocation());
            pstmt.setString(3, door.getStatus());
            pstmt.setInt(4, door.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteDoor(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement psUpdate = conn.prepareStatement(
                    "UPDATE AccessLogs SET door_id = NULL WHERE door_id = ?")) {
                psUpdate.setInt(1, id);
                psUpdate.executeUpdate();
            }

            try (PreparedStatement psDelete = conn.prepareStatement(
                    "DELETE FROM Doors WHERE id = ?")) {
                psDelete.setInt(1, id);
                psDelete.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
