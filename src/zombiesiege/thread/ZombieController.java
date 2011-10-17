package zombiesiege.thread;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

import zombiesiege.ZombieSiegeGame;

public class ZombieController extends Thread {

    private static final int FORGET_DISTANCE = 15;
    private static final int FIREBALL_DISTANCE = 30;
    private static final int BLOCKBREAK_DISTANCE = 30;
    private static final int ARROW_DISTANCE = 15;

    private final ZombieSiegeGame game;
    private int tick = 0;

    public ZombieController(ZombieSiegeGame game) {
        this.game = game;
    }

    @Override
    public void run() {
        tick++;
        for (Monster m : game.horde) {
            LivingEntity t = m.getTarget();
            if (t == null) {
                Vector v = game.getBase().toVector().subtract(m.getLocation().toVector());
                m.setVelocity(v.normalize().multiply(0.4));
            } else if (t.getLocation().distance(m.getLocation()) > FORGET_DISTANCE) {
                Vector v = t.getLocation().toVector().subtract(m.getLocation().toVector());
                m.setVelocity(v.normalize().multiply(0.4));
            }
            if (m instanceof Giant) {
                if (t != null && t.getLocation().distance(m.getLocation()) < FIREBALL_DISTANCE) {
                    if ((m.hashCode() + tick) % 5 == 0) {
                        Location from = m.getEyeLocation().add(0, 8, 0);
                        from = getOffset(from, t.getLocation());
                        from = lookAt(from, t.getLocation());
                        m.getWorld().spawn(from, Fireball.class);
                    }
                }
            }
            if (m instanceof Zombie) {
                if (game.zombieBlockBreak && ((t != null && t.getLocation().distance(m.getLocation()) < BLOCKBREAK_DISTANCE) || m.getLocation().distance(game.getBase()) < BLOCKBREAK_DISTANCE)) {
                    if ((m.hashCode() + tick) % 25 == 0) {
                        List<Block> l = m.getLineOfSight(null, 3);
                        for (Block b : l) {
                            if (b.getType() != Material.OBSIDIAN && b.getType() != Material.BRICK) {
                                b.setType(Material.AIR);
                            }
                        }
                    }
                }
                if (game.zombieFireArrow && t != null && t.getLocation().distance(m.getLocation()) < ARROW_DISTANCE) {
                    if (((m.hashCode() % 3 == 0) && (m.hashCode() + tick) % 2 == 0)) {
                        m.shootArrow();
                    }
                }
            }
        }
    }

    public Location getOffset(Location loc, Location lookat) {
        Vector v = lookat.toVector().subtract(loc.toVector()).normalize().multiply(6);
        loc.setX(loc.getX() + v.getX());
        loc.setY(loc.getY() + v.getY());
        loc.setZ(loc.getZ() + v.getZ());
        return loc;
    }

    // bergerkiller of Bukkit forums
    // http://forums.bukkit.org/threads/summoning-a-fireball.40724/
    public Location lookAt(Location loc, Location lookat) {   
        //Clone the loc to prevent applied changes to the input loc
        loc = loc.clone();

        // Values of change in distance (make it relative)
        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();

        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));        
            } else {
                loc.setYaw((float) (0.5 * Math.PI));    
            }
            loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }

        // Get the distance from dx/dz
        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        // Set pitch
        loc.setPitch((float) -Math.atan(dy / dxz));

        // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

        return loc;
    }
}
