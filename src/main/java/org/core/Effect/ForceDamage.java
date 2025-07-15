package org.core.Effect;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ForceDamage implements Effects, Listener {
    private final LivingEntity target;
    private final double damage;

    public ForceDamage(LivingEntity target, double damage) {
        this.target = target;
        this.damage = damage;
    }

    @Override
    public void applyEffect(Entity entity) {
        target.setNoDamageTicks(1);
        target.damage(damage, entity);
        target.setNoDamageTicks(10);
    }

    @EventHandler
    public void quitRemove(PlayerQuitEvent event){
        Player player = event.getPlayer();
        player.setNoDamageTicks(10);
    }

    @Override
    public void removeEffect(Entity entity) {
    }
}
