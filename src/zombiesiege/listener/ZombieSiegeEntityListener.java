package zombiesiege.listener;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Giant;
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
import org.bukkit.inventory.ItemStack;

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
        if (e.getEntity() instanceof Player) {
            game.addDeath((Player) e.getEntity());
            return;
        }
        if (e.getEntity() instanceof Monster) {
            Monster m = (Monster) e.getEntity();
            List<ItemStack> drops = e.getDrops();
            drops.clear();
            
            if (m instanceof Zombie) {
                drops.add(new ItemStack(Material.BONE, 1));
            }
            if (m instanceof Giant) {
                drops.add(new ItemStack(Material.DIAMOND, 5));
            }
            
            EntityDamageEvent de = m.getLastDamageCause();
            if (de instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) de;
                if (ee.getDamager() instanceof Player) {
                    game.addKill((Player) ee.getDamager());
                }
            }
            game.unregisterMonster(m);
            return;
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
            if ((ee.getDamager() instanceof Projectile) && (ee.getEntity() instanceof Monster)) {
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
                            game.getWorld().createExplosion(z.getLocation(), 1.2F);
                        }
                    }
                }
            }
        }
    }
}
