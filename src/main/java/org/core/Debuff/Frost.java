package org.core.Debuff;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Frost implements Debuffs{
    private static final Map<Entity, Long> frostbiteEntities = new HashMap();

    private final Entity target;
    private final long duration;

    public Frost(Entity target, long duration) {
        this.target = target;
        this.duration = duration;
    }

    @Override
    public void applyEffect(Entity entity) {
        if (!(entity instanceof LivingEntity)) return;

        long endTime = System.currentTimeMillis() + duration;

        new BukkitRunnable() {
            @Override
            public void run() {
                frostbiteEntities.put(target, endTime);

                entity.setFreezeTicks(20);

                if (System.currentTimeMillis() >= endTime) {
                    removeEffect(target);
                    cancel();
                }
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Core")), 0L, 20L);
    }

    @Override
    public void removeEffect(Entity entity) {
        frostbiteEntities.remove(entity);
    }

    public static boolean isFrostbite(Entity entity) {
        Long endTime = frostbiteEntities.get(entity);
        return endTime != null && System.currentTimeMillis() < endTime;
    }

    @EventHandler
    public void quitRemove(PlayerQuitEvent event){
        Player player = event.getPlayer();
        removeEffect(player);
    }
}
