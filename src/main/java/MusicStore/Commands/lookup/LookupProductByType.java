package MusicStore.Commands.lookup;

import MusicStore.Commands.Command;
import MusicStore.Commands.CommandCategory;
import org.mariadb.jdbc.Connection;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class LookupProductByType implements Command {

    @Override
    public void execute(Connection connection, Scanner scanner) {
        System.out.println("Enter product type (CD/Vinyl): ");
        String type = scanner.nextLine().trim();

        lookupProductInDataBase(connection, type);
    }

    @Override
    public CommandCategory getCategory() { return CommandCategory.LOOKUP; }

    private void lookupProductInDataBase(Connection connection, String type) {
        String sql = "{CALL lookupProductByType(?)}";
        try(CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setString(1, type);

            ResultSet result = callableStatement.executeQuery();

            System.out.println("Products found: ");
            while(result.next()) {
                int productId = result.getInt(1);
                String artist = result.getString(2) + " " + result.getString(3) + " ; " + result.getString(4);
                String album = result.getString(5);
                BigDecimal price = result.getBigDecimal(6);
                int quantity = result.getInt(7);
                System.out.println("PRODUCT ID: "+productId+" ARTIST: "+artist+" ALBUM: "+album+" PRICE: "+price+" IN STORE: " + quantity);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
