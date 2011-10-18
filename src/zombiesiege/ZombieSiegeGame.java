package zombiesiege;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import zombiesiege.thread.LightningSpawner;
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
    public Set<Monster> horde = new CopyOnWriteArraySet<Monster>();

    public boolean isDay = true;
    public int dayNum = 0;
    public boolean firstMessage = false;
    private Material originalMaterial;

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
        instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new LightningSpawner(this), 0, 200L);

        sendWelcomeMessage();
    }

    public void endGame() {
        sendMessageToAll("The current ZombieSiege session has been ended.");
        instance.getServer().getScheduler().cancelTasks(instance);
        for (Entity entity : world.getEntities()) {
            if ((entity instanceof Monster) || (entity instanceof Projectile)) {
                entity.remove();
            }
        }
        resetBase();
    }

    public void winGame() {
        sendMessageToAll(ChatColor.GREEN + "V I C T O R Y !");
        for (Player p : world.getPlayers()) {
            sendStats(p);
        }
        sendMessageToAll("Continue playing for infinite mode.");
    }
    
    public void loseGame() {
        sendMessageToAll(ChatColor.DARK_RED + "D E F E A T !");
        sendMessageToAll("A zombie reached the spawn point!");
        for (Player p : world.getPlayers()) {
            sendStats(p);
            world.strikeLightning(p.getLocation());
            p.damage(1000);
        }
        instance.getServer().getScheduler().cancelTasks(instance);
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
        world.setDifficulty(Difficulty.EASY);
        world.setTime(0L);
        world.setStorm(false);
        world.setSpawnLocation(base.getBlockX(), base.getBlockY(), base.getBlockZ());
        for (Entity entity : world.getEntities()) {
            if ((entity instanceof Monster) || (entity instanceof Projectile)) {
                entity.remove();
            }
        }
    }

    private void setupBase() {
        originalMaterial = base.add(new Location(world, 0, -1, 0)).getBlock().getType();
        base.getBlock().setType(Material.BEDROCK);
    }

    private void resetBase() {
        base.add(new Location(world, 0, -1, 0)).getBlock().setType(originalMaterial);
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
        sendMessageToAll("Protect the spawn point from zombies.");
        sendMessageToAll("You each get one tool and piece of armor to start with!");
    }

    public void sendDayMessage() {
        switch (dayNum) {
        case 0:
            sendMessageToAll(ChatColor.AQUA + "Dawn of First Day");
            sendMessageToAll("You have been supplied with a slice of watermelon and an egg. Zombies begin spawning at nightfall.");
            break;
        case 1:
            sendMessageToAll(ChatColor.AQUA + "Dawn of Second Day");
            sendMessageToAll("You have been supplied with a golden apple and spider webs.");
            break;
        case 2:
            sendMessageToAll(ChatColor.AQUA + "Dawn of Third Day");
            sendMessageToAll("You have been supplied with cake and brick blocks.");
            break;
        case 3:
            sendMessageToAll(ChatColor.AQUA + "Dawn of Fourth Day");
            sendMessageToAll("You have been supplied with bows and arrows.");
            break;
        case 4:
            sendMessageToAll(ChatColor.AQUA + "Dawn of Last Day");
            sendMessageToAll("You have been supplied with TNT and more arrows.");
            break;
        case 5:
            winGame();          
            break;
        }
    }

    public void sendNightMessage() {
        switch (dayNum) {
        case 0:
            sendMessageToAll(ChatColor.DARK_PURPLE + "Dusk of First Day");
            sendMessageToAll("There are reports of an unusually high number of zombies about.");
            break;
        case 1:
            sendMessageToAll(ChatColor.DARK_PURPLE + "Dusk of Second Day");
            sendMessageToAll("The zombies are becoming more aggressive!");
            break;
        case 2:
            sendMessageToAll(ChatColor.DARK_PURPLE + "Dusk of Third Day");
            sendMessageToAll("Run for cover! The zombies seem to have brought weapons of their own!");
            break;
        case 3:
            sendMessageToAll(ChatColor.DARK_PURPLE + "Dusk of Fourth Day");
            sendMessageToAll("Something in the water seems to be making the zombies more volatile...");
            break;
        case 4:
            sendMessageToAll(ChatColor.DARK_PURPLE + "Dusk of Last Day");
            sendMessageToAll("Be very, very afraid.");
            break;
        }
    }

    public void sendStats(CommandSender s) {
        s.sendMessage(ChatColor.LIGHT_PURPLE + padRight("Player", 15) + padRight("Kills", 10) + "Deaths");
        List<Player> l = world.getPlayers();
        Collections.sort(l, playerComparator);
        for (Player p : l) {
            String name = p.getName();
            int kills, deaths;
            if (playerKills.containsKey(name)) {
                kills = playerKills.get(name);
            } else {
                kills = 0;
                playerKills.put(name, 0);
            }
            if (playerDeaths.containsKey(name)) {
                deaths = playerDeaths.get(name);
            } else {
                deaths = 0;
                playerDeaths.put(name, 0);
            }
            s.sendMessage(padRight(name, 15) + padRight("" + kills, 10) + deaths);
        }
        s.sendMessage(ChatColor.LIGHT_PURPLE + "========================");
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

    public void disperseEquipment() {
        switch (dayNum) {
        case 0:
            world.dropItem(base, new ItemStack(Material.MELON, 1));
            world.dropItem(base, new ItemStack(Material.EGG, 1));
            break;
        case 1:
            world.dropItem(base, new ItemStack(Material.GOLDEN_APPLE, 1));
            world.dropItem(base, new ItemStack(Material.WEB, 5));
            break;
        case 2:
            world.dropItem(base, new ItemStack(Material.BRICK, 10));
            world.dropItem(base, new ItemStack(Material.CAKE, 1));
            break;
        case 3:
            world.dropItem(base, new ItemStack(Material.BOW, 2));
            world.dropItem(base, new ItemStack(Material.ARROW, 64));
            break;
        case 4:
            world.dropItem(base, new ItemStack(Material.TNT, 5));
            world.dropItem(base, new ItemStack(Material.ARROW, 64));
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
        PlayerInventory i = p.getInventory();
        i.clear();
        i.setHelmet(null);
        i.setChestplate(null);
        i.setLeggings(null);
        i.setBoots(null);
        
        p.setItemInHand(new ItemStack(EQUIPABLES[r.nextInt(EQUIPABLES.length)], 1));
        switch (r.nextInt(4)) {
        case 0:
            p.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET, 1));
            break;
        case 1:
            p.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
            break;
        case 2:
            p.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
            break;
        case 3:
            p.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS, 1));
            break;
        }
    }

    private static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);  
    }
    
    public final class PlayerComparator implements Comparator<Player> {

        @Override
        public int compare(Player p1, Player p2) {
            int p1kills, p2kills, p1deaths, p2deaths;
            if (playerKills.containsKey(p1.getName())) {
                p1kills = playerKills.get(p1.getName());
            } else {
                p1kills = 0;
                playerKills.put(p1.getName(), 0);
            }
            if (playerKills.containsKey(p2.getName())) {
                p2kills = playerKills.get(p2.getName());
            } else {
                p2kills = 0;
                playerKills.put(p2.getName(), 0);
            }
            if (playerDeaths.containsKey(p1.getName())) {
                p1deaths = playerDeaths.get(p1.getName());
            } else {
                p1deaths = 0;
                playerDeaths.put(p1.getName(), 0);
            }
            if (playerDeaths.containsKey(p2.getName())) {
                p2deaths = playerDeaths.get(p2.getName());
            } else {
                p2deaths = 0;
                playerDeaths.put(p2.getName(), 0);
            }
            double p1ratio, p2ratio;
            if (p1kills == 0) {
                p1ratio = 0;
            } else if (p1deaths == 0) {
                p1ratio = 1;
            } else {
                p1ratio = (double) p1kills / p1deaths;
            }
            if (p2kills == 0) {
                p2ratio = 0;
            } else if (p2deaths == 0) {
                p2ratio = 1;
            } else {
                p2ratio = (double) p2kills / p2deaths;
            }       
            return Double.compare(p1ratio, p2ratio);
        }
    }
}
