package org.core.coreProgram.Cores.Glacier.Skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Effect.Grounding;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Glacier.coreSystem.Glacier;

public class F implements SkillBase {

    private final Glacier config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public F(Glacier config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player){
        World world = player.getWorld();
        Location center = player.getLocation().clone();

        SetBiome(world, center, 12);
        SetRain(world);

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);


        for (Entity entity : world.getNearbyEntities(center, 4, 4, 4)) {
            if (entity.equals(player) || !(entity instanceof LivingEntity)) continue;

            Vector direction = entity.getLocation().toVector().subtract(center.toVector()).normalize().multiply(1.5);

            entity.setVelocity(direction);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            FreezeEntity(player, center, 4);
        }, 20);

    }

    public void SetBiome(World world, Location center, int radius){

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int y = cy - radius; y <= cy + radius; y++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    world.setBiome(x, y, z, Biome.SNOWY_PLAINS);
                }
            }
        }

        for (Player cplayer : world.getNearbyPlayers(center, radius + 16)) {
            int chunkX = cplayer.getLocation().getBlockX() >> 4;
            int chunkZ = cplayer.getLocation().getBlockZ() >> 4;
            world.refreshChunk(chunkX, chunkZ);
        }
    }

    public void SetRain(World world){
        boolean wasStorm = world.hasStorm();
        boolean wasThundering = world.isThundering();
        int prevWeatherDuration = world.getWeatherDuration();
        int prevThunderDuration = world.getThunderDuration();

        world.setStorm(true);
        world.setThundering(false);
        world.setWeatherDuration(20 * 60);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            world.setStorm(wasStorm);
            world.setThundering(wasThundering);
            world.setWeatherDuration(prevWeatherDuration);
            world.setThunderDuration(prevThunderDuration);
        }, 20L * 60);
    }

    public void FreezeEntity(Player player, Location center, int radius) {
        World world = player.getWorld();
        int radiusSquared = radius * radius;

        for (Entity rangeTarget : world.getNearbyEntities(player.getLocation(), 8.0, 8.0, 8.0)) {
            if (rangeTarget instanceof LivingEntity target && rangeTarget != player) {

                Location TLoc = target.getLocation().clone();

                int cx = TLoc.getBlockX();
                int cy = TLoc.getBlockY();
                int cz = TLoc.getBlockZ();

                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {

                            if (x * x + y * y + z * z > radiusSquared) continue;

                            Block block = world.getBlockAt(cx + x, cy + y, cz + z);
                            if (block.getType() == Material.AIR) {
                                block.setType(Material.FROSTED_ICE);
                            }
                        }
                    }
                }

            }
        }

        world.playSound(center, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.1f);
        world.playSound(center, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.1f);
        world.spawnParticle(Particle.SNOWFLAKE, center, 80, 1.5, 1.5, 1.5, 0.1);
    }

}
