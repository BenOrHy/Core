package org.core.coreProgram.Cores.Glacier.Skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Debuff.Frost;
import org.core.Effect.ForceDamage;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Glacier.coreSystem.Glacier;

import java.util.Set;

public class Q implements SkillBase {
    private final Glacier config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public Q(Glacier config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player) {
        ItemStack offhandItem = player.getInventory().getItem(EquipmentSlot.OFF_HAND);

        if (offhandItem.getType() == Material.BLUE_ICE && offhandItem.getAmount() >= 7) {

            player.spawnParticle(Particle.SNOWFLAKE, player.getLocation().clone().add(0, 1, 0), 80, 1.5, 1.5, 1.5, 0.1);
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1, 1);
            player.playSound(player.getLocation(), Sound.BLOCK_SNOW_BREAK, 1, 1);
            placePowderSnowCone(player, 8.0, 60.0);

            offhandItem.setAmount(offhandItem.getAmount() - 6);
        }else{
            player.playSound(player.getLocation(), Sound.BLOCK_GLASS_PLACE, 1, 1);
            player.sendActionBar(Component.text("Blue Ice needed").color(NamedTextColor.RED));
            long cools = 100L;
            cool.updateCooldown(player, "Q", cools);
        }
    }

    public void placePowderSnowCone(Player player, double radius, double angleDegrees) {
        Location playerLoc = player.getLocation();
        World world = player.getWorld();

        Vector forward = playerLoc.getDirection().setY(0).normalize();
        Vector origin = new Vector(playerLoc.getX() + 0.5, playerLoc.getY(), playerLoc.getZ() + 0.5);

        double halfAngleRad = Math.toRadians(angleDegrees / 2);

        int minX = (int)Math.floor(playerLoc.getX() - radius);
        int maxX = (int)Math.ceil(playerLoc.getX() + radius);
        int minZ = (int)Math.floor(playerLoc.getZ() - radius);
        int maxZ = (int)Math.ceil(playerLoc.getZ() + radius);
        int playerY = playerLoc.getBlockY();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Vector blockPos = new Vector(x + 0.5, playerY, z + 0.5);
                Vector directionToBlock = blockPos.clone().subtract(origin);
                directionToBlock.setY(0);
                double distance = directionToBlock.length();

                if (distance == 0 || distance > radius) continue;

                directionToBlock.normalize();
                double dot = forward.dot(directionToBlock);
                dot = Math.min(1.0, Math.max(-1.0, dot));
                double angleBetween = Math.acos(dot);

                if (angleBetween <= halfAngleRad) {
                    int targetY = -1;
                    for (int y = playerY + 2; y >= playerY - 7; y--) {
                        Block baseBlock = world.getBlockAt(x, y, z);
                        if (baseBlock.getType().isSolid() && !baseBlock.isPassable()) {
                            targetY = y + 1;
                            break;
                        }
                    }
                    if (targetY == -1) continue;

                    Block aboveBlock = world.getBlockAt(x, targetY, z);

                    if (aboveBlock.isPassable() || aboveBlock.getType() == Material.AIR) {
                        aboveBlock.setType(Material.POWDER_SNOW);
                    }
                }
            }
        }
    }


}
