package org.core.coreProgram.Cores.Nox.Passive;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.core.Cool.Cool;
import org.core.coreConfig;
import org.core.coreProgram.Cores.Nox.coreSystem.Nox;

import java.util.*;

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

        updateList(player, skill, 0);

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

        new BukkitRunnable() {
            @Override
            public void run() {
                long remainingTime = cooldownEndTime - System.currentTimeMillis();
                if (remainingTime <= 0) {
                    bossBar.setProgress(1.0);
                    bossBar.removePlayer(player);
                    updateList(player, skill, 1);
                    cancel();
                } else {
                    Map<String, Double> skillMap = config.dreamPoint.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());

                    double elapsed = finalDuration - remainingTime;

                    skillMap.putIfAbsent(skill, 1.0);
                    skillMap.put(skill, elapsed / 1000);

                    double progress = elapsed / finalDuration;
                    bossBar.setProgress(Math.min(1.0, Math.max(0.0, progress)));
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void updateList(Player player, String skillType, int updateType){

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective old = scoreboard.getObjective("NOX");
        if (old != null) old.unregister();

        Objective objective = scoreboard.registerNewObjective("NOX", Criteria.DUMMY, Component.text("NOX"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score1 = objective.getScore("------------");
        score1.setScore(6);

        int scoreNumber = switch (skillType) {
            case "R" -> 3;
            case "Q" -> 2;
            default -> 1;
        };

        if(updateType == 1) {
            Score score = objective.getScore("§5" + skillType);
            score.setScore(scoreNumber);
        }else{
            Score score = objective.getScore("§7" + skillType);
            score.setScore(scoreNumber);
        }

        player.setScoreboard(scoreboard);
    }
}
