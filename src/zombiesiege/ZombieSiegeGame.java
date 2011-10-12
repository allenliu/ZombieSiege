package zombiesiege;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import zombiesiege.thread.ZombieController;
import zombiesiege.thread.ZombieSpawner;

public class ZombieSiegeGame {

    private final ZombieSiege instance;
    private final World world;
    private final Location base;
    public List<Monster> horde = new ArrayList<Monster>();
    
    private final Random r = new Random();
    
    public ZombieSiegeGame(ZombieSiege instance, Player p) {
        this.instance = instance;
        world = p.getWorld();
        base = p.getLocation();
        createBase();
        
        world.setSpawnFlags(true, true);
        world.setPVP(true);
        instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new ZombieSpawner(this), 0, 400L);
        instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new ZombieController(this), 0, 20L);
        
        /*for (Player p1 : world.getPlayers()) {
            world.spawnCreature(p1.getLocation(), CreatureType.GIANT);
            for (LivingEntity e : world.getLivingEntities()) {
                if (e instanceof Monster) {
                    Monster m = (Monster) e;
                    m.setTarget(p);
                }
            }
        }*/
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
    
    private void createBase() {
        world.setSpawnLocation(base.getBlockX(), base.getBlockY(), base.getBlockZ());
        base.add(new Location(world, 0, -1, 0)).getBlock().setType(Material.OBSIDIAN);
    }
    
    public void registerMonster(Monster m) {
        List<Player> l = world.getPlayers();
        if (!l.isEmpty()) {
            Player p = l.get(r.nextInt(l.size()));
            //m.setTarget(p);
        }
        horde.add(m);
    }
    
    public void unregisterMonster(Monster m) {
        horde.remove(m);
    }
}
