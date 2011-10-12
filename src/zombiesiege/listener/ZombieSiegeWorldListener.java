package zombiesiege.listener;

import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

import zombiesiege.ZombieSiege;

public class ZombieSiegeWorldListener extends WorldListener {
    
    private final ZombieSiege instance;
    
    public ZombieSiegeWorldListener(ZombieSiege instance) {
        this.instance = instance;
    }
    
    public void onChunkUnload(ChunkUnloadEvent e) {
        
    }

}
