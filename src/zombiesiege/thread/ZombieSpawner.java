package zombiesiege.thread;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;

import zombiesiege.ZombieSiegeGame;

public class ZombieSpawner extends Thread {
    
    private static final int SPAWN_RADIUS = 30;
    
    private final ZombieSiegeGame game;
    private final Random r = new Random();
    
    public ZombieSpawner(ZombieSiegeGame game) {
        this.game = game;
    }
    
    @Override
    public void run() {
        Location spawnCenter = game.getBase();
        for (int i = 0; i < 10; i ++) {
            int theta = r.nextInt(360);
            double x = spawnCenter.getX() + SPAWN_RADIUS * Math.cos(Math.toRadians(theta));
            double z = spawnCenter.getZ() + SPAWN_RADIUS * Math.sin(Math.toRadians(theta));
            double y = game.getWorld().getHighestBlockYAt((int) x, (int) z);
            Location spawn = new Location(game.getWorld(), x, y, z);
            game.getWorld().spawnCreature(spawn, CreatureType.ZOMBIE);
        }
    }

}
