import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class query {
    static String tableName = "Users";
    static String personalTableSchema;
    //    static String personaltableSchema = "(" +
//            "date date PRIMARY KEY," +
//            "tasks VARCHAR(255)" +
//            ");";
    static ResultSet resultset;
    //    static String insert = "INSERT INTO Users VALUES ";
    static String selectAll = "SELECT * FROM Users";

    static void showChoices() {
        System.out.print("What do you want to do..." +
                "\n1) Enter Today diary" +
                "\n2) Enter data of another date" +
                "\n3) Remove Data of any date" +
                "\n4) See History of particular Date" +
                "\n5) Delete account" +
                "\n6) Exit" +
                "\nEnter your choice: ");
    }

    static void insertUser(Connection connection) throws Exception {
        System.out.println("Creating Account...");
        Scanner sc = new Scanner(System.in);
        String userName;
        while (true) {
            System.out.print("Enter userName: ");
            userName = sc.next();
            if (userExist(connection, userName))
                System.out.println("User name : " + userName + " is already taken, choose another name.");
            else
                break;
        }
        String password, password2;

        while (true) {
            System.out.print("Set Password: ");
            password = sc.next();
            System.out.print("Enter Password again: ");
            password2 = sc.next();
            if (!password.equals(password2))
                System.out.println("Passwords are not same, Enter again...");
            else
                break;
        }
//            sc.close();
        String sql = "INSERT INTO Users (userName, password, tasks) VALUES (?, ?, ?);";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, userName);
        ps.setString(2, password);
        String tasks = getSchema(userName);
        ps.setString(3, tasks);
        System.out.println("contacting database...");
        int rowsaff = ps.executeUpdate();
        if (rowsaff > 0) {
            while (!tableExists(connection, userName)) {
                createTable(connection, userName);
            }
            System.out.println("Account with username:" + userName + " created, login to access account");
        } else {
            System.out.println("Account creation failed !!!\nEnter datails again:");
        }
    }

    static void createTable(Connection connection, String userName) throws Exception {
        System.out.println("creating table for " + userName);
        String sql = personalTableSchema;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.executeUpdate();
        System.out.println("Database for " + userName + " is generated.");
    }

    static boolean insertComa() {
        personalTableSchema = personalTableSchema + ", ";
        return true;
    }

    static String getSchema(String tableName) {
        personalTableSchema = "CREATE TABLE " + tableName + " ( date date PRIMARY KEY";
        String s = "";
        Scanner sc = new Scanner(System.in);
        String task = "";
        System.out.println("Enter tasks (enter 'done' when entered all tasks, there should be atleast one task): ");
        do {
            task = sc.next();
            if (!task.equals("done")) {
                personalTableSchema = personalTableSchema + ", " + task + " bool";
                s = s + task + ",";
            }
        } while (!task.equals("done"));
        personalTableSchema = personalTableSchema + ");";
        return s;
    }

    static boolean tableExists(Connection connection, String tableName) throws SQLException {
        String databaseName = connect.username;
        String query = "SELECT COUNT(*) AS table_count " +
                "FROM INFORMATION_SCHEMA.TABLES " +
                "WHERE TABLE_SCHEMA = '" + databaseName + "' " +
                "AND TABLE_NAME = '" + tableName + "'";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                int tableCount = resultSet.getInt("table_count");
                return tableCount > 0;
            }
        }
        return false;
    }

    static void dropTable(Connection connection, String tableName) throws Exception {
        while (tableExists(connection, tableName)) {
            String sql = "DROP TABLE " + tableName;
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();
        }
    }

    static boolean verifyUser(Connection connection, String userName) throws Exception {
        System.out.println("Verifying user...");

        Scanner sc = new Scanner(System.in);
        String password;
        System.out.print("Enter Password: ");
        password = sc.next();
        System.out.println("verifying user...");
        String sql = "SELECT password FROM " + tableName + " WHERE BINARY userName = '" + userName + "';";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            if (password.equals(rs.getString("password"))) {
                System.out.println("LogIn successfull...");
                return true;
            } else {
                System.out.println("Wrong password !!!\nTry LogIn again.");
                return false;
            }
        } else
            System.out.println("password data not extracted properly.");
        return false;
    }

    static boolean userExist(Connection connection, String userName) {
        System.out.println("Checking user existance...");
        String sql = "SELECT COUNT(*) AS count FROM Users WHERE BINARY userName = '" + userName + "'";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt("count");
            }
            ps.close();
            return count > 0;
        } catch (Exception e) {
            System.out.println("exception in userExist");
            e.printStackTrace();
        }
        return true;
    }

    static void insertData(Connection connection, String userName, LocalDate date) throws Exception {
        if (dataExist(connection, userName, date)) {
            System.out.println("Data for data:" + date + " already inserted, Delete that data to update.");
            return;
        }
        System.out.println("inserting data for date:" + date);
        String sql = "SELECT tasks FROM " + tableName + " WHERE BINARY userName='" + userName + "';";
        PreparedStatement ps = connection.prepareStatement(sql);
        resultset = ps.executeQuery();
        String s = "";
        if (resultset.next())
            s = resultset.getString("tasks");
        String[] tasks = s.split(",");
        System.out.println("Mark the completed tasks (1:completed, 0:not completed):");
        char completed;
        String value = "";
        Scanner sc = new Scanner(System.in);
        for (int i = 0; i < tasks.length; i++) {
            System.out.print(tasks[i]+": ");
            completed = sc.next().charAt(0);
            value = value + completed;
            if (i != tasks.length - 1)
                value = value + ",";
        }

        sql = "INSERT INTO " + userName + " VALUES (?, " + value + ")";
        ps = connection.prepareStatement(sql);
        ps.setDate(1, java.sql.Date.valueOf(date));
        int count = ps.executeUpdate();
        if (count > 0)
            System.out.println("Data of date " + date + " stored successfully.");
        else
            System.out.println("Data of date " + date + " was not stored, please try again.");
    }

    static boolean dataExist(Connection connection, String userName, LocalDate date) throws Exception {
        String sql = "SELECT COUNT(*) AS count FROM " + userName + " WHERE date=(?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDate(1, Date.valueOf(date));
        resultset = ps.executeQuery();
        if (resultset.next()) {
            int count = resultset.getInt("count");
            if (count > 0) return true;
        }
        System.out.println("Data of date:" + date + " does not exist!!!");
        return false;
    }

    static void deleteData(Connection connection, String userName, LocalDate date) throws Exception {
        System.out.println("deleting data of date:" + date);
        String sql = "DELETE FROM " + userName + " WHERE date=(?);";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDate(1, Date.valueOf(date));
        while (dataExist(connection, userName, date))
            ps.executeUpdate();
        System.out.println("Data of date:" + date + " deleted.");
    }

    static void showData(Connection connection, String userName, LocalDate date) throws Exception {
        if (!dataExist(connection, userName, date))
            return;
        String sql = "SELECT tasks FROM " + tableName + " WHERE BINARY userName = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, userName);
        ResultSet rs = ps.executeQuery();
        String s = "";
        if(rs.next())
            s = rs.getString("tasks");
        String[] tasks = s.split(",");
        for(int i=0; i<=tasks.length; i++)
            System.out.print("+---------------");
        System.out.println("+");
        System.out.printf("|%-15s", "date");
        for(String task : tasks){
            if(task.length()>15)
                System.out.printf("|%-12s...", task.substring(0, 12));
            else
                System.out.printf("|%-15s", task);
        }
        System.out.println("|");
        for(int i=0; i<=tasks.length; i++)
            System.out.print("+---------------");
        System.out.println("+");
        sql = "SELECT * FROM " + userName + " WHERE date = ?";
        ps = connection.prepareStatement(sql);
        ps.setDate(1, Date.valueOf(date));
        rs = ps.executeQuery();
        if (rs.next()) {
            Date d = rs.getDate("date");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            // Format the Date object into a String
            String dateString = dateFormat.format(d);
            System.out.printf("|%-15s", dateString);
            for(String task : tasks)
                System.out.printf("|%-15d", rs.getInt(task));
        }
        System.out.println("|");
        for(int i=0; i<=tasks.length; i++)
            System.out.print("+---------------");
        System.out.println("+");
    }
}
