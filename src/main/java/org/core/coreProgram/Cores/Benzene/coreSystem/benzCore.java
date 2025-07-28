package org.core.coreProgram.Cores.Benzene.coreSystem;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.core.Cool.Cool;
import org.core.Core;
import org.core.coreConfig;
import org.core.coreProgram.Abs.ConfigWrapper;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Abs.absCore;
import org.core.coreProgram.Cores.Benzene.Passive.ChainCalc;
import org.core.coreProgram.Cores.Benzene.Passive.DamageAmplify;
import org.core.coreProgram.Cores.Benzene.Passive.DamageShare;
import org.core.coreProgram.Cores.Benzene.Skill.F;
import org.core.coreProgram.Cores.Benzene.Skill.Q;
import org.core.coreProgram.Cores.Benzene.Skill.R;

import java.util.LinkedHashMap;

import static org.bukkit.Bukkit.getLogger;

public class benzCore extends absCore {

    private final Core plugin;
    private final Benzene config;

    private final ChainCalc chaincalc;
    private final DamageAmplify damageAmplify;
    private final DamageShare damageShare;

    private final R Rskill;
    private final Q Qskill;
    private final F Fskill;

    public benzCore(Core plugin, coreConfig tag, Benzene config, Cool cool) {
        super(tag, cool);

        this.plugin = plugin;
        this.config = config;

        this.chaincalc = new ChainCalc(tag, config, plugin, cool);
        this.damageAmplify = new DamageAmplify(config);
        this.damageShare = new DamageShare(config, plugin, cool);

        this.Rskill = new R(config, plugin, cool, chaincalc);
        this.Qskill = new Q(config, plugin, cool);
        this.Fskill = new F(config, plugin, cool, chaincalc);

        getLogger().info("Benzene downloaded...");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void passiveAttackEffect(PlayerInteractEvent event) {
        if(tag.Benzene.contains(event.getPlayer())){
            if (skillUsing.contains(event.getPlayer().getUniqueId())) {
                skillUsing.remove(event.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void rSkillPassive(PlayerMoveEvent event){

        Player player = event.getPlayer();

        if(tag.Benzene.contains(player) && config.atkCount.getOrDefault(player.getUniqueId(), 0) < 3){
            player.setWalkSpeed((float) 0.2 * ((float) 4/3));
        }else{
            player.setWalkSpeed((float) 0.2);
        }

    }

    @EventHandler
    public void passiveEffect(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        Location loc1 = player.getLocation().add(0, player.getHeight() / 2 + 0.2, 0);
        Location loc2 = target.getLocation().add(0, target.getHeight() / 2 + 0.2, 0);
        double distance = loc1.distance(loc2);

        if(tag.Benzene.contains(player) && !config.rskill_using.getOrDefault(player.getUniqueId(), false) && config.atkCount.getOrDefault(player.getUniqueId(), 0) < 3) {
            config.atkCount.put(player.getUniqueId(), config.atkCount.getOrDefault(player.getUniqueId(), 0) + 1);
        }

        if(tag.Benzene.contains(player) && !config.rskill_using.getOrDefault(player.getUniqueId(), false)) {
            if (config.atkCount.getOrDefault(player.getUniqueId(), 0) == 3) {
                player.sendActionBar(Component.text("Skill Enable").color(NamedTextColor.DARK_GRAY));
            } else {
                player.sendActionBar(Component.text("Attack Count : " + config.atkCount.getOrDefault(player.getUniqueId(), 0)).color(NamedTextColor.GRAY));
            }
        }

        if(config.Chain.getOrDefault(player.getUniqueId(), new LinkedHashMap<>()).containsValue(target)
                && distance <= 24){
            double originalDamage = event.getDamage();
            double amplifiedDamage = damageAmplify.Amplify(player, target, originalDamage);

            event.setDamage(amplifiedDamage);

            damageShare.damageShareTrigger(player, target, originalDamage);

        }
    }

    @EventHandler
    public void chainDelete(EntityDeathEvent event) {
        Entity death = event.getEntity();

        chaincalc.decrease(death);

        if(event.getEntity() instanceof Player player && tag.Benzene.contains(player)){
            config.variableReset(player);
        }
    }

    @EventHandler
    public void chainedCreeperExplode(EntityExplodeEvent event) {
        Entity ex = event.getEntity();

        chaincalc.decrease(ex);
    }

    @Override
    protected boolean contains(Player player) {
        return tag.Benzene.contains(player);
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
        return main.getType() == Material.IRON_SWORD && off.getType() == Material.CHAIN;
    }

    private boolean canUseRSkill(Player player) {
        int count = config.atkCount.getOrDefault(player.getUniqueId(), 0);
        return count >= 3 && !config.rskill_using.getOrDefault(player.getUniqueId(), false) && !config.fskill_using.getOrDefault(player.getUniqueId(), false);
    }

    private boolean canUseQSkill(Player player) {
        return true;
    }

    private boolean canUseFSkill(Player player) {
        return !config.rskill_using.getOrDefault(player.getUniqueId(), false) && !config.fskill_using.getOrDefault(player.getUniqueId(), false);
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
                off.getType() == Material.CHAIN &&
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