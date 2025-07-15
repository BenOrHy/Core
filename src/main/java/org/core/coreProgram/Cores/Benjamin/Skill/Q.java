package org.core.coreProgram.Cores.Benjamin.Skill;

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
import org.core.coreProgram.Cores.Benjamin.Passive.HardSlash;
import org.core.coreProgram.Cores.Benjamin.coreSystem.Benjamin;

import java.util.HashSet;

public class Q implements SkillBase {

    private final Benjamin config;
    private final JavaPlugin plugin;
    private final Cool cool;
    private final HardSlash hardSlash;

    public Q(Benjamin config, JavaPlugin plugin, Cool cool, HardSlash hardSlash) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
        this.hardSlash = hardSlash;
    }

    @Override
    public void Trigger(Player player) {

        config.qskill_using.put(player.getUniqueId(), true);

        player.swingMainHand();
        World world = player.getWorld();
        world.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);

        double slashLength = 4.4;
        double maxAngle = Math.toRadians(50);
        long tickDelay = 0L;
        int maxTicks = 5;
        double innerRadius = 2.2;

        config.damaged_1.put(player.getUniqueId(), new HashSet<>());

        Location origin = player.getEyeLocation().add(0, -0.6, 0);
        Vector direction = player.getLocation().getDirection().clone().setY(0).normalize();

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {

                if (ticks >= maxTicks || player.isDead()) {

                    config.qskill_using.remove(player.getUniqueId());

                    config.Q_stack.remove(player.getUniqueId());

                    this.cancel();
                    return;
                }

                double progress = (ticks + 1) * (maxAngle * 2 / maxTicks) - maxAngle;
                Vector rotatedDir = direction.clone().rotateAroundY(progress);

                for (double length = 0; length <= slashLength; length += 0.1) {
                    for (double angle = -maxAngle; angle <= maxAngle; angle += Math.toRadians(2)) {
                        Vector angleDir = rotatedDir.clone().rotateAroundY(angle);
                        Vector particleOffset = angleDir.clone().multiply(length);

                        Location particleLocation = origin.clone().add(particleOffset);

                        double distanceFromOrigin = particleLocation.distance(origin);

                        if (distanceFromOrigin >= innerRadius) {
                            if(length < innerRadius + 0.3){
                                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(66, 66, 66), 0.4f);
                                world.spawnParticle(Particle.DUST, particleLocation, 1, 0, 0, 0, 0, dustOptions);
                            }else if(length < innerRadius + 1.2){
                                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(88, 88, 88), 0.4f);
                                world.spawnParticle(Particle.DUST, particleLocation, 1, 0, 0, 0, 0, dustOptions);
                            }else{
                                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(110, 110, 110), 0.4f);
                                world.spawnParticle(Particle.DUST, particleLocation, 1, 0, 0, 0, 0, dustOptions);
                            }

                            for (Entity entity : world.getNearbyEntities(particleLocation, 0.6, 0.6, 0.6)) {
                                if (entity instanceof LivingEntity target && entity != player && !config.damaged_1.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(entity)) {
                                    config.damaged_1.getOrDefault(player.getUniqueId(), new HashSet<>()).add(entity);
                                    ForceDamage forceDamage = new ForceDamage(target, config.q_Skill_damage * config.Q_stack.getOrDefault(player.getUniqueId(), 1.0));
                                    forceDamage.applyEffect(player);
                                    target.setVelocity(new Vector(0, 0, 0));
                                }
                            }
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, tickDelay, 1L);
    }
}
