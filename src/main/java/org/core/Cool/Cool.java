package org.core.Cool;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class Cool {

    private final JavaPlugin plugin;

    public Cool(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private static class CooldownData {
        long endTime;
        BossBar bossBar;
        BukkitRunnable cooldownTask;
        boolean isOnCooldown;

        CooldownData(long endTime, BossBar bossBar, BukkitRunnable cooldownTask, boolean isOnCooldown) {
            this.endTime = endTime;
            this.bossBar = bossBar;
            this.cooldownTask = cooldownTask;
            this.isOnCooldown = isOnCooldown;
        }
    }

    private final HashMap<UUID, HashMap<String, CooldownData>> cooldowns = new HashMap<>();

    public boolean isReloading(Player player, String skill) {
        UUID playerId = player.getUniqueId();
        if (cooldowns.containsKey(playerId) && cooldowns.get(playerId).containsKey(skill)) {
            CooldownData cooldownData = cooldowns.get(playerId).get(skill);
            return cooldownData.isOnCooldown;
        }
        return false;
    }

    public void setCooldown(Player player, long duration, String skill) {
        UUID playerId = player.getUniqueId();
        cooldowns.putIfAbsent(playerId, new HashMap<>());

        long cooldownEndTime = System.currentTimeMillis() + duration;
        CooldownData cooldownData = cooldowns.get(playerId).get(skill);

        if (cooldownData == null || !cooldownData.isOnCooldown) {
            BossBar bossBar;
            if (cooldownData == null || cooldownData.bossBar == null) {
                bossBar = Bukkit.createBossBar(skill + " Cooldown", BarColor.WHITE, BarStyle.SOLID);
                bossBar.addPlayer(player);
            } else {
                bossBar = cooldownData.bossBar;
                bossBar.setProgress(1.0);
            }

            BukkitRunnable cooldownTask = new BukkitRunnable() {
                @Override
                public void run() {
                    long remainingTime = cooldownEndTime - System.currentTimeMillis();
                    if (remainingTime <= 0) {
                        bossBar.setProgress(0);
                        bossBar.removePlayer(player);
                        cooldowns.get(playerId).remove(skill);
                        cancel();
                    } else {
                        double progress = (double) remainingTime / duration;
                        bossBar.setProgress(Math.max(0, progress));
                    }
                }
            };

            cooldowns.get(playerId).put(skill, new CooldownData(cooldownEndTime, bossBar, cooldownTask, true));
            cooldownTask.runTaskTimer(plugin, 0L, 1L);
        }
    }

    public void updateCooldown(Player player, String skill, long newDuration) {
        UUID playerId = player.getUniqueId();
        cooldowns.putIfAbsent(playerId, new HashMap<>());

        HashMap<String, CooldownData> playerCooldowns = cooldowns.get(playerId);
        CooldownData cooldownData = playerCooldowns.get(skill);

        long cooldownEndTime = System.currentTimeMillis() + newDuration;

        if (cooldownData == null) {
            BossBar bossBar = Bukkit.createBossBar(skill + " Cooldown", BarColor.WHITE, BarStyle.SOLID);
            bossBar.addPlayer(player);

            BukkitRunnable cooldownTask = new BukkitRunnable() {
                @Override
                public void run() {
                    long remainingTime = cooldownEndTime - System.currentTimeMillis();
                    if (remainingTime <= 0) {
                        bossBar.setProgress(0);
                        bossBar.removePlayer(player);
                        playerCooldowns.remove(skill);
                        cancel();
                    } else {
                        double progress = (double) remainingTime / newDuration;
                        bossBar.setProgress(Math.max(0, progress));
                    }
                }
            };

            cooldownTask.runTaskTimer(plugin, 0L, 1L);
            playerCooldowns.put(skill, new CooldownData(cooldownEndTime, bossBar, cooldownTask, true));

        } else {
            cooldownData.endTime = cooldownEndTime;

            if (cooldownData.bossBar != null) {
                cooldownData.bossBar.setProgress(1.0);
            }

            if (!cooldownData.cooldownTask.isCancelled()) {
                cooldownData.cooldownTask.cancel();
            }

            cooldownData.cooldownTask = new BukkitRunnable() {
                @Override
                public void run() {
                    long remainingTime = cooldownEndTime - System.currentTimeMillis();
                    if (remainingTime <= 0) {
                        cooldownData.bossBar.setProgress(0);
                        cooldownData.bossBar.removePlayer(player);
                        playerCooldowns.remove(skill);
                        cancel();
                    } else {
                        double progress = (double) remainingTime / newDuration;
                        cooldownData.bossBar.setProgress(Math.max(0, progress));
                    }
                }
            };

            cooldownData.cooldownTask.runTaskTimer(plugin, 0L, 1L);
        }
    }
}
