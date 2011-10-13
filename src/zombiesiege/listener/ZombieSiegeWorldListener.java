package zombiesiege.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

import zombiesiege.ZombieSiege;
import zombiesiege.ZombieSiegeGame;

public class ZombieSiegeWorldListener extends WorldListener {
    
    private final ZombieSiege instance;
    
    public ZombieSiegeWorldListener(ZombieSiege instance) {
        this.instance = instance;
    }
    
    public void onChunkUnload(ChunkUnloadEvent e) {
        ZombieSiegeGame game = instance.getGame();
        if (game == null) {
            return;
        }
        Entity[] entities = e.getChunk().getEntities();
        for (int i = 0; i < entities.length; i++) {
            if (entities[i] instanceof Monster) {
                Monster m = (Monster) entities[i];
                game.unregisterMonster(m);
            }
        }
    }

}
