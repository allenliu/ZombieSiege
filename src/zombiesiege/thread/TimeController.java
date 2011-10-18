package zombiesiege.thread;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Projectile;

import zombiesiege.ZombieSiegeGame;

public class TimeController extends Thread {
    
    private final ZombieSiegeGame game;

    public TimeController(ZombieSiegeGame game) {
        this.game = game;
    }
    
    @Override
    public void run() {
        Long time = game.getWorld().getTime();
        if (!game.firstMessage && 100 < time && time < 13000L) {
            game.firstMessage = true;
            game.disperseEquipment();
            game.sendDayMessage();
            return;
        }
        if (game.isDay && 13000L < time) {
            game.isDay = false;
            for (Entity entity : game.getWorld().getEntities()) {
                if ((entity instanceof Monster) || (entity instanceof Projectile)) {
                    entity.remove();
                }
            }
            game.horde.clear();
            game.enableZombieBehavior();
            game.sendNightMessage();
            return;
        }
        if (!game.isDay && 100 < time && time < 13000L) {
            game.isDay = true;
            game.dayNum++;
            game.disperseEquipment();
            game.sendDayMessage();
            return;
        }
    }

}
