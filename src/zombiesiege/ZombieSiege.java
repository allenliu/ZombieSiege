package zombiesiege;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import zombiesiege.listener.ZombieSiegeBlockListener;
import zombiesiege.listener.ZombieSiegeCommandExecutor;
import zombiesiege.listener.ZombieSiegeEntityListener;
import zombiesiege.listener.ZombieSiegePlayerListener;
import zombiesiege.listener.ZombieSiegeWorldListener;

public class ZombieSiege extends JavaPlugin {

    private final static String VERSION = "1.0";
    
    private final ZombieSiegeCommandExecutor commandExecutor = new ZombieSiegeCommandExecutor(this);
    private final ZombieSiegePlayerListener playerListener = new ZombieSiegePlayerListener(this);
    private final ZombieSiegeEntityListener entityListener = new ZombieSiegeEntityListener(this);
    private final ZombieSiegeBlockListener blockListener = new ZombieSiegeBlockListener(this);
    private final ZombieSiegeWorldListener worldListener = new ZombieSiegeWorldListener(this);
    
    private ZombieSiegeGame game;
    
    public Logger log = Logger.getLogger("Minecraft");
    
    @Override
    public void onDisable() {
        log.info("ZombieSiege" + VERSION + " disabled."); 
    }

    @Override
    public void onEnable() {
        log.info("ZombieSiege " + VERSION + " enabled.");
        getCommand("start").setExecutor(commandExecutor);
        getCommand("end").setExecutor(commandExecutor);
        getCommand("stats").setExecutor(commandExecutor);
        getCommand("debug").setExecutor(commandExecutor);
        registerEvents();
    }

    private void registerEvents() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvent(Event.Type.PLAYER_RESPAWN, playerListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_BED_ENTER, playerListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_PORTAL, playerListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.CREATURE_SPAWN, entityListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.CHUNK_UNLOAD, worldListener, Event.Priority.Normal, this);
    }
    
    public void startGame(Player p) {
        if (game == null) {
            game = new ZombieSiegeGame(this, p);
        } else {
            p.sendMessage("ZombieSiege is already in session!");
        }
    }
    
    public void endGame(Player p) {
        if (game == null) {
            if (p != null) {
                p.sendMessage("There is no game in session.");
            }
        } else {
            game.endGame();
            game = null;
        }
    }
    
    public ZombieSiegeGame getGame() {
        return game;
    }
    
}
