package org.example.server.tcp.handler;

import org.example.database.Database;
import org.example.model.dto.*;
import org.example.model.message.Request;
import org.example.model.message.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

public class BoardHandler {

    public synchronized static Response<NewBoardResponseDto> handleBoardCreation(Request<NewBoardRequestDto> request) throws Exception {

        String name = request.getPayload().getName();

        Connection conn = Database.getConnection();
        String query = "SELECT * FROM boards WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return new Response<>("error", null, "Board " + name + " Already Exists");
            }
        } catch (Exception e) {
            return new Response<>("error", null, e.getMessage());
        }

        String username = request.getPayload().getUsername();
        String ownerId = request.getPayload().getOwnerId();
        String id = UUID.randomUUID().toString();


        query = "INSERT INTO boards(id,name, owner_id) VALUES(?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setString(3, ownerId);
            stmt.executeUpdate();
        } catch (Exception e) {
            return new Response<>("error", null, e.getMessage());
        }

        String bridgeId = UUID.randomUUID().toString();
        query = "INSERT INTO board_users(id, board_id, user_id, username, role) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bridgeId);
            stmt.setString(2, id);
            stmt.setString(3, ownerId);
            stmt.setString(4, username);
            stmt.setString(5, "owner");
            stmt.executeUpdate();
        } catch (Exception e) {
            return new Response<>("error", null, e.getMessage());
        }
        return new Response<NewBoardResponseDto>("ok", new NewBoardResponseDto(name, id), "Board " + name + " Created Successfully");
    }

    public synchronized static Response<BoardsResponseDto> handleBoardsList(Request<BoardsRequestDto> request) throws Exception {
        String userId = request.getPayload().getUserId();

        Connection conn = Database.getConnection();
        String query = "SELECT id, name, created_at FROM boards WHERE id IN (SELECT board_id FROM board_users WHERE user_id = ?) ORDER BY created_at DESC";
        ArrayList<String[]> boards = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                boards.add(new String[]{name, id});
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("error", null, e.getMessage());
        }

        return new Response<>("ok", new BoardsResponseDto(boards), "Boards Loaded Successfully");
    }

    public synchronized static Response<BoardUsersResponseDto> handleBoardUsersList(Request<BoardRequestDto> request) throws Exception {
        String board_id = request.getPayload().getId();

        Connection conn = Database.getConnection();
        String query = "SELECT user_id, username, role FROM board_users WHERE board_id= ?";
        ArrayList<String[]> associated_users = new ArrayList<>();
        String owner_id = null;
        String owner_username = null;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, board_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String role = rs.getString("role");
                if (role.equals("member")) {
                    String id = rs.getString("user_id");
                    String username = rs.getString("username");
                    associated_users.add(new String[]{username, id});
                } else {
                    owner_id = rs.getString("user_id");
                    owner_username = rs.getString("username");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("error", null, e.getMessage());
        }

        return new Response<>("ok", new BoardUsersResponseDto(owner_username, owner_id, associated_users), "Associated Users Loaded Successfully");
    }

    public synchronized static Response<?> handleAddUserToBoard(Request<AddUserToBoardRequestDto> request) throws Exception {
        String username = request.getPayload().getUsername();
        String board_id = request.getPayload().getBoardId();
        String board_name = request.getPayload().getBoardName();

        Connection conn = Database.getConnection();
        String query = "SELECT * FROM board_users WHERE username = ? AND board_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, board_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return new Response<>("error", null, "User Is Already A Member Of The Board");
            else {
                query = "SELECT * FROM users WHERE username = ?";
                PreparedStatement stmt1 = conn.prepareStatement(query);
                stmt1.setString(1, username);
                ResultSet results = stmt1.executeQuery();
                if (!results.next()) {
                    return new Response<>("error", null, "Username With " + username + " Not Found");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("error", null, e.getMessage());
        }

        query = "SELECT id FROM users WHERE username = ?";
        String user_id = null;
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) user_id = rs.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("error", null, e.getMessage());
        }

        query = "INSERT INTO board_users (user_id, board_id, username) VALUES(?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user_id);
            stmt.setString(2, board_id);
            stmt.setString(3, username);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("error", null, e.getMessage());
        }
        NotificationHandler.sendNotifications(board_id, username + " Added To Board " + board_name);
        return new Response<>("ok", null, username + " Added To Board Successfully");
    }
}

