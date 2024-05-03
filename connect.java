import java.sql.*;

public class connect {
    // Database connection parameters
    static String url = "jdbc:mysql://sql6.freesqldatabase.com:3306/sql6702394"; // JDBC URL
    static String username = "sql6702394"; // Database username
    // here database name and user name sre same
    static String password = "b2I4zZAjet"; // Database password
    static Connection connection;
//    static Statement statement;

    connect() {
        try {
            // Establishing the connection
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("connection established...");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
