package MusicStore.Commands.add;

import MusicStore.Commands.Command;
import MusicStore.Commands.CommandCategory;
import org.mariadb.jdbc.Connection;

import java.sql.CallableStatement;
import java.sql.Date;
import java.util.Scanner;

public class AddAlbum implements Command {

    @Override
    public void execute(Connection connection, Scanner scanner) {
        System.out.println("Enter artist id: ");
        int artistid = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter album name: ");
        String name = scanner.nextLine();

        System.out.println("Enter release date (YYYY-MM-DD): ");
        Date date = Date.valueOf(scanner.nextLine());

        addAlbumInDatabase(connection, artistid, name, date);
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADD;
    }

    private void addAlbumInDatabase(Connection connection, int artistid, String name, Date date) {
        String sql = "{CALL AddAlbum(?, ?, ?)}";

        try (CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setInt(1, artistid);
            callableStatement.setString(2, name);
            callableStatement.setDate(3, date);

            callableStatement.execute();
            System.out.println("Successfully added a new album.");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
