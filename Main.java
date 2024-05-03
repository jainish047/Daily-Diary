import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    static ResultSet resultSet;
    static Connection connection;
    static String userName;
    public static void main(String[] args) {
        // Database connection parameters
        connect c = new connect();      // connection & statement initialised
        connection = connect.connection;

        try {
            while(true){
                Scanner sc = new Scanner(System.in);
                System.out.print("Enter User Name to login (enter 'NULL' to create new account): ");
                userName = sc.next();
                if (userName.equals("NULL")) {
                    query.insertUser(connection);
                    continue;
                }
                if (!query.userExist(connection, userName)) {
                    System.out.println("User:" + userName + " does not exist, Enter user name again.");
                    continue;
                }
                if(query.verifyUser(connection, userName))
                    break;
            }
            Scanner s = new Scanner (System.in);
            int choice = 0;
            LocalDate date;
            while(choice!=6){
                query.showChoices();
                choice = s.nextInt();
                switch (choice){
                    case 1:
                        date = LocalDate.now();
                        query.insertData(connection, userName, date);
                        break;
                    case 2:
                        date = getDate();
                        query.insertData(connection, userName, date);
                        break;
                    case 3:
                        date = getDate();
                        query.deleteData(connection, userName, date);
                        break;
                    case 4:
                        date = getDate();
                        query.showData(connection, userName, date);
                        break;
                    case 5:
                        String sql = "DELETE FROM "+query.tableName+" WHERE userName='"+userName+"';";
                        PreparedStatement ps = connection.prepareStatement(sql);
                        ps.executeUpdate();
                        query.dropTable(connection, userName);
                        choice = 6;
                    case 6:
                        System.out.println("Disconnecting...");
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Closing resources
        try{
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        System.out.println("Disconnected.");
    }
    static LocalDate getDate(){
        Scanner s = new Scanner (System.in);
        System.out.print("Enter the date (YYYY-MM-DD): ");
        String inputDate = s.nextLine();
        // Parse the user input into a LocalDate object
        return LocalDate.parse(inputDate);
    }
}