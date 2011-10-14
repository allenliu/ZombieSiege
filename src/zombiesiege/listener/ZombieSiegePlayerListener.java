package zombiesiege.listener;

import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerListener;

import zombiesiege.ZombieSiege;
import zombiesiege.ZombieSiegeGame;

public class ZombieSiegePlayerListener extends PlayerListener {
    
    private final ZombieSiege instance;
    
    public ZombieSiegePlayerListener(ZombieSiege instance) {
        this.instance = instance;
    }

    public void onPlayerBedEnter(PlayerBedEnterEvent e) {
        ZombieSiegeGame game = instance.getGame();
        if (game == null) {
            return;
        }
        e.setCancelled(true);
        e.getPlayer().sendMessage("No time to sleep! There are zombies to kill!");
    }
}
