package com.student.services;

import com.student.data.LogDao;

import java.util.List;

public class LogService {
    private final LogDao logDao = new LogDao();

    public void logEvent(int userId, int doorId, String result) {
        logDao.logEvent(userId, doorId, result);
    }

    public List<String> getAllLogs() {
        return logDao.getLogs();
    }
}