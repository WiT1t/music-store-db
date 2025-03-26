package MusicStore.Commands;

import org.mariadb.jdbc.Connection;

import java.util.Scanner;

public interface Command {
    void execute(Connection connection, Scanner scanner);
    CommandCategory getCategory();
}
