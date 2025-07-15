package org.core.coreProgram.Cores.Carpenter.Skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Effect.ForceDamage;
import org.core.Effect.Grounding;
import org.core.Effect.Invulnerable;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Carpenter.coreSystem.Carpenter;

import java.util.*;

public class Q implements SkillBase {

    private final Carpenter config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public Q(Carpenter config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player) {
        if(!config.q_using.getOrDefault(player.getUniqueId(), false)){
            player.sendActionBar(Component.text("Q click to smash").color(NamedTextColor.YELLOW));
            Load(player);
        }else{
            config.crash.put(player.getUniqueId(), true);
            Crash(player);
            cool.updateCooldown(player, "Q", 7000L);
        }
    }

    public void Load(Player player){

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);

        cool.setCooldown(player, config.q_Skill_Load, "q -normal distribution-");

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {

                if(ticks % 5 == 0){


                    player.spawnParticle(Particle.SMOKE, player.getLocation().add(0, 1.0, 0), 13, 0.5, 0.1, 0.5, 0);
                }

                if(ticks >= 100 || config.crash.getOrDefault(player.getUniqueId(), false)){
                    long cools = 50L;
                    cool.updateCooldown(player, "q -normal distribution-", cools);
                    config.q_using.remove(player.getUniqueId());
                    config.crash.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }

                config.normal_distribution.put(player.getUniqueId(), Pdf(45 + ((double) ticks / 10), 50, 1));

                config.q_using.put(player.getUniqueId(), true);

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public double Pdf(double x, double mean, double std){

        double coef = 1.0 / (std * Math.sqrt(2 * Math.PI));
        double exp = -Math.pow(x - mean, 2) / (2 * std * std);

        return coef * Math.exp(exp);
    }

    public void Crash(Player player){

        player.swingMainHand();

        World world = player.getWorld();

        config.q_damaged.put(player.getUniqueId(), new HashSet<>());

        Vector direction = player.getEyeLocation().add(0, -0.5, 0).getDirection().normalize();
        Location particleLocation = player.getEyeLocation().clone()
                .add(direction.clone().multiply(2.6));

        player.spawnParticle(Particle.EXPLOSION, particleLocation, 1, 0, 0, 0, 0);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 255, 0), 1.0f);
        world.spawnParticle(Particle.DUST, particleLocation, 70, 0.5, 0.5, 0.5, 0, dustOptions);

        double radius = 1.2;

        if(config.q_Skill_damage * (1.0 + config.normal_distribution.getOrDefault(player.getUniqueId(), 0.0)) >= 9){
            radius = 3.5;
            player.spawnParticle(Particle.END_ROD, particleLocation, 24, 0.3, 0.4, 0.3, 0.7);
            player.spawnParticle(Particle.CRIT, particleLocation, 30, 0.5, 0.5, 0.5, 0);
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        }
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 1);

        for (Entity entity : world.getNearbyEntities(particleLocation, radius, radius, radius)) {
            if (entity instanceof LivingEntity object && entity != player && !config.q_damaged.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(entity)) {

                config.q_damaging.put(player.getUniqueId(), true);
                config.q_damaged.getOrDefault(player.getUniqueId(), new HashSet<>()).add(entity);
                ForceDamage forceDamage = new ForceDamage(object, config.q_Skill_damage * (1.0 + config.normal_distribution.getOrDefault(player.getUniqueId(), 0.0)));
                forceDamage.applyEffect(player);
                entity.setVelocity(new Vector(0, 0, 0));
                config.q_damaging.remove(player.getUniqueId());

                entity.setVelocity(new Vector(0, config.q_Skill_jump * (1.0 + config.normal_distribution.getOrDefault(player.getUniqueId(), 0.0)), 0));
            }
        }

        config.q_damaged.remove(player.getUniqueId());
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
