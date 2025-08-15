package org.core.coreProgram.Cores.Nox.Passive;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.core.Cool.Cool;
import org.core.coreConfig;
import org.core.coreProgram.Cores.Nox.coreSystem.Nox;

public class Dream implements Listener {

    private final Nox config;
    private final JavaPlugin plugin;
    private final Cool cool;
    private final coreConfig tag;

    public Dream(Nox config, coreConfig tag, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
        this.tag = tag;
    }

    public void wanderersDream(Player player, String skill) {

        long duration = 0;

        BossBar bossBar = Bukkit.createBossBar(skill + " Cooldown", BarColor.PURPLE, BarStyle.SOLID);
        bossBar.addPlayer(player);

        if (skill.equals("R")) {
            duration = 3000;
        } else if (skill.equals("Q")) {
            duration = 6000;
        } else if (skill.equals("F")) {
            duration = 6000;
        }

        long cooldownEndTime = System.currentTimeMillis() + duration;
        long finalDuration = duration;

        BukkitRunnable cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                long remainingTime = cooldownEndTime - System.currentTimeMillis();
                if (remainingTime <= 0) {

                    bossBar.setProgress(1.0);
                    bossBar.removePlayer(player);

                    cancel();
                } else {
                    double elapsed = finalDuration - remainingTime;
                    double progress = elapsed / finalDuration;
                    bossBar.setProgress(Math.min(1.0, Math.max(0.0, progress)));

                }
            }
        };
        cooldownTask.runTaskTimer(plugin, 0L, 1L);
    }

    public void fullList(){

    }
}
