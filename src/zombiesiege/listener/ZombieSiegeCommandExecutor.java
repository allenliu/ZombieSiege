package zombiesiege.listener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import zombiesiege.ZombieSiege;
import zombiesiege.ZombieSiegeGame;

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
            if (cmd.getName().equalsIgnoreCase("stats")) {
                ZombieSiegeGame game = instance.getGame();
                if (game == null) {
                    sender.sendMessage("There is no game in session.");
                    return true;
                }
                game.sendStats(sender);
                return true;
            }
            if (cmd.getName().equalsIgnoreCase("debug")) {
                ZombieSiegeGame game = instance.getGame();
                if (game == null) {
                    sender.sendMessage("There is no game in session.");
                    return true;
                }
                if (args.length == 1) {
                    game.dayNum = Integer.parseInt(args[0]);
                    sender.sendMessage("Changed day to " + game.dayNum);
                    return true;
                }
            }
        }
        return false;
    }

}
