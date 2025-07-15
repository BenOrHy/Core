package org.core.coreProgram.Cores.Benjamin.coreSystem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.core.Cool.Cool;
import org.core.Core;
import org.core.coreConfig;
import org.core.coreProgram.Abs.ConfigWrapper;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Abs.absCore;
import org.core.coreProgram.Cores.Benjamin.Passive.HardSlash;
import org.core.coreProgram.Cores.Benjamin.Skill.F;
import org.core.coreProgram.Cores.Benjamin.Skill.Q;
import org.core.coreProgram.Cores.Benjamin.Skill.R;
import org.core.coreProgram.Cores.Benjamin.coreSystem.Benjamin;

import java.util.LinkedHashMap;

import static org.bukkit.Bukkit.getLogger;

public class benCore extends absCore {
    private final Core plugin;
    private final Benjamin config;

    private final HardSlash hardSlash;

    private final R Rskill;
    private final Q Qskill;
    private final F Fskill;

    public benCore(Core plugin, coreConfig tag, Benjamin config, Cool cool) {
        super(tag, cool);

        this.plugin = plugin;
        this.config = config;

        this.hardSlash = new HardSlash(config, tag, plugin, cool);

        this.Rskill = new R(config, plugin, cool, hardSlash);
        this.Qskill = new Q(config, plugin, cool, hardSlash);
        this.Fskill = new F(config, plugin, cool, hardSlash);

        getLogger().info("Benjamin downloaded...");
    }

    @EventHandler
    public void reinforce(PlayerMoveEvent event){
        Player player = event.getPlayer();

        if(!config.reinforcing.containsKey(player.getUniqueId()) && tag.Benjamin.contains(player)) {
            hardSlash.slashReinforcer(player);
            hardSlash.updateReinforceList(player);
        }
    }

    @Override
    protected boolean contains(Player player) {
        return tag.Benjamin.contains(player);
    }

    @Override
    protected SkillBase getRSkill() {
        return Rskill;
    }

    @Override
    protected SkillBase getQSkill() {
        return Qskill;
    }

    @Override
    protected SkillBase getFSkill() {
        return Fskill;
    }

    private boolean hasProperItems(Player player) {
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        return main.getType() == Material.IRON_SWORD && off.getType() == Material.AIR;
    }

    private boolean canUseRSkill(Player player) {
        return !config.fskill_using.getOrDefault(player.getUniqueId(), false);
    }

    private boolean canUseQSkill(Player player) {
        return true;
    }

    private boolean canUseFSkill(Player player) {
        return !config.fskill_using.getOrDefault(player.getUniqueId(), false) && !config.qskill_using.getOrDefault(player.getUniqueId(), false);
    }

    @Override
    protected boolean isItemRequired(Player player){
        return hasProperItems(player);
    }

    @Override
    protected boolean isRCondition(Player player) {
        return canUseRSkill(player);
    }

    @Override
    protected boolean isQCondition(Player player, ItemStack droppedItem) {
        ItemStack off = player.getInventory().getItemInOffHand();
        return droppedItem.getType() == Material.IRON_SWORD &&
                off.getType() == Material.AIR &&
                canUseQSkill(player);
    }

    @Override
    protected boolean isFCondition(Player player) {
        return canUseFSkill(player);
    }

    @Override
    protected ConfigWrapper getConfigWrapper() {
        return new ConfigWrapper() {
            @Override
            public void variableReset(Player player) {
                config.variableReset(player);
            }

            @Override
            public void cooldownReset(Player player) {
                cool.updateCooldown(player, "R", config.frozenCool);
                cool.updateCooldown(player, "Q", config.frozenCool);
                cool.updateCooldown(player, "F", config.frozenCool);
            }

            @Override
            public long getRcooldown(Player player) {
                return config.R_COOLDOWN.getOrDefault(player.getUniqueId(), config.r_Skill_Cool);
            }

            @Override
            public long getQcooldown(Player player) {
                return config.Q_COOLDOWN.getOrDefault(player.getUniqueId(), config.q_Skill_Cool);
            }

            @Override
            public long getFcooldown(Player player) {
                return config.F_COOLDOWN.getOrDefault(player.getUniqueId(), config.f_Skill_Cool);
            }
        };
    }
}
