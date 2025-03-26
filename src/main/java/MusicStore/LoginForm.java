package MusicStore;

import org.mariadb.jdbc.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class LoginForm {
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/musicstore";
    Connection connection;

    public Roles executeLogin() {
        boolean isAuthenticated = false;
        String user = "";
        String password = "";
        while(!isAuthenticated) {
            user = askForUserName();
            password = askForPassword();
            isAuthenticated = authenticate(user, password);
        }
        if(user.equals("employee")) {
            return Roles.EMPLOYEE;
        }
        return Roles.MANAGER;
    }
    private String askForUserName() {
        String userName = null;
        Scanner scanner = new Scanner(System.in);
        while(userName == null) {
            System.out.println("Please enter username: ");
            userName = scanner.nextLine();
            userName = userName.trim().toLowerCase();
        }
        return userName;
    }

    private String askForPassword() {
        String pass = null;
        Scanner scanner = new Scanner(System.in);
        while(pass == null) {
            System.out.println("Please enter password: ");
            pass = scanner.nextLine();
            pass = pass.trim();
        }
        return pass;
    }

    private boolean authenticate(String user, String password) {
        try {
            connection = (Connection) DriverManager.getConnection(DB_URL, user, password);
            System.out.println("Successfully logged in as: " + user);
            return true;
        }
        catch (Exception e){
            System.out.println("Invalid username or password. Try again.");
            return false;
        }
    }

    public Connection getConnection() {return this.connection; }
}
