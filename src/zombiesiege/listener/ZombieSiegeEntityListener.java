package zombiesiege.listener;

import java.util.Random;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Monster;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

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
    }

    public void onEntityTarget(EntityTargetEvent e) {
        ZombieSiegeGame game = instance.getGame();
        if (game == null) {
            return;
        }
        System.out.println(e.getReason());
        if (e.getReason() == TargetReason.FORGOT_TARGET) {
            System.out.println("don't forget toarge");
            e.setCancelled(true);
        }
    }
}
