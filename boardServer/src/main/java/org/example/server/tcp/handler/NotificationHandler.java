package org.example.server.tcp.handler;

import org.example.database.Database;
import org.example.model.dto.NotificationDto;
import org.example.server.tcp.TcpServer;
import org.example.utils.JSON;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class NotificationHandler {

    public static void sendNotifications(String board_id, String message) throws Exception {
        System.out.println(board_id);
        String query = "SELECT user_id FROM board_users WHERE board_id = ?";
        Connection conn = Database.getConnection();
        Set<String> boardUsers = new HashSet<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, board_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                boardUsers.add(rs.getString("user_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(boardUsers.size());

        for (String uid : boardUsers) {
            TcpServer.Session session = TcpServer.sessions.get(uid);
            if (session != null) {
                try {
                    if (!session.socket.isClosed()) {
                        System.out.println(JSON.Stringify(new NotificationDto(message)));
                        session.writer.write(JSON.Stringify(new NotificationDto(message)));
                        session.writer.newLine();
                        session.writer.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


