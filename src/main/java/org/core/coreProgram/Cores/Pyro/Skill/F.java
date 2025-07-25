package org.core.coreProgram.Cores.Pyro.Skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.Cool.Cool;
import org.core.Debuff.Burn;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Pyro.coreSystem.Pyro;

import java.util.Random;

public class F implements SkillBase {

    private final Pyro config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public F(Pyro config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player){

        player.swingOffHand();

        ItemStack offhandItem = player.getInventory().getItem(EquipmentSlot.OFF_HAND);

        if(offhandItem.getType() == Material.BLAZE_POWDER && offhandItem.getAmount() >= 7) {
            World world = player.getWorld();


            offhandItem.setAmount(offhandItem.getAmount() - 20);
        }else{
            player.playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1, 1);
            player.sendActionBar(Component.text("powder needed").color(NamedTextColor.RED));
            long cools = 100L;
            cool.updateCooldown(player, "F", cools);
        }

    }

}
