package xyz.prosurestring.prosurestring;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;

public final class ServerTimer extends JavaPlugin {

    private BossBar timerBar;
    private BukkitTask timerTask;
    private int initialBorderSize = 20; // Starting border size

    @Override
    public void onEnable() {
        // Register command
        getCommand("startserver").setExecutor(new StartServerCommand());

        // Save default config if it doesn't exist
        saveDefaultConfig();

        reloadConfig();

        getLogger().info("ServerTimer has been enabled!");
    }

    @Override
    public void onDisable() {
        // Clean up
        if (timerBar != null) {
            timerBar.removeAll();
        }

        if (timerTask != null && !timerTask.isCancelled()) {
            timerTask.cancel();
        }

        getLogger().info("ServerTimer has been disabled!");
    }

    private class StartServerCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /startserver <BorderSize> <StartDuration> <GraceDuration>");
                return false;
            }

            try {
                int borderSize = Integer.parseInt(args[0]);
                int startDuration = Integer.parseInt(args[1]);
                int graceDuration = Integer.parseInt(args[2]);

                startServerSequence(sender, borderSize, startDuration, graceDuration);
                return true;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.DARK_RED + "Please provide valid numbers for all parameters.");
                sender.sendMessage(ChatColor.RED + "Usage: /StartServer <borderSize: int> <startDuration: int> <graceDuration: int>");
                return false;
            }
        }
    }

    private void startServerSequence(CommandSender sender, int borderSize, int startDuration, int graceDuration) {
        Bukkit.getWorlds().forEach(world -> world.setPVP(false));

        Bukkit.getWorlds().forEach(world -> {
            WorldBorder border = world.getWorldBorder();
            border.setSize(initialBorderSize);
            border.setCenter(world.getSpawnLocation());
        });

        Bukkit.broadcastMessage(ChatColor.GREEN + "Server start sequence initiated!");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "PvP is disabled. World border set to " + initialBorderSize + " blocks.");
        Bukkit.broadcastMessage(ChatColor.GOLD + "The game will start in " + startDuration + " minutes.");

        startServerTimer(startDuration, borderSize, graceDuration);
    }

    private void startServerTimer(int startDuration, int borderSize, int graceDuration) {
        if (timerBar != null) {
            timerBar.removeAll();
        }

        if (timerTask != null && !timerTask.isCancelled()) {
            timerTask.cancel();
        }

        timerBar = Bukkit.createBossBar(
                "Server Starts in " + formatTime(startDuration * 60),
                BarColor.GREEN,
                BarStyle.SOLID
        );

        Bukkit.getOnlinePlayers().forEach(timerBar::addPlayer);

        AtomicInteger secondsLeft = new AtomicInteger(startDuration * 60);

        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                int current = secondsLeft.decrementAndGet();

                if (current <= 0) {
                    this.cancel();
                    timerBar.removeAll();

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        player.sendTitle(ChatColor.GOLD + "GO!!!", ChatColor.GREEN + "The game has started!", 10, 70, 20);
                    });

                    expandWorldBorder(borderSize, graceDuration);
                    return;
                }

                double progress = (double) current / (startDuration * 60);
                timerBar.setProgress(Math.max(0, Math.min(1, progress)));
                timerBar.setTitle("Server Starts in " + formatTime(current));

                if (current <= 15) {
                    timerBar.setColor(BarColor.RED);

                    // countdown for last 3 seconds UvU
                    if (current == 3) {
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                            player.sendTitle(ChatColor.GREEN + "3", "", 10, 20, 0);
                        });
                    } else if (current == 2) {
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                            player.sendTitle(ChatColor.YELLOW + "2", "", 10, 20, 0);
                        });
                    } else if (current == 1) {
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                            player.sendTitle(ChatColor.RED + "1", "", 10, 20, 0);
                        });
                    }
                } else if (current <= 60) {
                    timerBar.setColor(BarColor.YELLOW);
                }
            }
        }.runTaskTimer(this, 20L, 20L); // Run every second (20 ticks is 1s :)
    }

    private void expandWorldBorder(int targetSize, int graceDuration) {
        // expanding border to target size
        Bukkit.getWorlds().forEach(world -> {
            WorldBorder border = world.getWorldBorder();
            border.setSize(targetSize, (targetSize / 200)); // Expand over x seconds wher x is the uhhhh like size and it's divided by 200 so it moves by 200 bps
        });

        // wAit 5 seconds before starting grace period because thats how long the titl e takes to go away
        new BukkitRunnable() {
            @Override
            public void run() {
                startGracePeriod(graceDuration);
            }
        }.runTaskLater(this, 5 * 20); // 5 seconds * 20 ticks = SecondsInTicks which we need uwu
    }

    private void startGracePeriod(int graceDuration) {
        if (timerBar != null) {
            timerBar.removeAll();
        }

        timerBar = Bukkit.createBossBar(
                "Grace Period ends in " + formatTime(graceDuration * 60),
                BarColor.PINK,
                BarStyle.SOLID
        );

        Bukkit.getOnlinePlayers().forEach(timerBar::addPlayer);

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(timerBar), this);

        AtomicInteger secondsLeft = new AtomicInteger(graceDuration * 60);

        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                int current = secondsLeft.decrementAndGet();

                if (current <= 0) {
                    // Grace period over haha losers gonna get stabbed /j
                    this.cancel();
                    timerBar.removeAll();

                    Bukkit.getWorlds().forEach(world -> world.setPVP(true));

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.playSound(player.getLocation(), Sound.valueOf("ambient.weather.thunder1"), 1, 2);
                        player.sendTitle(ChatColor.RED + "Grace period has ended!",
                                ChatColor.YELLOW + "PvP is now enabled!", 10, 70, 20);
                    });

                    Bukkit.broadcastMessage(ChatColor.RED + "Grace period has ended! PvP is now enabled!");
                    return;
                }

                double progress = (double) current / (graceDuration * 60);
                timerBar.setProgress(Math.max(0, Math.min(1, progress)));
                timerBar.setTitle("Grace Period ends in " + formatTime(current));

                // Change color in last minute uwu
                if (current <= 60) {
                    timerBar.setColor(BarColor.RED);
                }
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    // helper method to format time as MM:SS bc im too lazy to like make a oneliner uwu
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}