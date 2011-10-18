package zombiesiege.listener;

import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockListener;

import zombiesiege.ZombieSiege;
import zombiesiege.ZombieSiegeGame;

public class ZombieSiegeBlockListener extends BlockListener {

private final ZombieSiege instance;
    
    public ZombieSiegeBlockListener(ZombieSiege instance) {
        this.instance = instance;
    }
    
    public void onBlockCanBuild(BlockCanBuildEvent e) {
        ZombieSiegeGame game = instance.getGame();
        if (game == null) {
            return;
        }
        int x = e.getBlock().getX();
        int y = e.getBlock().getY();
        int z = e.getBlock().getZ();
        int baseX = game.getBase().getBlock().getX();
        int baseY = game.getBase().getBlock().getY();
        int baseZ = game.getBase().getBlock().getZ();
        if ((x == baseX && y == baseY + 1 && z == baseZ) || (x == baseX && y == baseY && z == baseZ)) {
            e.setBuildable(false);
        }
    }
}
