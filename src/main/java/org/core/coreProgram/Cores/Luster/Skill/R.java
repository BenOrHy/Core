package org.core.coreProgram.Cores.Luster.Skill;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Effect.ForceDamage;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Luster.coreSystem.Luster;

public class R implements SkillBase {
    private final Luster config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public R(Luster config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player) {

        World world = player.getWorld();

        Vector dir = player.getEyeLocation().getDirection().normalize();
        Vector spawnOffset = dir.clone().multiply(0.8).add(new Vector(0, -0.4, 0));
        Location spawnLoc = player.getEyeLocation().add(spawnOffset);

        FallingBlock fb = player.getWorld().spawn(
                spawnLoc,
                FallingBlock.class,
                entity -> {
                    entity.setBlockData(Material.IRON_BLOCK.createBlockData());
                    entity.setDropItem(false);
                    entity.setHurtEntities(false);
                    entity.setGravity(false);
                    entity.setPersistent(false);
                }
        );

        double speed = 1.5;
        fb.setVelocity(dir.multiply(speed));

        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(126, 126, 126), 1.3f);
        Particle.DustOptions dustOptions_gra = new Particle.DustOptions(Color.fromRGB(255, 255, 255), 0.7f);

        player.spawnParticle(Particle.FLASH, fb.getLocation(), 13, 0, 0, 0, 0.8);

        world.playSound(fb.getLocation(), Sound.ENTITY_WITHER_DEATH, 1f, 1f);
        world.spawnParticle(Particle.ENCHANTED_HIT, spawnLoc, 30, 0.2, 0.2, 0.2, 1);

        Vector backward = player.getLocation().getDirection().multiply(-0.7);
        player.setVelocity(player.getVelocity().add(backward));

        new BukkitRunnable() {
            int life = 20;

            @Override
            public void run() {

                if (!fb.isValid()) {
                    cancel();
                    return;
                }

                if (life-- <= 0) {
                    fb.remove();
                    cancel();
                    return;
                }

                world.spawnParticle(Particle.ENCHANTED_HIT, spawnLoc, 3, 0.2, 0.2, 0.2, 0);
                player.spawnParticle(Particle.DUST, fb.getLocation(), 1, 0.1, 0.1, 0.1, 0, dustOptions);
                player.spawnParticle(Particle.DUST, fb.getLocation(), 2, 0.1, 0.1, 0.1, 0, dustOptions_gra);

                for (Entity e : world.getNearbyEntities(fb.getLocation(), 0.7, 0.7, 0.7)) {
                    if (e instanceof LivingEntity le && !le.equals(player)) {

                        world.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1.6f);

                        ForceDamage forceDamage = new ForceDamage(le, config.r_Skill_Damage);
                        forceDamage.applyEffect(player);

                        Vector knock = le.getLocation().toVector().subtract(fb.getLocation().toVector())
                                .normalize().multiply(1.7);
                        le.setVelocity(le.getVelocity().add(knock));

                        world.spawnParticle(Particle.BLOCK, fb.getLocation(), 44, 0.3, 0.3, 0.3,
                                Material.IRON_BLOCK.createBlockData());

                        world.playSound(fb.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 1f);

                        fb.remove();
                        cancel();
                        return;
                    }
                }

                Block block = fb.getLocation().getBlock();
                if (block.getType().isSolid()) {
                    world.spawnParticle(Particle.BLOCK, fb.getLocation(), 44, 0.3, 0.3, 0.3,
                            Material.IRON_BLOCK.createBlockData());
                    world.playSound(fb.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.9f, 0.7f);
                    fb.remove();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

}
