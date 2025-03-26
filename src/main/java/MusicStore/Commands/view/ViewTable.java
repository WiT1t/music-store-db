package MusicStore.Commands.view;

import MusicStore.Commands.Command;
import MusicStore.Commands.CommandCategory;
import org.mariadb.jdbc.Connection;
import org.mariadb.jdbc.Statement;
import org.mariadb.jdbc.client.result.ResultSetMetaData;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class ViewTable implements Command {

    @Override
    public void execute(Connection connection, Scanner scanner) {
        System.out.println("Enter table name (Artist, Album, Product, Store, Shipments, Sales, Staff): ");
        String name = scanner.nextLine();

        viewTableFromDatabase(connection, name);
    }

    @Override
    public CommandCategory getCategory() { return CommandCategory.VIEW; }

    private void viewTableFromDatabase(Connection connection, String table_name) {
        String sql = "SELECT * FROM " + table_name;
        try {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            ResultSetMetaData metaData = (ResultSetMetaData) result.getMetaData();

            int columns = metaData.getColumnCount();

            System.out.println("Table "+ table_name+ ": ");
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
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
