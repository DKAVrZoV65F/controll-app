package com.student.data;

import com.student.db.DBConnection;
import com.student.model.Permission;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionDao {
    public void createPermission(Permission permission) {
        String sql = "INSERT INTO Permissions (door_id, role, canOpen, canClose) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, permission.getDoorId());
            ps.setString(2, permission.getRole());
            ps.setBoolean(3, permission.isCanOpen());
            ps.setBoolean(4, permission.isCanClose());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Permission> getPermissionsByDoorId(int doorId) {
        List<Permission> list = new ArrayList<>();
        String sql = "SELECT * FROM Permissions WHERE door_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doorId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Permission perm = new Permission();
                perm.setId(rs.getInt("id"));
                perm.setDoorId(rs.getInt("door_id"));
                perm.setRole(rs.getString("role"));
                perm.setCanOpen(rs.getBoolean("canOpen"));
                perm.setCanClose(rs.getBoolean("canClose"));
                list.add(perm);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    public List<Permission> getAllPermissions() {
        List<Permission> list = new ArrayList<>();
        String sql = "SELECT * FROM Permissions";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while(rs.next()){
                Permission perm = new Permission();
                perm.setId(rs.getInt("id"));
                perm.setDoorId(rs.getInt("door_id"));
                perm.setRole(rs.getString("role"));
                perm.setCanOpen(rs.getBoolean("canOpen"));
                perm.setCanClose(rs.getBoolean("canClose"));
                list.add(perm);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return list;
    }

    public void updatePermission(Permission permission) {
        String sql = "UPDATE Permissions SET door_id = ?, role = ?, canOpen = ?, canClose = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, permission.getDoorId());
            ps.setString(2, permission.getRole());
            ps.setBoolean(3, permission.isCanOpen());
            ps.setBoolean(4, permission.isCanClose());
            ps.setInt(5, permission.getId());
            ps.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    // В PermissionDao:
    public Permission findByRoleAndDoor(String role, int doorId) {
        String sql = "SELECT * FROM Permissions WHERE role = ? AND door_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, doorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Permission p = new Permission();
                p.setId(rs.getInt("id"));
                p.setDoorId(rs.getInt("door_id"));
                p.setRole(rs.getString("role"));
                p.setCanOpen(rs.getBoolean("canOpen"));
                p.setCanClose(rs.getBoolean("canClose"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deletePermission(int id) {
        String sql = "DELETE FROM Permissions WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}