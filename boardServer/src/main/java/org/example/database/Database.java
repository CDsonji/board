package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:board.db";

    /**
     * Get a connection to the database.
     * Each thread should call this separately.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Initialize all tables.
     * Call this once at server startup.
     */
    public static void initialize() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // users table
            String sqlUsers = """
                        CREATE TABLE IF NOT EXISTS users (
                            id TEXT PRIMARY KEY,
                            username TEXT UNIQUE NOT NULL,
                            password_hash TEXT NOT NULL,
                            salt TEXT NOT NULL,
                            created_at INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)
                        );
                    """;

            // boards table
            String sqlBoards = """
                        CREATE TABLE IF NOT EXISTS boards (
                            id TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            owner_id TEXT NOT NULL,
                            created_at INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000),
                            FOREIGN KEY(owner_id) REFERENCES users(id)
                        );
                    """;

            // tasks table
            String sqlTasks = """
                        CREATE TABLE IF NOT EXISTS tasks (
                            id TEXT PRIMARY KEY,
                            board_id TEXT NOT NULL,
                            title TEXT NOT NULL,
                            description TEXT,
                            status TEXT NOT NULL DEFAULT 'todo',
                            priority TEXT NOT NULL,
                            due_date INTEGER,
                            created_at INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000),
                            updated_at INTEGER,
                            FOREIGN KEY(board_id) REFERENCES boards(id)
                        );
                    """;

            // board_users association table
            String sqlBoardUsers = """
                        CREATE TABLE IF NOT EXISTS board_users (
                            id TEXT PRIMARY KEY,
                            board_id TEXT NOT NULL,
                            user_id TEXT NOT NULL,
                            role TEXT DEFAULT 'member',
                            username TEXT NOT NULL,
                            created_at INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000),
                            FOREIGN KEY(board_id) REFERENCES boards(id),
                            FOREIGN KEY(user_id) REFERENCES users(id),
                            FOREIGN KEY(username) REFERENCES users(username)
                        );
                    """;


            stmt.execute(sqlUsers);
            stmt.execute(sqlBoards);
            stmt.execute(sqlTasks);
            stmt.execute(sqlBoardUsers);

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

