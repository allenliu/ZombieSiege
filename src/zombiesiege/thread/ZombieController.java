package zombiesiege.thread;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.util.Vector;

import zombiesiege.ZombieSiegeGame;

public class ZombieController extends Thread {
    
    private static final int FORGET_DISTANCE = 15;
    
    private final ZombieSiegeGame game;

    public ZombieController(ZombieSiegeGame game) {
        this.game = game;
    }
    
    @Override
    public void run() {
        for (Monster m : game.horde) {
            LivingEntity t = m.getTarget();
            if (t == null) {
                Vector v = game.getBase().toVector().subtract(m.getLocation().toVector());
                m.setVelocity(v.normalize().multiply(0.3));
                continue;
            }
            if (t.getLocation().distance(m.getLocation()) > FORGET_DISTANCE) {
                Vector v = t.getLocation().toVector().subtract(m.getLocation().toVector());
                m.setVelocity(v.normalize().multiply(0.3));
                continue;
            }
        }
    }
}
