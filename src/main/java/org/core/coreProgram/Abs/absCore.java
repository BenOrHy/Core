package org.core.coreProgram.Abs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.core.Cool.Cool;
import org.core.coreConfig;

public abstract class absCore implements Listener {

    protected final coreConfig tag;
    protected final Cool cool;

    public absCore(coreConfig tag, Cool cool) {
        this.tag = tag;
        this.cool = cool;
    }

    protected abstract boolean contains(Player player);

    protected abstract SkillBase getRSkill();
    protected abstract SkillBase getQSkill();
    protected abstract SkillBase getFSkill();

    protected abstract boolean isItemRequired(Player player);
    protected abstract boolean isRCondition(Player player);
    protected abstract boolean isQCondition(Player player, ItemStack droppedItem);
    protected abstract boolean isFCondition(Player player);

    protected abstract ConfigWrapper getConfigWrapper();

    @EventHandler
    public void variableQuitDelete(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        getConfigWrapper().variableReset(player);
    }

    @EventHandler
    public void cooldownReset(PlayerJoinEvent event){
        Player player = event.getPlayer();
        getConfigWrapper().cooldownReset(player);
    }

    @EventHandler
    public void rSkillTrigger(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!contains(player) || !isItemRequired(player)) return;

        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                break;
            default:
                return;
        }

        event.setCancelled(true);

        if (cool.isReloading(player, "R") || !isRCondition(player)) return;

        cool.setCooldown(player, getConfigWrapper().getRcooldown(player), "R");
        getRSkill().Trigger(player);

    }

    @EventHandler
    public void qSkillTrigger(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack dropped = event.getItemDrop().getItemStack();

        if (!contains(player) || !isQCondition(player, dropped)) return;;

        event.setCancelled(true);

        if (cool.isReloading(player, "Q")) return;

        cool.setCooldown(player, getConfigWrapper().getQcooldown(player), "Q");
        getQSkill().Trigger(player);

    }

    @EventHandler
    public void fSkillTrigger(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (!(contains(player) && isItemRequired(player))) return;

        event.setCancelled(true);

        if (cool.isReloading(player, "F") || !isFCondition(player)) return;

        cool.setCooldown(player, getConfigWrapper().getFcooldown(player), "F");
        getFSkill().Trigger(player);
    }

}
