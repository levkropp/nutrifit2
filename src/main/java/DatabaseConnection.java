import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:food.db"; // "food.db" is the SQLite database file

    public static Connection getConnection() throws Exception {
        Class.forName("org.sqlite.JDBC"); // SQLite JDBC driver
        return DriverManager.getConnection(URL);
    }
}
