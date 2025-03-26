package MusicStore;

import MusicStore.Commands.*;
import org.mariadb.jdbc.Connection;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicStoreApp {
    private static final Logger logger = LoggerFactory.getLogger(MusicStoreApp.class);

    public static void main(String[] args) {
        logger.info("Starting Music Store App.");

        LoginForm login = new LoginForm();
        Roles role = login.executeLogin();
        Connection connection = login.getConnection();

        CommandManager commandManager = new CommandManager();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Music Store Management App.");
        System.out.println("To view your available commands type 'commands'. To exit type 'exit'.");

        UserSession.startSession(role);

        while (true) {
            String userResponse = scanner.nextLine().trim().toLowerCase();
            if (userResponse.equalsIgnoreCase("exit")) {
                break;
            } else if (userResponse.equalsIgnoreCase("commands")) {
                handleCommands(commandManager, connection, scanner);
            }
            System.out.println("To view your available commands type 'commands'. To exit type 'exit'.");
        }

        scanner.close();
    }

    private static void handleCommands(CommandManager commandManager, Connection connection, Scanner scanner) {
        System.out.println("Choose commands category. Type: ADD, VIEW, UPDATE, LOOKUP, DELETE.");
        String userResponse = scanner.nextLine().trim().toUpperCase();
        try {
            CommandCategory category = CommandCategory.valueOf(userResponse);
            List<Command> commands = commandManager.getCommandsByCategory(category);

            if(commands.isEmpty()) {
                System.out.println("No commands available in this category.");
                return;
            }

            displayCommands(commands);
            executeSelectedCommand(commands, connection, scanner);
        }
        catch(IllegalArgumentException e) {
            System.out.println("Invalid category. Please try again.");
        }
        catch(Exception e) {
            System.out.println("Error occurred. Try again.");
        }
    }

    private static void displayCommands(List<Command> commands) {
        System.out.println("Available commands:");
        for (int i = 0; i < commands.size(); i++) {
            System.out.println((i + 1) + " - " + commands.get(i).getClass().getSimpleName());
        }
    }

    private static void executeSelectedCommand(List<Command> commands, Connection connection, Scanner scanner) {
        System.out.print("Enter the number of the command to execute: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());

            if (choice > 0 && choice <= commands.size()) {
                commands.get(choice - 1).execute(connection, scanner);
            } else {
                System.out.println("Invalid choice. Please select a valid command number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
}
