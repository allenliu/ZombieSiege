package zombiesiege;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import zombiesiege.thread.TimeController;
import zombiesiege.thread.ZombieController;
import zombiesiege.thread.ZombieSpawner;

public class ZombieSiegeGame {

    private static final Material[] EQUIPABLES = {
        Material.WOOD_SPADE, Material.WOOD_HOE, Material.WOOD_SWORD, Material.WOOD_AXE, Material.WOOD_PICKAXE,
        Material.STONE_SPADE, Material.STONE_HOE, Material.STONE_SWORD, Material.STONE_AXE, Material.STONE_PICKAXE,
        Material.IRON_SPADE, Material.IRON_HOE, Material.IRON_SWORD, Material.IRON_AXE, Material.IRON_PICKAXE,
        Material.GOLD_SPADE, Material.GOLD_HOE, Material.GOLD_SWORD, Material.GOLD_AXE, Material.GOLD_PICKAXE,
        Material.DIAMOND_SPADE, Material.DIAMOND_HOE, Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE
    };
    
    private final Comparator<Player> playerComparator = new PlayerComparator();

    private final ZombieSiege instance;
    private final World world;
    private final Location base;
    private final Map<String, Integer> playerKills = new HashMap<String, Integer>();
    private final Map<String, Integer> playerDeaths = new HashMap<String, Integer>();
    public List<Monster> horde = new ArrayList<Monster>();


    public boolean isDay = true;
    public int dayNum = 0;
    public boolean firstMessage = false;

    public boolean zombieBlockBreak = false;
    public boolean zombieFireArrow = false;
    public boolean zombieExplosion = false;
    public boolean zombieGiant = false;

    private final Random r = new Random();

    public ZombieSiegeGame(ZombieSiege instance, Player p) {
        this.instance = instance;
        world = p.getWorld();
        base = p.getLocation();
        setupWorld();
        setupBase();
        setupPlayers();

        instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new ZombieSpawner(this), 0, 400L);
        instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new ZombieController(this), 0, 20L);
        instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new TimeController(this), 0, 200L);

        sendWelcomeMessage();
    }

    public void endGame() {
        sendMessageToAll("The current ZombieSiege session has been ended.");
        instance.getServer().getScheduler().cancelAllTasks();
        for (Entity entity : world.getEntities()) {
            if ((entity instanceof Monster) || (entity instanceof Projectile)) {
                entity.remove();
            }
        }
    }

    public World getWorld() {
        return world;
    }

    public Location getBase() {
        return base;
    }

    private void setupWorld() {
        world.setSpawnFlags(true, true);
        world.setPVP(true);
        world.setTime(0L);
        world.setSpawnLocation(base.getBlockX(), base.getBlockY(), base.getBlockZ());
        for (Entity entity : world.getEntities()) {
            if ((entity instanceof Monster) || (entity instanceof Projectile)) {
                entity.remove();
            }
        }
    }

    private void setupBase() {
        base.add(new Location(world, 0, -1, 0)).getBlock().setType(Material.OBSIDIAN);
    }

    private void setupPlayers() {
        for (Player p : world.getPlayers()) {
            p.leaveVehicle();
            p.teleport(base.add(0, 1, 0));
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setTotalExperience(0);
            equip(p);
        }
    }

    public void registerMonster(Monster m) {
        horde.add(m);
    }

    public void unregisterMonster(Monster m) {
        horde.remove(m);
    }

    public void sendMessageToAll(String m) {
        for (Player p : world.getPlayers()) {
            p.sendMessage(m);
        }
    }

    public void sendWelcomeMessage() {
        sendMessageToAll(ChatColor.DARK_GREEN + "Z O M B I E  " + ChatColor.DARK_AQUA + "S I E G E  " + ChatColor.DARK_BLUE + "1.0");
        sendMessageToAll("Survive five nights of zombie mayhem!");
        sendMessageToAll("You each get one (maybe not so useful) tool to start with!");
        sendMessageToAll("You have until nightfall to prepare yourselves.");
    }

    public void sendDayMessage() {
        switch (dayNum) {
        case 0:
            sendMessageToAll("Dawn of day 0:");
            sendMessageToAll("Start a mine, build a wall, farm for food.");
            break;
        case 1:
            sendMessageToAll("Dawn of day 1:");
            sendMessageToAll("");
            break;
        case 2:
            sendMessageToAll("Dawn of day 2:");
            break;
        case 3:
            sendMessageToAll("Dawn of day 3:");
            break;
        case 4:
            sendMessageToAll("Dawn of last day:");
            break;
        }
    }

    public void sendNightMessage() {
        switch (dayNum) {
        case 0:
            sendMessageToAll("Dusk of day 0:");
            sendMessageToAll("There are reports of an unusually high number of zombies about.");
            break;
        case 1:
            sendMessageToAll("Dusk of day 1:");
            sendMessageToAll("The zombies are becoming more aggressive!");
            break;
        case 2:
            sendMessageToAll("Dusk of day 2:");
            sendMessageToAll("Run for cover! The zombies seem to have brought weapons of their own!");
            break;
        case 3:
            sendMessageToAll("Dusk of day 3:");
            sendMessageToAll("Something in the water seems to be making the zombies more volatile...");
            break;
        case 4:
            sendMessageToAll("Dusk of last day:");
            sendMessageToAll("Be very, very afraid.");
            break;
        }
    }

    public void sendStats(CommandSender s) {
        s.sendMessage(ChatColor.LIGHT_PURPLE + padRight("Statistics", 15) + padRight("Kills", 10) + "Deaths");
        List<Player> l = world.getPlayers();
        Collections.sort(l, playerComparator);
        for (Player p : l) {
            String name = p.getName();
            s.sendMessage(padRight(name, 15) + padRight(playerKills.get(name).toString(), 10) + playerDeaths.get(name));
        }
    }

    public void enableZombieBehavior() {
        switch (dayNum) {
        case 0:
            break;
        case 1:
            zombieBlockBreak = true;
            break;
        case 2:
            zombieFireArrow = true;
            break;
        case 3:
            zombieExplosion = true;
            break;
        case 4:
            zombieGiant = true;
            break;
        }
    }

    public void addKill(Player p) {
        String name = p.getName();
        if (playerKills.containsKey(name)) {
            playerKills.put(name, playerKills.get(name) + 1);
        } else {
            playerKills.put(name, 1);
        }
    }

    public void addDeath(Player p) {
        String name = p.getName();
        if (playerDeaths.containsKey(name)) {
            playerDeaths.put(name, playerDeaths.get(name) + 1);
        } else {
            playerDeaths.put(name, 1);
        }
    }

    private void equip(Player p) {
        Inventory i = p.getInventory();
        i.clear();
        p.setItemInHand(new ItemStack(EQUIPABLES[r.nextInt(EQUIPABLES.length)], 1));
    }

    private static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);  
    }

    private static String padLeft(String s, int n) {
        return String.format("%1$#" + n + "s", s);  
    }
    
    public final class PlayerComparator implements Comparator<Player> {

        @Override
        public int compare(Player p1, Player p2) {
            int p1kills;
            int p2kills;
            if (playerKills.containsKey(p1)) {
                p1kills = playerKills.get(p1);
            } else {
                p1kills = 0;
                playerKills.put(p1.getName(), 0);
            }
            if (playerKills.containsKey(p2)) {
                p2kills = playerKills.get(p2);
            } else {
                p2kills = 0;
                playerKills.put(p2.getName(), 0);
            }
            return p1kills - p2kills;
        }
    }
}
