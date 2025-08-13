package org.core.coreProgram.Cores.Knight.coreSystem;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Core;
import org.core.Debuff.Frost;
import org.core.Effect.ForceDamage;
import org.core.coreConfig;
import org.core.coreProgram.Abs.ConfigWrapper;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Abs.absCore;
import org.core.coreProgram.Cores.Knight.Skill.F;
import org.core.coreProgram.Cores.Knight.Skill.Q;
import org.core.coreProgram.Cores.Knight.Skill.R;
import org.core.coreProgram.Cores.Knight.coreSystem.Knight;

import static org.bukkit.Bukkit.getLogger;

public class knightCore extends absCore {
    private final Core plugin;
    private final Knight config;

    private final R Rskill;
    private final Q Qskill;
    private final F Fskill;


    public knightCore(Core plugin, coreConfig tag, Knight config, Cool cool) {
        super(tag, cool);

        this.plugin = plugin;
        this.config = config;

        this.Rskill = new R(config, plugin, cool);
        this.Qskill = new Q(config, plugin, cool);
        this.Fskill = new F(config, plugin, cool);

        getLogger().info("Knight downloaded...");
    }

    @EventHandler
    public void focusCancel(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        LivingEntity attacker = null;
        Entity damager = event.getDamager();

        if (damager instanceof LivingEntity) {
            attacker = (LivingEntity) damager;
        } else if (damager instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof LivingEntity shooter) {
                attacker = shooter;
            }
        }

        if (attacker == null) return;

        if (tag.Knight.contains(player)) {
            if (config.isFocusing.getOrDefault(player.getUniqueId(), false)) {
                event.setCancelled(true);

                if (!config.isFocusCancel.getOrDefault(player.getUniqueId(), false)) {

                    Location playerLoc = player.getLocation();
                    Vector dirToAttacker = attacker.getLocation().toVector()
                            .subtract(player.getLocation().toVector())
                            .normalize();
                    playerLoc.setDirection(dirToAttacker);
                    player.teleport(playerLoc);

                    Vector knockback = dirToAttacker.multiply(1.4);
                    damager.setVelocity(knockback);

                    PotionEffect glowing = new PotionEffect(PotionEffectType.GLOWING, 20 * 3, 1, false, true);
                    ((LivingEntity) attacker).addPotionEffect(glowing);

                    player.spawnParticle(Particle.ENCHANTED_HIT, damager.getLocation().clone().add(0, 1, 0), 21, 0.4, 0.4, 0.4, 1);
                    player.spawnParticle(Particle.SWEEP_ATTACK, damager.getLocation().clone().add(0, 1, 0), 1, 0., 0, 0, 0);

                    player.playSound(playerLoc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
                    player.playSound(playerLoc, Sound.ENTITY_IRON_GOLEM_REPAIR, 1, 1);

                    config.isFocusCancel.put(player.getUniqueId(), true);
                }
            }
        }
    }



    @EventHandler
    public void respawnHealthSet(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth == null) return;

        double originalMax = 20.0;
        maxHealth.setBaseValue(originalMax);

        player.setHealth(originalMax);
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void passiveAttackEffect(PlayerInteractEvent event) {

        if(tag.Knight.contains(event.getPlayer())) {
            if (!skillUsing.contains(event.getPlayer().getUniqueId()) && !config.q_Skill_Using.getOrDefault(event.getPlayer().getUniqueId(), false)) {

                Player player = event.getPlayer();

                if (hasProperItems(player)) {
                    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {

                        if (cool.isReloading(player, "cutting")) {
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_WEAK, 1, 1);
                            return;
                        }

                        cool.setCooldown(player, 625L, "cutting");

                        World world = player.getWorld();
                        Location playerLocation = player.getLocation();
                        Vector direction = playerLocation.getDirection().normalize().multiply(1.3);

                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);

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
                                        .add(direction.clone().multiply(ticks * 1.4))
                                        .add(0, 1.4, 0);

                                player.spawnParticle(Particle.SWEEP_ATTACK, particleLocation, 1, 0, 0, 0, 0);
                                player.spawnParticle(Particle.ENCHANTED_HIT, particleLocation, 7, 0.3, 0.1, 0.3, 0);

                                for (Entity entity : world.getNearbyEntities(particleLocation, 0.7, 0.3, 0.7)) {
                                    if (entity instanceof LivingEntity target && entity != player) {

                                        ForceDamage forceDamage = new ForceDamage(target, 3);
                                        forceDamage.applyEffect(player);

                                        config.collision.put(player.getUniqueId(), true);
                                        break;
                                    }
                                }

                                ticks++;
                            }
                        }.runTaskTimer(plugin, 0L, 1L);

                        ItemStack offHand = player.getInventory().getItemInOffHand();
                        ItemMeta meta = offHand.getItemMeta();
                        if (meta instanceof Damageable && offHand.getType().getMaxDurability() > 0) {
                            Damageable damageable = (Damageable) meta;
                            int newDamage = damageable.getDamage() + 1;
                            damageable.setDamage(newDamage);
                            offHand.setItemMeta(meta);

                            if (newDamage >= offHand.getType().getMaxDurability()) {
                                player.getInventory().setItemInOffHand(null);
                            }
                        }

                        event.setCancelled(true);
                    }
                }
            } else {
                skillUsing.remove(event.getPlayer().getUniqueId());
            }
        }
    }

    @Override
    protected boolean contains(Player player) {
        return tag.Knight.contains(player);
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
        return main.getType() == Material.NETHERITE_SWORD && off.getType() == Material.DIAMOND_SWORD;
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
        return droppedItem.getType() == Material.NETHERITE_SWORD &&
                off.getType() == Material.DIAMOND_SWORD &&
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
