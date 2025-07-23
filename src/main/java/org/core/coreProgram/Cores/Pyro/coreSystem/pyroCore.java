package org.core.coreProgram.Cores.Pyro.coreSystem;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Core;
import org.core.Debuff.Burn;
import org.core.Effect.ForceDamage;
import org.core.Effect.Stun;
import org.core.coreConfig;
import org.core.coreProgram.Abs.ConfigWrapper;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Abs.absCore;
import org.core.coreProgram.Cores.Dagger.Passive.DamageStroke;
import org.core.coreProgram.Cores.Pyro.Passive.Causalgia;
import org.core.coreProgram.Cores.Pyro.Skill.F;
import org.core.coreProgram.Cores.Pyro.Skill.Q;
import org.core.coreProgram.Cores.Pyro.Skill.R;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.UUID;

import static org.bukkit.Bukkit.getLogger;


public class pyroCore extends absCore {
    private final Core plugin;
    private final Pyro config;

    public final Causalgia causalgia;

    private final R Rskill;
    private final Q Qskill;
    private final F Fskill;


    public pyroCore(Core plugin, coreConfig tag, Pyro config, Cool cool) {
        super(tag, cool);

        this.plugin = plugin;
        this.config = config;

        this.causalgia = new Causalgia(tag, config, plugin, cool);

        this.Rskill = new R(config, plugin, cool);
        this.Qskill = new Q(config, plugin, cool);
        this.Fskill = new F(config, plugin, cool);


        getLogger().info("Pyro downloaded...");
    }

    @EventHandler
    public void passiveAttackEffect(PlayerInteractEvent event){

        Player player = event.getPlayer();
        Action action = event.getAction();

        World world = player.getWorld();

        Location playerLocation = player.getLocation();
        Vector direction = playerLocation.getDirection().normalize().multiply(1.3);

        if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {

            if(!hasProperItems(player)){
                player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(4.0);
                return;
            }

            player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(2.0);

            player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1, 1);
            event.setCancelled(true);

            config.collision.put(player.getUniqueId(), false);

            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    if (ticks >= 7 || config.collision.getOrDefault(player.getUniqueId(), true)) {
                        config.collision.remove(player.getUniqueId());
                        this.cancel();
                        return;
                    }

                    Location particleLocation = playerLocation.clone()
                            .add(direction.clone().multiply(ticks * 1.8))
                            .add(0, 1.4, 0);

                    player.spawnParticle(Particle.FLAME, particleLocation, 1, 0, 0, 0, 0);
                    player.spawnParticle(Particle.SMOKE, particleLocation, 1, 0, 0, 0, 0);

                    for (Entity entity : world.getNearbyEntities(particleLocation, 0.5, 0.5, 0.5)) {
                        if (entity instanceof LivingEntity target && entity != player) {

                            Burst(player, particleLocation);
                            config.collision.put(player.getUniqueId(), true);
                            break;

                        }
                    }

                    ticks++;
                }
            }.runTaskTimer(plugin, 0L, 1L);

            cool.setCooldown(player, 1000L, "flame");
        }
    }

    public void Burst(Player player, Location burstLoction){

        World world = player.getWorld();

        player.playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);
        player.spawnParticle(Particle.FLAME, burstLoction, 21, 0.1, 0.1, 0.1, 0.6);
        player.spawnParticle(Particle.SOUL_FIRE_FLAME, burstLoction, 14, 0.1, 0.1, 0.1, 0.5);

        for (Entity entity : world.getNearbyEntities(burstLoction, 3, 3, 3)) {
            if (entity instanceof LivingEntity target && entity != player) {

                ForceDamage forceDamage = new ForceDamage(target, config.f_Skill_Damage);
                forceDamage.applyEffect(player);

                if (Math.random() < 0.3) {
                    Burn burn = new Burn(entity, 7000L);
                    burn.applyEffect(player);
                }

                Vector direction = entity.getLocation().toVector().subtract(burstLoction.toVector()).normalize().multiply(0.5);
                direction.setY(0.3);

            }
        }
    }

    @Override
    protected boolean contains(Player player) {
        return tag.Dagger.contains(player);
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
        return main.getType() == Material.BLAZE_ROD && off.getType() == Material.BLAZE_POWDER;
    }

    private boolean canUseRSkill(Player player) { return true; }

    private boolean canUseQSkill(Player player) { return true; }

    private boolean canUseFSkill(Player player) { return true; }

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
        return droppedItem.getType() == Material.BLAZE_ROD &&
                off.getType() == Material.BLAZE_POWDER &&
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