package org.core.coreProgram.Cores.Benjamin.Passive;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.core.Cool.Cool;
import org.core.coreConfig;
import org.core.coreProgram.Cores.Benjamin.coreSystem.Benjamin;

import java.util.*;

public class HardSlash implements Listener {

    private final Benjamin config;
    private final JavaPlugin plugin;
    private final Cool cool;
    private final coreConfig tag;

    public HardSlash(Benjamin config, coreConfig tag, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
        this.tag = tag;
    }

    public void slashReinforcer(Player player) {

        if (config.reinforcing.containsKey(player.getUniqueId()) || !tag.Benjamin.contains(player)) {
            return;
        }

        BukkitRunnable reinforce = new BukkitRunnable() {
            public void run() {

                if (!player.isOnline() || !tag.Benjamin.contains(player)) {
                    config.reinforcing.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                boolean r = cool.isReloading(player, "R");
                boolean q = cool.isReloading(player, "Q");
                boolean f = cool.isReloading(player, "F");

                double value;

                int reloadingCount = 0;
                if (r) reloadingCount++;
                if (q) reloadingCount++;
                if (f) reloadingCount++;

                if (reloadingCount == 3) {
                    value = 64.0;
                } else if (reloadingCount == 2) {
                    value = 16.0;
                } else if (reloadingCount == 1) {
                    value = 4.0;
                } else {
                    value = 1.0;
                }

                config.Ampli.put(player.getUniqueId(), value);

                if(!f && config.F_stack.getOrDefault(player.getUniqueId(), 0.0) < 6.0) {
                    config.F_stack.put(player.getUniqueId(), config.F_stack.getOrDefault(player.getUniqueId(), 1.0) + (1 * config.Ampli.getOrDefault(player.getUniqueId(), 1.0)) * 6 / 100);
                }

                if(!q && config.Q_stack.getOrDefault(player.getUniqueId(), 0.0) < 3.0) {
                    config.Q_stack.put(player.getUniqueId(), config.Q_stack.getOrDefault(player.getUniqueId(), 1.0) + (1 * config.Ampli.getOrDefault(player.getUniqueId(), 1.0)) / 100);
                }

                if(!r && config.R_stack.getOrDefault(player.getUniqueId(), 0.0) < 3.0) {
                    config.R_stack.put(player.getUniqueId(), config.R_stack.getOrDefault(player.getUniqueId(), 1.0) + (1 * config.Ampli.getOrDefault(player.getUniqueId(), 1.0)) / 100);
                }
            }
        };

        config.reinforcing.put(player.getUniqueId(), reinforce);
        reinforce.runTaskTimer(plugin, 0L, 20L);
    }

    private final Map<UUID, BukkitRunnable> activeRunnable = new HashMap<>();

    public void updateReinforceList(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (activeRunnable.containsKey(playerUUID) || !tag.Benjamin.contains(player)) {
            return;
        }

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !tag.Benjamin.contains(player)) {

                    player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

                    activeRunnable.remove(playerUUID);
                    this.cancel();
                    return;
                }

                ScoreboardManager manager = Bukkit.getScoreboardManager();
                Scoreboard scoreboard = manager.getNewScoreboard();

                Objective objective = scoreboard.registerNewObjective("BENJAMIN", Criteria.DUMMY, Component.text("BENJAMIN"));
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                Score score1 = objective.getScore("------------");
                score1.setScore(4);

                String str2 = String.format("%.2f", config.R_stack.getOrDefault(player.getUniqueId(), 1.0));
                Score score2 = objective.getScore("R : " + str2);
                score2.setScore(3);

                String str3 = String.format("%.2f", config.Q_stack.getOrDefault(player.getUniqueId(), 1.0));
                Score score3 = objective.getScore("Q : " + str3);
                score3.setScore(2);

                String str4 = String.format("%.2f", config.F_stack.getOrDefault(player.getUniqueId(), 1.0));
                Score score4 = objective.getScore("F : " + str4);
                score4.setScore(1);

                player.setScoreboard(scoreboard);
            }
        };

        activeRunnable.put(playerUUID, runnable);
        runnable.runTaskTimer(plugin, 0, 1L);
    }
}
