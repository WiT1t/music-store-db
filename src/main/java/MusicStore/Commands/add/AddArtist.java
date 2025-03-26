package MusicStore.Commands.add;

import MusicStore.Commands.Command;
import MusicStore.Commands.CommandCategory;
import org.mariadb.jdbc.Connection;

import java.sql.CallableStatement;
import java.util.Scanner;

public class AddArtist implements Command {

    @Override
    public void execute(Connection connection, Scanner scanner) {
        System.out.println("Enter artist first name: ");
        String name = scanner.nextLine();

        System.out.println("Enter artist surname: ");
        String surname = scanner.nextLine();

        System.out.println("Enter artist nickname or press 'Enter': ");
        String nickname = scanner.nextLine();

        System.out.println("Enter artist country: ");
        String country = scanner.nextLine();

        addArtistInDatabase(connection, name, surname, nickname, country);
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADD;
    }

    private void addArtistInDatabase(Connection connection, String name, String surname, String nickname, String country) {
        String sql = "{CALL AddArtist(?, ?, ?, ?)}";
        try (CallableStatement callableStatement = connection.prepareCall(sql)) {
            callableStatement.setString(1, name);
            callableStatement.setString(2, surname);
            callableStatement.setString(3, nickname);
            callableStatement.setString(4, country);

            callableStatement.execute();
            System.out.println("Successfully added a new artist.");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
