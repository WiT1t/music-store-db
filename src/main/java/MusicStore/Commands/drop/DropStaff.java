package MusicStore.Commands.drop;

import MusicStore.Commands.Command;
import MusicStore.Commands.CommandCategory;
import org.mariadb.jdbc.Connection;

import java.sql.CallableStatement;
import java.util.Scanner;

public class DropStaff implements Command {

    @Override
    public void execute(Connection connection, Scanner scanner) {
        System.out.println("Enter employee id: ");
        try{
            int ID = Integer.parseInt(scanner.nextLine().trim());

            dropStaffInDatabase(connection, ID);
        }
        catch (NumberFormatException e) {
            System.out.println("ID is an integer.");
        }
    }

    @Override
    public CommandCategory getCategory() { return CommandCategory.DELETE; }

    private void dropStaffInDatabase(Connection connection, int ID) {
        String sql = "{CALL dropStaff(?)}";
        try (CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setInt(1, ID);

            callableStatement.execute();
            System.out.println("Successfully deleted staff member.");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
