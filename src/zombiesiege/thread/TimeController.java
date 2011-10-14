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
        if (!game.firstMessage && 100 < time && time < 13000L) {
            game.firstMessage = true;
            game.sendDayMessage();
            return;
        }
        if (game.isDay && 13000L < time) {
            game.isDay = false;
            game.enableZombieBehavior();
            game.sendNightMessage();
            return;
        }
        if (!game.isDay && 100 < time && time < 13000L) {
            game.isDay = true;
            game.dayNum++;
            game.sendDayMessage();
            return;
        }
    }

}
