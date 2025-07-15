package org.core.coreProgram.Cores.Carpenter.Skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.core.Cool.Cool;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Carpenter.coreSystem.Carpenter;

public class F implements SkillBase {

    private final Carpenter config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public F(Carpenter config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player) {
        if(config.f_count.getOrDefault(player.getUniqueId(), 0) < 3){
            config.f_count.put(player.getUniqueId(), config.f_count.getOrDefault(player.getUniqueId(), 0) + 1);
            if(config.f_count.getOrDefault(player.getUniqueId(), 3) != 0){
                player.sendActionBar(Component.text(3 - config.f_count.getOrDefault(player.getUniqueId(), 0)).color(NamedTextColor.YELLOW));
            }else if(config.f_count.getOrDefault(player.getUniqueId(), 3) == 0){
                player.sendActionBar(Component.text("totem set").color(NamedTextColor.YELLOW));
            }
            heal(player);
            cool.updateCooldown(player, "F", 430L);
        }else{
            config.f_count.remove(player.getUniqueId());
            player.sendActionBar(Component.text("gained").color(NamedTextColor.YELLOW));
            totem(player);
            cool.updateCooldown(player, "F", 120000L);
        }
    }

    public void heal(Player player){

        World world = player.getWorld();

        world.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
        world.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);

        player.spawnParticle(Particle.HEART, player.getLocation().add(0, 1, 0), 3, 0.3, 0.4, 0.3, 0);
        player.spawnParticle(Particle.WITCH, player.getLocation().add(0, 1, 0), 43, 0.5, 0.5, 0.5, 0);
        player.spawnParticle(Particle.ENCHANT, player.getLocation().add(0, 1, 0), 43, 0.7, 0.7, 0.7, 0);

        player.heal(config.f_Skill_heal);

    }

    public void totem(Player player){

        World world = player.getWorld();

        world.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
        world.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GOLD, 1, 1);
        world.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);

        player.spawnParticle(Particle.TOTEM_OF_UNDYING, player.getLocation().add(0, 1.2, 0), 12, 0.3, 0.4, 0.3, 0);
        player.spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1.2, 0), 24, 0.3, 0.4, 0.3, 0.7);
        player.spawnParticle(Particle.WITCH, player.getLocation().add(0, 1.2, 0), 43, 0.5, 0.5, 0.5, 0);
        player.spawnParticle(Particle.ENCHANT, player.getLocation().add(0, 1.2, 0), 43, 0.7, 0.7, 0.7, 0);

        ItemStack offhandItem = player.getInventory().getItem(EquipmentSlot.OFF_HAND);
        ItemStack setItem = ItemStack.of(Material.TOTEM_OF_UNDYING);

        if (offhandItem.getType() == Material.AIR) {
            player.getInventory().setItemInOffHand(setItem);
        }else{
            player.give(setItem);
        }

    }

}
