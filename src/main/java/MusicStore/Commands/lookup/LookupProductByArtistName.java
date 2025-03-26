package MusicStore.Commands.lookup;

import MusicStore.Commands.Command;
import MusicStore.Commands.CommandCategory;
import org.mariadb.jdbc.Connection;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class LookupProductByArtistName implements Command {

    @Override
    public void execute(Connection connection, Scanner scanner) {
        System.out.println("Enter artist first name (or press Enter): ");
        String name = scanner.nextLine().trim();

        System.out.println("Enter artist surname (or press Enter): ");
        String surname = scanner.nextLine().trim();

        System.out.println("Enter artist nickname (or press Enter): ");
        String nickname = scanner.nextLine().trim();

        lookupProductInDataBase(connection, name, surname, nickname);
    }

    @Override
    public CommandCategory getCategory() { return CommandCategory.LOOKUP; }

    private void lookupProductInDataBase(Connection connection, String name, String surname, String nickname) {
        String sql = "{CALL lookupProductByArtistName(?, ?, ?)}";
        try(CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setString(1, name);
            callableStatement.setString(2, surname);
            callableStatement.setString(3, nickname);

            ResultSet result = callableStatement.executeQuery();

            System.out.println("Products found: ");
            while(result.next()) {
                int productId = result.getInt(1);
                String album = result.getString(2);
                String type = result.getString(3);
                BigDecimal price = result.getBigDecimal(4);
                int quantity = result.getInt(5);
                System.out.println("PRODUCT ID: "+productId+" ALBUM: "+album+" TYPE: "+type+" PRICE: "+price+" IN STORE: " + quantity);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
