package zombiesiege.thread;

import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Zombie;
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
            } else if (t.getLocation().distance(m.getLocation()) > FORGET_DISTANCE) {
                Vector v = t.getLocation().toVector().subtract(m.getLocation().toVector());
                m.setVelocity(v.normalize().multiply(0.3));
                if (m instanceof Giant) {
                    Location from = m.getEyeLocation().add(0, 8, 0);
                    from = getOffset(from, t.getLocation());
                    from = lookAt(from, t.getLocation());
                    m.getWorld().spawn(from, Fireball.class);
                }
                continue;
            } else if (m instanceof Zombie) {
                m.shootArrow();
            } else if (m instanceof Giant) {
                Location from = m.getEyeLocation().add(0, 8, 0);
                from = getOffset(from, t.getLocation());
                from = lookAt(from, t.getLocation());
                m.getWorld().spawn(from, Fireball.class);
                //Location l = m.getEyeLocation().toVector().add(m.getLocation().getDirection().multiply(2)).toLocation(m.getWorld(), m.getLocation().getYaw(), m.getLocation().getPitch());
                //Fireball fireball = m.getWorld().spawn(l, Fireball.class);
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
