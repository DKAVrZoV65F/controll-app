package com.student.services;

import com.student.data.UserDao;
import com.student.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDao userDao = new UserDao();
    private final LogService logService = new LogService();

    public boolean registerUser(String username, String password, String role, boolean activated) {
        if (userDao.findByUsername(username) != null) {
            logService.logEvent(0, 0, "Ошибка регистрации: пользователь " + username + " уже существует");
            return false;
        }
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPasswordHash(passwordHash);
        newUser.setRole(role);
        newUser.setActivated(activated);
        userDao.createUser(newUser);
        logService.logEvent(newUser.getId(), 0, "Зарегистрирован новый аккаунт: " + username);
        return true;
    }

    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && user.isActivated()) {
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                logService.logEvent(user.getId(), 0, "Успешная авторизация для " + username);
                return user;
            }
        }
        int logUser = (user != null) ? user.getId() : 0;
        logService.logEvent(logUser, 0, "Неудачная попытка авторизации для " + username);
        return null;
    }

    public void changeUserRole(int userId, String newRole) {
        User user = userDao.findById(userId);
        if (user != null) {
            user.setRole(newRole);
            userDao.updateUser(user);
        }
    }

    public void setUserActivation(int userId, boolean activated) {
        User user = userDao.findById(userId);
        if (user != null) {
            user.setActivated(activated);
            userDao.updateUser(user);
        }
    }

    public java.util.List<User> getAllUsers() {
        return userDao.findAll();
    }

    public void deleteUser(int userId) {
        userDao.deleteUser(userId);
    }
}