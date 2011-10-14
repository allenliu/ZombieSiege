package zombiesiege.listener;

import java.util.Random;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import zombiesiege.ZombieSiege;
import zombiesiege.ZombieSiegeGame;

public class ZombieSiegeEntityListener extends EntityListener {

    private final ZombieSiege instance;
    private final Random r = new Random();

    public ZombieSiegeEntityListener(ZombieSiege instance) {
        this.instance = instance;
    }

    public void onCreatureSpawn(CreatureSpawnEvent e) {
        ZombieSiegeGame game = instance.getGame();
        if (game == null) {
            return;
        }
        if (e.getEntity() instanceof Monster) {
            Monster m = (Monster) e.getEntity();
            if (e.getCreatureType() == CreatureType.GIANT) {
                game.registerMonster(m);
                return;
            }
            if (e.getCreatureType() == CreatureType.ZOMBIE) {
                if (e.getSpawnReason() == SpawnReason.NATURAL) {
                    game.registerMonster(m);
                    return;
                }
                if (e.getSpawnReason() == SpawnReason.CUSTOM) {
                    game.registerMonster(m);
                    return;
                }
            }
        }
        e.setCancelled(true);
    }

    public void onEntityDeath(EntityDeathEvent e) {
        ZombieSiegeGame game = instance.getGame();
        if (game == null) {
            return;
        }
        if (e.getEntity() instanceof Monster) {
            Monster m = (Monster) e.getEntity();
            game.unregisterMonster(m);
        }
    }

    public void onEntityTarget(EntityTargetEvent e) {
        ZombieSiegeGame game = instance.getGame();
        if (game == null) {
            return;
        }
        if (e.getTarget() instanceof Monster) {
            e.setCancelled(true);
        }
    }

    public void onEntityDamage(EntityDamageEvent e) {
        ZombieSiegeGame game = instance.getGame();
        if (game == null) {
            return;
        }
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
            if (ee.getDamager() instanceof Projectile) {
                Projectile p = (Projectile) ee.getDamager();
                if (p.getShooter() instanceof Monster) {
                    e.setCancelled(true);
                    return;
                }
            }
            if (game.zombieExplosion && e.getEntity() instanceof Zombie) {
                if (ee.getDamager() instanceof Player) {
                    if (ee.getCause() == DamageCause.ENTITY_ATTACK) {
                        if (r.nextFloat() < 0.05) {
                            Zombie z = (Zombie) e.getEntity();
                            z.damage(100);
                            game.getWorld().createExplosion(z.getLocation(), 0.8F);
                        }
                    }
                }
            }
        }
    }
}
