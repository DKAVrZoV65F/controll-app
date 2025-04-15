package com.student.services;

import com.student.data.PermissionDao;
import com.student.model.Permission;
import java.util.List;

public class PermissionService {
    private final PermissionDao permissionDao = new PermissionDao();

    public void createPermission(Permission permission) {
        permissionDao.createPermission(permission);
    }

    public List<Permission> getAllPermissions() {
        return permissionDao.getAllPermissions();
    }

    public void deletePermission(int permissionId) {
        permissionDao.deletePermission(permissionId);
    }

    public boolean permissionExists(int doorId, String role) {
        Permission p = permissionDao.findByRoleAndDoor(role, doorId);
        return (p != null);
    }
}