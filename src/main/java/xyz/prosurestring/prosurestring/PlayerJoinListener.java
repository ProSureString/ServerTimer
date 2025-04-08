package xyz.prosurestring.prosurestring;

import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final BossBar bossBar;

    public PlayerJoinListener(BossBar bossBar) {
        this.bossBar = bossBar;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // for adding newly joined player to the boss bar so if som1 rejoins / joins after timer starts, they see it too!
        if (bossBar != null) {
            bossBar.addPlayer(event.getPlayer());
        }
    }
}