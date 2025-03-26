package MusicStore.Commands.update;

import MusicStore.Commands.Command;
import MusicStore.Commands.CommandCategory;
import org.mariadb.jdbc.Connection;

import java.sql.CallableStatement;
import java.util.Scanner;

public class UpdateProductPrice implements Command {

    @Override
    public void execute(Connection connection, Scanner scanner) {
        System.out.println("Enter the product ID:");
        int productId = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter the new price:");
        double newPrice = Double.parseDouble(scanner.nextLine());

        try {
            updateProductPriceInDatabase(connection, productId, newPrice);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.UPDATE;
    }

    private void updateProductPriceInDatabase(Connection connection, int productId, double newPrice) throws Exception {
        String sql = "{CALL UpdateProductPrice(?, ?)}";
        try (CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setInt(1, productId);
            callableStatement.setDouble(2, newPrice);

            callableStatement.execute();
            System.out.println("Product price updated successfully.");
        }
    }
}
