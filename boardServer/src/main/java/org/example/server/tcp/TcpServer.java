package org.example.server.tcp;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.model.dto.*;
import org.example.model.message.Response;
import org.example.model.message.Request;
import org.example.server.tcp.handler.AuthHandler;
import org.example.server.tcp.handler.BoardHandler;
import org.example.server.tcp.handler.TaskHandler;
import org.example.utils.JSON;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TcpServer {

    private static final int PORT = 9000;

    public static class Session {
        public Socket socket;
        public BufferedWriter writer;

        public Session(Socket socket, BufferedWriter writer) {
            this.socket = socket;
            this.writer = writer;
        }

    }

    public static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private final Gson gson = new Gson();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // handle each client in a separate thread
                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Received: " + line);
                Request<?> genericReq = JSON.Objectify(line, new TypeToken<Request<?>>() {
                }.getType());

                // Process request
                Response<?> response = processRequest(genericReq, socket, writer);

                // Send response
                String respJson = JSON.Stringify(response);
                writer.write(respJson);
                writer.newLine();
                writer.flush();
            }

        } catch (Exception e) {
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            System.out.println("Client disconnected");
            for (String user_id: sessions.keySet()) {
                if (sessions.get(user_id).socket.equals(socket)) sessions.remove(user_id);
            }
        }
    }

    private Response<?> processRequest(Request<?> request, Socket socket, BufferedWriter writer) {
        try {
            return switch (request.getCommand()) {
                case "register" -> {
                    Request<AuthRequestDto> signupReq = JSON.Objectify(JSON.Stringify(request), new TypeToken<Request<AuthRequestDto>>() {
                    }.getType());
                    yield AuthHandler.handleSignup(signupReq);
                }
                case "login" -> {
                    Request<AuthRequestDto> loginReq = JSON.Objectify(JSON.Stringify(request), new TypeToken<Request<AuthRequestDto>>() {
                    }.getType());
                    yield AuthHandler.handleLogin(new Session(socket,writer), loginReq);
                }
                case "logout" -> {
                    Request<LogoutRequestDto> logoutReq = JSON.Objectify(JSON.Stringify(request), new TypeToken<Request<LogoutRequestDto>>() {
                    }.getType());
                    yield AuthHandler.handleLogout(logoutReq);
                }
                case "create_board" -> {
                    Request<NewBoardRequestDto> newBoardReq = JSON.Objectify(JSON.Stringify(request), new TypeToken<Request<NewBoardRequestDto>>() {
                    }.getType());
                    yield BoardHandler.handleBoardCreation(newBoardReq);
                }
                case "boards_list" -> {
                    Request<BoardsRequestDto> boardsReq = JSON.Objectify(JSON.Stringify(request), new TypeToken<Request<BoardsRequestDto>>() {
                    }.getType());
                    yield BoardHandler.handleBoardsList(boardsReq);
                }
                case "load_board_users" -> {
                    Request<BoardRequestDto> boardReq = JSON.Objectify(JSON.Stringify(request), new TypeToken<Request<BoardRequestDto>>() {
                    }.getType());
                    yield BoardHandler.handleBoardUsersList(boardReq);
                }
                case "add_user_to_board" -> {
                    Request<AddUserToBoardRequestDto> addReq = JSON.Objectify(JSON.Stringify(request), new TypeToken<Request<AddUserToBoardRequestDto>>() {
                    }.getType());
                    yield BoardHandler.handleAddUserToBoard(addReq);
                }
                case "add_task_to_board" -> {
                    Request<AddTaskRequestDto> addReq = JSON.Objectify(JSON.Stringify(request), new TypeToken<Request<AddTaskRequestDto>>() {
                    }.getType());
                    yield TaskHandler.handleAddTask(addReq);
                }
                case "load_board_tasks" -> {
                    Request<BoardRequestDto> tasksReq = JSON.Objectify(JSON.Stringify(request), new TypeToken<Request<BoardRequestDto>>() {
                    }.getType());
                    yield TaskHandler.handleTasksList(tasksReq);
                }
                case "update_task" -> {
                    Request<UpdateTaskRequest> updateReq = JSON.Objectify(JSON.Stringify(request), new TypeToken<Request<UpdateTaskRequest>>() {
                    }.getType());
                    yield TaskHandler.handleUpdateTask(updateReq);
                }
                case "delete_task" -> {
                    Request<TaskRequestDto> deleteReq = JSON.Objectify(JSON.Stringify(request), new TypeToken<Request<TaskRequestDto>>() {
                    }.getType());
                    yield TaskHandler.handleDeleteTask(deleteReq);
                }
                case "view_task" -> {
                    Request<TaskRequestDto> viewReq = JSON.Objectify(JSON.Stringify(request), new TypeToken<Request<TaskRequestDto>>() {
                    }.getType());
                    yield TaskHandler.handleViewTask(viewReq);
                }
                default -> new Response<>("error", null, "Unknown command");
            };


        } catch (Exception e) {
            e.printStackTrace();
            return new Response<>("error", null, "Server error: " + e.getMessage());
        }
    }
}



