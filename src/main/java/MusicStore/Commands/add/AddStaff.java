package MusicStore.Commands.add;

import MusicStore.Commands.Command;
import MusicStore.Commands.CommandCategory;
import org.mariadb.jdbc.Connection;

import java.sql.CallableStatement;
import java.util.Scanner;

public class AddStaff implements Command {

    @Override
    public void execute(Connection connection, Scanner scanner) {
        System.out.println("Enter name: ");
        String name = scanner.nextLine().trim();

        System.out.println("Enter surname: ");
        String surname = scanner.nextLine().trim();

        System.out.println("Enter position: ");
        String position = scanner.nextLine().trim();

        System.out.println("Enter phone number: ");
        String number = scanner.nextLine().trim();

        addStaffInDatabase(connection, name, surname, position, number);
    }

    @Override
    public CommandCategory getCategory() { return CommandCategory.ADD; }

    private void addStaffInDatabase(Connection connection, String name, String surname, String position, String phone_number) {
        String sql = "{CALL addStaff(?,?,?,?)}";
        try(CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setString(1, name);
            callableStatement.setString(2, surname);
            callableStatement.setString(3, position);
            callableStatement.setString(4, phone_number);

            callableStatement.execute();
            System.out.println("Successfully added a new staff member.");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
