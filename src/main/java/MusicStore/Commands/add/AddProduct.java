package MusicStore.Commands.add;

import MusicStore.Commands.Command;
import MusicStore.Commands.CommandCategory;
import org.mariadb.jdbc.Connection;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.util.Scanner;

public class AddProduct implements Command {

    @Override
    public void execute(Connection connection, Scanner scanner) {
        System.out.println("Enter album id: ");
        int album_id = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter product typ (CD/Vinyl): ");
        String type = scanner.nextLine();

        System.out.println("Enter price: ");
        BigDecimal price = new BigDecimal(scanner.nextLine());

        addProductInDatabase(connection, album_id, type, price);
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADD;
    }

    private void addProductInDatabase(Connection connection, int album_id, String type, BigDecimal price) {
        String sql = "{CALL AddProduct(?, ?, ?)}";
        try (CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setInt(1, album_id);
            callableStatement.setString(2, type);
            callableStatement.setBigDecimal(3, price);

            callableStatement.execute();
            System.out.println("Successfully added a new product.");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
