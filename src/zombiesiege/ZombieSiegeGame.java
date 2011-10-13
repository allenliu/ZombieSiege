package zombiesiege;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
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
    
    private final ZombieSiege instance;
    private final World world;
    private final Location base;
    public List<Monster> horde = new ArrayList<Monster>();
    
    public boolean isDay = true;
    public int dayNum = 0;
    
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
        instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new TimeController(this), 0, 300L);
        
        /*for (Player p1 : world.getPlayers()) {
            world.spawnCreature(p1.getLocation(), CreatureType.GIANT);
            for (LivingEntity e : world.getLivingEntities()) {
                if (e instanceof Monster) {
                    Monster m = (Monster) e;
                    m.setTarget(p);
                }
            }
        }*/
        sendDayMessage();
    }
    
    public void endGame() {
        instance.getServer().getScheduler().cancelAllTasks();
        for (Monster m : horde) {
            m.setHealth(0);
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
        for (LivingEntity entity : world.getLivingEntities()) {
            if ((entity instanceof Monster) && !(entity instanceof Zombie)) {
                entity.remove();
            }
        }
    }
    
    private void setupBase() {
        base.add(new Location(world, 0, -1, 0)).getBlock().setType(Material.OBSIDIAN);
    }
    
    private void setupPlayers() {
        for (Player p : world.getPlayers()) {
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
    
    public void sendDayMessage() {
        switch (dayNum) {
        case 0:
            sendMessageToAll(ChatColor.DARK_GREEN + "Z O M B I E  " + ChatColor.DARK_AQUA + "S I E G E   " + ChatColor.DARK_BLUE + "1.0");
            sendMessageToAll("Dawn of day 0:");
            sendMessageToAll("You have until nightfall to prepare yourselves.");
            break;
        case 1:
            sendMessageToAll("Dawn of day 1:");
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
        sendMessageToAll("Zombies will spawn now.");
    }
    
    private void equip(Player p) {
        Inventory i = p.getInventory();
        i.clear();
        p.setItemInHand(new ItemStack(EQUIPABLES[r.nextInt(EQUIPABLES.length)], 1));
    }
}
