package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:repair_system.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void initialize() {
        String createUsers = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL DEFAULT 'CLIENT',
                full_name TEXT,
                phone TEXT
            )
            """;

        String createRequests = """
            CREATE TABLE IF NOT EXISTS repair_requests (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                client_id INTEGER,
                client_name TEXT NOT NULL,
                client_phone TEXT NOT NULL,
                device_type TEXT NOT NULL,
                device_model TEXT,
                serial_number TEXT,
                problem_description TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'NEW',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                technician_notes TEXT,
                repair_cost REAL DEFAULT 0.0,
                FOREIGN KEY (client_id) REFERENCES users(id)
            )
            """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(createRequests);

            // Мастер по умолчанию
            stmt.execute("""
                INSERT OR IGNORE INTO users (id, username, password, role, full_name)
                VALUES (1, 'master', 'master', 'MASTER', 'Главный мастер')
                """);

            System.out.println("База данных инициализирована");
        } catch (SQLException e) {
            System.err.println("Ошибка инициализации БД: " + e.getMessage());
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}