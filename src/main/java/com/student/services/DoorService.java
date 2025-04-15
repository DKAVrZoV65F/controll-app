package com.student.services;

import com.student.data.DoorDao;
import com.student.data.LogDao;
import com.student.data.PermissionDao;
import com.student.model.Door;
import com.student.model.Permission;
import com.student.model.User;

import java.util.List;

public class DoorService {
    private final DoorDao doorDao = new DoorDao();
    private final PermissionDao permissionDao = new PermissionDao();
    private final LogDao logDao = new LogDao();

    public boolean openDoor(User user, Door door) {
        com.student.model.Permission perm = permissionDao.findByRoleAndDoor(user.getRole(), door.getId());
        if (perm == null) {
            logDao.logEvent(user.getId(), door.getId(), "Открыть нельзя: нет записи в Permissions");
            return false;
        }

        if (!perm.isCanOpen()) {
            logDao.logEvent(user.getId(), door.getId(), "Открыть нельзя: canOpen=false");
            return false;
        }

        door.setStatus("OPEN");
        doorDao.updateDoor(door);
        logDao.logEvent(user.getId(), door.getId(), "Дверь открыта");
        return true;
    }

    public boolean closeDoor(User user, Door door) {
        Permission perm = permissionDao.findByRoleAndDoor(user.getRole(), door.getId());
        if (perm == null) {
            logDao.logEvent(user.getId(), door.getId(), "Закрыть нельзя: нет записи в Permissions");
            return false;
        }

        if (!perm.isCanClose()) {
            logDao.logEvent(user.getId(), door.getId(), "Закрыть нельзя: canClose=false");
            return false;
        }

        door.setStatus("CLOSED");
        doorDao.updateDoor(door);
        logDao.logEvent(user.getId(), door.getId(), "Дверь закрыта");
        return true;
    }

    public List<Door> getAllDoors() {
        return doorDao.getAllDoors();
    }

    public void updateDoor(Door door) {
        doorDao.updateDoor(door);
    }

    public void createDoor(Door door) {
        doorDao.createDoor(door);
    }

    public void deleteDoor(int id) {
        doorDao.deleteDoor(id);
    }

    public boolean doorExists(String doorName) {
        Door existing = doorDao.getDoorByName(doorName);
        return (existing != null);
    }
}
