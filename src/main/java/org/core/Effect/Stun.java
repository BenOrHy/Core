package org.core.Effect;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Stun implements Effects, Listener {
    private static final Map<Entity, Long> stunnedEntities = new HashMap();

    private final Entity target;
    private final long duration;

    public Stun(Entity target, long duration) {
        this.target = target;
        this.duration = duration;
    }

    @Override
    public void applyEffect(Entity entity) {
        if (!(entity instanceof LivingEntity)) return;

        LivingEntity livingEntity = (LivingEntity) target;

        long endTime = System.currentTimeMillis() + duration;


        new BukkitRunnable() {
            @Override
            public void run() {
                stunnedEntities.put(target, endTime);
                livingEntity.setAI(false);

                if (target instanceof Player) {
                    target.sendActionBar(Component.text("Stunned").color(NamedTextColor.YELLOW));
                }

                if (System.currentTimeMillis() >= endTime) {
                    removeEffect(target);
                    cancel();
                }
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Core")), 0L, 20L);
    }

    @Override
    public void removeEffect(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) return;

        stunnedEntities.remove(entity);
        livingEntity.setAI(true);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event){
        Player player= event.getPlayer();
        removeEffect(player);
    }

    public static boolean isStunned(Entity entity) {
        Long endTime = stunnedEntities.get(entity);
        return endTime != null && System.currentTimeMillis() < endTime;
    }

    public static void handlePlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (isStunned(player)) {
            event.setCancelled(true);
        }
    }
}