package zombiesiege.thread;

import java.util.Random;

import org.bukkit.Location;

import zombiesiege.ZombieSiegeGame;

public class LightningSpawner extends Thread {

    private static final int SPAWN_RADIUS = 40;

    private final ZombieSiegeGame game;
    private final Random r = new Random();

    public LightningSpawner(ZombieSiegeGame game) {
        this.game = game;
    }

    @Override
    public void run() {
        if (game.getWorld().getTime() < 13000L) {
            return;
        }
        Location spawnCenter = game.getBase();
        int theta = r.nextInt(360);
        double x = spawnCenter.getX() + SPAWN_RADIUS * Math.cos(Math.toRadians(theta));
        double z = spawnCenter.getZ() + SPAWN_RADIUS * Math.sin(Math.toRadians(theta));
        double y = game.getWorld().getHighestBlockYAt((int) x, (int) z);
        Location spawn = new Location(game.getWorld(), x, y, z);
        game.getWorld().strikeLightning(spawn);
    }
}
