package org.core.coreProgram.Cores.Nox.Skill;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Effect.ForceDamage;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Nox.Passive.Dream;
import org.core.coreProgram.Cores.Nox.coreSystem.Nox;

import java.util.HashMap;
import java.util.HashSet;

public class Q implements SkillBase {

    private final Nox config;
    private final JavaPlugin plugin;
    private final Cool cool;
    private final Dream dream;

    public Q(Nox config, JavaPlugin plugin, Cool cool, Dream dream) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
        this.dream = dream;
    }

    @Override
    public void Trigger(Player player) {
        World world = player.getWorld();

        world.spawnParticle(Particle.ENCHANTED_HIT, player.getLocation().clone().add(0, 1.2, 0), 33, 0.6, 0.6, 0.6, 1);

        Location start = player.getLocation().clone();

        Vector direction = start.getDirection().normalize();
        Location end = start.clone().add(direction.clone().multiply(6));

        player.teleport(end);

        double step = 0.5;

        for (double i = 0; i <= 6; i += step) {
            Location point = start.clone().add(direction.clone().multiply(i));
            for (Entity entity : world.getNearbyEntities(point, 1.2, 1.2, 1.2)) {
                if (entity instanceof LivingEntity target && entity != player) {
                    ForceDamage forceDamage = new ForceDamage(target, config.q_Skill_damage * config.dreamPoint.getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault("Q", 1.0));
                    forceDamage.applyEffect(player);
                }
            }
        }

        for (double i = 0; i <= 6; i += step) {
            Location point = start.clone().add(direction.clone().multiply(i));
            world.spawnParticle(Particle.CRIT, point, 1, 0, 0, 0, 0);
        }

        world.playSound(start, Sound.ENTITY_WITHER_SHOOT, 1f, 1f);
        dream.wanderersDream(player, "Q");
    }

}
