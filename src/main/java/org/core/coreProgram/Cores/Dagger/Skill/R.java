package org.core.coreProgram.Cores.Dagger.Skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Effect.ForceDamage;
import org.core.Effect.Stun;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Dagger.Passive.DamageStroke;
import org.core.coreProgram.Cores.Dagger.coreSystem.Dagger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class R implements SkillBase {
    private final Dagger config;
    private final JavaPlugin plugin;
    private final Cool cool;
    private final DamageStroke damagestroker;

    public R(Dagger config, JavaPlugin plugin, Cool cool, DamageStroke damagestroker) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
        this.damagestroker = damagestroker;
    }

    @Override
    public void Trigger(Player player) {
        World world = player.getWorld();

        player.swingMainHand();

        LivingEntity target = getTargetedEntity(player,4.0, 0.4);

        if(target != null){

            Stun stun = new Stun(target, config.r_Stun);
            stun.applyEffect(player);

            config.r_damaged.put(player.getUniqueId(), true);
            damagestroker.damageStroke(player, target);
            ForceDamage forceDamage = new ForceDamage(target, config.r_Skill_damage);
            forceDamage.applyEffect(player);
            target.setVelocity(new Vector(0, 0, 0));
            config.r_damaged.remove(player.getUniqueId());

            player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THROW, 1, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1, 1);

            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0f);
            world.spawnParticle(Particle.DUST, target.getLocation().add(0, 1.5, 0), 24, 0.4, 0.4, 0.4, 0, dustOptions);
            player.spawnParticle(Particle.CRIT, target.getLocation().add(0, 1.5, 0), 8, 0.2, 0.2, 0.2, 0);

        }else {

            player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THROW, 1, 1);

            Vector direction = player.getEyeLocation().getDirection().normalize();
            Location particleLocation = player.getEyeLocation().clone()
                    .add(direction.clone().multiply(2.0));

            player.spawnParticle(Particle.CRIT, particleLocation, 8, 0.08, 0.08, 0.08, 0);

            player.sendActionBar(Component.text("not designated").color(NamedTextColor.DARK_RED));

            long cools = 250L;
            cool.updateCooldown(player, "R", cools);

        }

    }

    public static LivingEntity getTargetedEntity(Player player, double range, double raySize) {
        World world = player.getWorld();
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        List<LivingEntity> candidates = new ArrayList<>();

        for (Entity entity : world.getNearbyEntities(eyeLocation, range, range, range)) {
            if (!(entity instanceof LivingEntity) || entity.equals(player)) continue;

            RayTraceResult result = world.rayTraceEntities(
                    eyeLocation, direction, range, raySize, e -> e.equals(entity)
            );

            if (result != null) {
                candidates.add((LivingEntity) entity);
            }
        }

        return candidates.stream()
                .min(Comparator.comparingDouble(Damageable::getHealth))
                .orElse(null);
    }
}