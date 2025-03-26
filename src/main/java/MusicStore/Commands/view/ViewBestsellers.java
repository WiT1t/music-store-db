package MusicStore.Commands.view;

import MusicStore.Commands.Command;
import MusicStore.Commands.CommandCategory;
import org.mariadb.jdbc.Connection;
import org.mariadb.jdbc.client.result.ResultSetMetaData;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class ViewBestsellers implements Command {
    @Override
    public void execute(Connection connection, Scanner scanner) {
        ViewBestsellersFromDataBase(connection);
    }

    @Override
    public CommandCategory getCategory() { return CommandCategory.VIEW; }

    private void ViewBestsellersFromDataBase(Connection connection) {
        String sql = "{CALL ViewBestsellers}";

        try(CallableStatement callableStatement = connection.prepareCall(sql)) {
            ResultSet result = callableStatement.executeQuery();
            ResultSetMetaData metaData = (ResultSetMetaData) result.getMetaData();

            int columns = metaData.getColumnCount();

            System.out.println("BESTSELLERS: ");
            for(int i=0; i<columns; i++) {
                System.out.print(metaData.getColumnName(i+1) + "\t");
            }
            System.out.println();

            while(result.next()) {
                for(int i=0; i<columns; i++) {
                    System.out.print(result.getString(i+1) +"\t");
                }
                System.out.println();
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
