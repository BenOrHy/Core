package org.core.coreProgram.Cores.Glacier.Skill;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.Cool.Cool;
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

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();

        int radius = 12;

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

        world.playSound(center, Sound.BLOCK_GLASS_BREAK, 1.0f, 1.1f);
        world.playSound(center, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1.0f, 1.1f);
        world.spawnParticle(Particle.SNOWFLAKE, center.add(0, 5, 0), 200, 12, 0.6, 12, 0.1);

    }

}
