package org.core.Effect;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Grounding implements  Effects, Listener {
    private static final Map<Entity, Long> groundedEntities = new HashMap();

    private final Entity target;
    private final long duration;

    public Grounding(Entity target, long duration) {
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
                groundedEntities.put(target, endTime);

                if (System.currentTimeMillis() >= endTime) {
                    removeEffect(target);
                    cancel();
                }
            }
        }.runTaskTimer(Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Core")), 0L, 20L);
    }

    @Override
    public void removeEffect(Entity entity) {
        groundedEntities.remove(entity);
    }

    public static boolean isGrounded(Entity entity) {
        Long endTime = groundedEntities.get(entity);
        return endTime != null && System.currentTimeMillis() < endTime;
    }

    @EventHandler
    public void quitRemove(PlayerQuitEvent event){
        Player player = event.getPlayer();
        removeEffect(player);
    }

    public static void handleEntityJump(EntityJumpEvent event) {
        if (isGrounded(event.getEntity())) {
            event.setCancelled(true);
        }
    }

}
