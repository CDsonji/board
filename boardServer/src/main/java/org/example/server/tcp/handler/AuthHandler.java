package org.example.server.tcp.handler;

import org.example.database.Database;
import org.example.model.dto.AuthRequestDto;
import org.example.model.dto.AuthResponseDto;
import org.example.model.dto.LogoutRequestDto;
import org.example.model.message.Response;
import org.example.model.message.Request;
import org.example.server.tcp.TcpServer;
import org.example.utils.PasswordUtil;

import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class AuthHandler {

    public synchronized static Response<?> handleSignup(Request<AuthRequestDto> request) throws Exception {

        String username = request.getPayload().getUsername();
        String password = request.getPayload().getPassword();

        Connection conn = Database.getConnection();
        String query = "SELECT id FROM users WHERE username = ?";

        try  {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Response<>("error",null, "Username already exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create new user
        String salt = PasswordUtil.generateSaltBase64();
        String hash = PasswordUtil.hashPassword(password, salt);
        String id = UUID.randomUUID().toString();

        query = "INSERT INTO users(id, username, password_hash, salt) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.setString(2, username);
            stmt.setString(3, hash);
            stmt.setString(4, salt);
            stmt.executeUpdate();
        } catch (Exception e) {
            return new Response<>("error",null, e.getMessage());
        }

        return new Response<>("ok",null, "Signup Successful");
    }

    public synchronized static Response<AuthResponseDto> handleLogin(TcpServer.Session session, Request<AuthRequestDto> request) throws Exception {

        Connection conn = Database.getConnection();
        String username = request.getPayload().getUsername();
        String password = request.getPayload().getPassword();
        String sql = "SELECT id, password_hash, salt FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return new Response<>("error",null, "Username Not Found");
            }

            String id = rs.getString("id");
            String hash = rs.getString("password_hash");
            String salt = rs.getString("salt");

            if (TcpServer.sessions.containsKey(id) && TcpServer.sessions.get(id).socket.isConnected())
                return new Response<>("error",null, "You Are Already Logged In On Another Device");

            if (PasswordUtil.verify(password, salt, hash)) {
                TcpServer.sessions.put(id,session);
                return new Response<>("ok", new AuthResponseDto(username,id),"Login successful");
            } else {
                return new Response<>("error",null, "Password Not Correct");
            }
        } catch (Exception e) {
            return new Response<>("error",null, e.getMessage());
        }
    }

    public synchronized static Response<?> handleLogout(Request<LogoutRequestDto> request) {
        String id = request.getPayload().getId();

        // Remove session
        if (TcpServer.sessions.containsKey(id)) {
            TcpServer.sessions.remove(id);
            return new Response<>("ok", null, "Logout Successful");
        } else {
            return new Response<>("error", null, "User Not Logged In");
        }
    }

}
