package MusicStore.Commands;

import MusicStore.Commands.add.*;
import MusicStore.Commands.drop.DropStaff;
import MusicStore.Commands.lookup.*;
import MusicStore.Commands.update.UpdateProductPrice;
import MusicStore.Commands.view.*;
import MusicStore.Roles;
import MusicStore.UserSession;

import java.util.*;

public class CommandManager {
    public final Map<Roles, List<Command>> roleCommands = new HashMap<>();

    public CommandManager() {
        assignEmployeeCommands();
        assignManagerCommands();
    }

    private void assignEmployeeCommands() {
        roleCommands.put(Roles.EMPLOYEE, Arrays.asList(
                new ViewTable(),
                //new ViewStoreAvailable(),
                //new ViewStoreByPrice(),
                //new ViewBestsellers(),
                new LookupProductByAlbumName(),
                new LookupProductByArtistName(),
                new LookupProductByType(),
                new AddShipment(),
                new AddSale()
        ));
    }

    private void assignManagerCommands() {
        roleCommands.put(Roles.MANAGER, Arrays.asList(
                new ViewTable(),
                new ViewStoreAvailable(),
                new ViewBestsellers(),
                new LookupProductByAlbumName(),
                new LookupProductByArtistName(),
                new LookupProductByType(),
                new AddShipment(),
                new AddSale(),
                new AddAlbum(),
                new AddArtist(),
                new AddProduct(),
                new AddStaff(),
                new DropStaff(),
                new UpdateProductPrice()
        ));
    }

    public List<Command> getAvailableCommands() {
        UserSession session = UserSession.getInstance();
        Roles role = session.getRole();
        return roleCommands.getOrDefault(role, Collections.emptyList());
    }

    public List<Command> getCommandsByCategory(CommandCategory category) {
        List<Command> allCommands = getAvailableCommands();
        List<Command> filteredCommands = new ArrayList<>();
        for (Command command : allCommands) {
            if (command.getCategory() == category) {
                filteredCommands.add(command);
            }
        }
        return filteredCommands;
    }
}
