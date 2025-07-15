package org.core.coreProgram.Cores.Bambo.Skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.core.Cool.Cool;
import org.core.Effect.ForceDamage;
import org.core.coreProgram.Cores.Bambo.coreSystem.Bambo;
import org.core.coreProgram.Abs.SkillBase;
import org.bukkit.util.Vector;

import java.util.*;

public class R implements SkillBase {

    private final Bambo config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public R(Bambo config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player) {
        if(!config.reloaded.getOrDefault(player.getUniqueId(), false)){

            config.reloaded.put(player.getUniqueId(), true);
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 1, 1);
            player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);

            long cools = 300L;

            cool.updateCooldown(player, "R", cools);

            ItemStack offhandItem = player.getInventory().getItem(EquipmentSlot.OFF_HAND);

            if (offhandItem.getType() != Material.AIR) {
                int amount = offhandItem.getAmount();
                if (amount > 1) {
                    offhandItem.setAmount(amount - 1);
                } else {
                    player.getInventory().setItem(EquipmentSlot.OFF_HAND, null);
                }
            }

            player.sendActionBar(Component.text("Loaded").color(NamedTextColor.GREEN));

        }else{

            config.reloaded.remove(player.getUniqueId());
            player.swingMainHand();
            reUse(player);
            player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THROW, 1, 1);

        }
    }

    public void reUse(Player player){
        config.damaged.put(player.getUniqueId(), new HashSet<>());

        World world = player.getWorld();

        Location playerLocation = player.getLocation();
        Vector direction = playerLocation.getDirection().normalize().multiply(1.2);

        config.r_damaged.put(player.getUniqueId(), true);
        for (int ticks = 0; ticks < 40; ticks++) {
            Location particleLocation = playerLocation.clone()
                    .add(direction.clone().multiply(ticks * 0.4))
                    .add(0, 1.5, 0);

            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 255, 255),  0.8f);
            world.spawnParticle(Particle.DUST, particleLocation, 6, 0, 0, 0, 0, dustOptions);
            player.spawnParticle(Particle.ENCHANTED_HIT, particleLocation, 6, 0, 0, 0, 0);

            for (Entity entity : world.getNearbyEntities(particleLocation, 0.3, 0.3, 0.3)) {
                if (entity instanceof LivingEntity target
                        && entity != player
                        && !config.damaged.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(entity)) {

                    ForceDamage forceDamage = new ForceDamage(target, config.r_Skill_damage);
                    forceDamage.applyEffect(player);

                    config.damaged.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(target);
                    target.setVelocity(new Vector(0, 0, 0));
                }
            }
        }
        config.r_damaged.put(player.getUniqueId(), false);

        config.damaged.remove(player.getUniqueId());

        player.sendActionBar(Component.text("Fired!").color(NamedTextColor.DARK_GREEN));

    }
}
