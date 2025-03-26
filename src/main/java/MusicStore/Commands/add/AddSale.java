package MusicStore.Commands.add;

import MusicStore.Commands.Command;
import MusicStore.Commands.CommandCategory;
import org.mariadb.jdbc.Connection;

import java.sql.CallableStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Scanner;

public class AddSale implements Command {

    @Override
    public void execute(Connection connection, Scanner scanner) {
        try{
            LocalDateTime now = LocalDateTime.now();

            System.out.println("Enter your staff id: ");
            int staffID = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("Enter product id: ");
            int prodID = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("Enter product quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());

            addSaleInDatabase(connection, now, staffID, prodID, quantity);
        }
        catch (Exception e) {
            System.out.println("Unexpected error occurred.");
        }
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADD;
    }

    private void addSaleInDatabase(Connection connection, LocalDateTime now, int staffID, int product, int quantity) {
        String sql = "{CALL AddSale(?,?,?,?)}";
        try(CallableStatement callableStatement = connection.prepareCall(sql)) {
            Timestamp currentDate = Timestamp.valueOf(now);

            callableStatement.setTimestamp(1, currentDate);
            callableStatement.setInt(2, staffID);
            callableStatement.setInt(3, product);
            callableStatement.setInt(4, quantity);

            callableStatement.execute();
            System.out.println("Successfully added a new sale.");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
