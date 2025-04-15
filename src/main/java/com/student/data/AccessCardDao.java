package com.student.data;

import com.student.db.DBConnection;
import com.student.model.AccessCard;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessCardDao {
    public void createAccessCard(AccessCard card) {
        String sql = "INSERT INTO AccessCards (cardNumber, user_id, validity) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, card.getCardNumber());
            ps.setInt(2, card.getUserId());
            ps.setTimestamp(3, Timestamp.valueOf(card.getValidity()));
            ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public AccessCard findByCardNumber(String cardNumber) {
        String sql = "SELECT * FROM AccessCards WHERE cardNumber = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, cardNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                AccessCard card = new AccessCard();
                card.setId(rs.getInt("id"));
                card.setCardNumber(rs.getString("cardNumber"));
                card.setUserId(rs.getInt("user_id"));
                card.setValidity(rs.getTimestamp("validity").toLocalDateTime());
                return card;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<AccessCard> getAllAccessCards() {
        List<AccessCard> list = new ArrayList<>();
        String sql = "SELECT * FROM AccessCards";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while(rs.next()){
                AccessCard card = new AccessCard();
                card.setId(rs.getInt("id"));
                card.setCardNumber(rs.getString("cardNumber"));
                card.setUserId(rs.getInt("user_id"));
                card.setValidity(rs.getTimestamp("validity").toLocalDateTime());
                list.add(card);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}