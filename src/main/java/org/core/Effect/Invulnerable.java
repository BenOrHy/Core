package org.core.Effect;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Invulnerable implements Effects, Listener {
    private static final Set<Entity> invulnerablePlayers = new HashSet<>();

    private final long duration;

    public Invulnerable(long duration) {
        this.duration = duration;
    }

    @Override
    public void applyEffect(Entity entity) {
        if (invulnerablePlayers.contains(entity)) {
            return;
        }

        invulnerablePlayers.add(entity);
        entity.setInvulnerable(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                removeEffect(entity);
            }
        }.runTaskLater(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Core")), duration / 50L); // 50tick = 1ticket
    }

    @Override
    public void removeEffect(Entity entity) {
        if (invulnerablePlayers.contains(entity)) {
            invulnerablePlayers.remove(entity);
            entity.setInvulnerable(false);
        }
    }

    @EventHandler
    public void quitRemove(PlayerQuitEvent event){
        Player player = event.getPlayer();
        removeEffect(player);
    }

    public static boolean isInvulnerable(Player player) {
        return invulnerablePlayers.contains(player);
    }
}
