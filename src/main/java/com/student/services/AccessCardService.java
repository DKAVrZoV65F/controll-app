package com.student.services;

import com.student.data.AccessCardDao;
import com.student.model.AccessCard;
import java.util.List;

public class AccessCardService {
    private final AccessCardDao cardDao = new AccessCardDao();

    public void createAccessCard(AccessCard card) {
        cardDao.createAccessCard(card);
    }

    public List<AccessCard> getAllAccessCards() {
        return cardDao.getAllAccessCards();
    }
}