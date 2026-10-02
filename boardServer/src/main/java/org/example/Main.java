package org.example;

import org.example.database.Database;
import org.example.server.tcp.TcpServer;

public class Main {
    public static void main(String[] args) {
        Database.initialize();
        var server = new TcpServer();
        server.start();
    }
}