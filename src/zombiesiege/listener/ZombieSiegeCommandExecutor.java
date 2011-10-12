package zombiesiege.listener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import zombiesiege.ZombieSiege;

public class ZombieSiegeCommandExecutor implements CommandExecutor {

    private final ZombieSiege instance;
    
    public ZombieSiegeCommandExecutor(ZombieSiege instance) {
        this.instance = instance;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("start")) {
                instance.startGame(p);
                return true;
            }
            if (cmd.getName().equalsIgnoreCase("end")) {
                instance.endGame(p);
                return true;
            }
        }
        return false;
    }

}
