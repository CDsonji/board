package com.example.board.client;


import com.example.board.controllers.notitfication.NotificationHandler;
import com.example.board.model.dto.NotificationDto;
import com.example.board.model.message.Response;
import com.example.board.model.message.Request;
import com.example.board.utils.JSON;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TcpClient {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();
    private final NotificationHandler notificationHandler;

    public TcpClient(String host, int port,NotificationHandler notificationHandler) throws IOException {
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        this.notificationHandler = notificationHandler;
        // Start background reader
        new Thread(this::listen).start();
    }



    public <T> Response<T> sendRequest(Request<?> request, Class<T> dataClass) {
        String json = JSON.Stringify(request);
        writer.println(json);

        try {
            String respJson = responseQueue.take(); // waits for matching response
            return JSON.Objectify(respJson, TypeToken.getParameterized(Response.class, dataClass).getType());
        } catch (Exception e) {
            return new Response<>("error", null, "Connection lost");
        }
    }

    private void listen() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("\"notification\":")) {
                    if (notificationHandler != null) {
                        notificationHandler.onNotification(JSON.Objectify(line, new TypeToken<NotificationDto>() {}.getType()));
                    }
                } else {
                    responseQueue.offer(line); // route back to sendRequest
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        socket.close();
    }

}

