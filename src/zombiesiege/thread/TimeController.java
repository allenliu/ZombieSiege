package zombiesiege.thread;

import zombiesiege.ZombieSiegeGame;

public class TimeController extends Thread {
    
    private final ZombieSiegeGame game;

    public TimeController(ZombieSiegeGame game) {
        this.game = game;
    }
    
    @Override
    public void run() {
        Long time = game.getWorld().getTime();
        if (game.isDay && time > 13000L) {
            game.isDay = false;
            game.sendNightMessage();
            return;
        }
        if (!game.isDay && time < 13000L) {
            game.isDay = true;
            game.dayNum++;
            game.sendDayMessage();
            return;
        }
    }

}
