package org.core.coreProgram.Cores.Luster.coreSystem;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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
import org.core.coreProgram.Cores.Luster.Skill.F;
import org.core.coreProgram.Cores.Luster.Skill.Q;
import org.core.coreProgram.Cores.Luster.Skill.R;

import static org.bukkit.Bukkit.getLogger;

public class lustCore extends absCore {
    private final Core plugin;
    private final Luster config;

    private final R Rskill;
    private final Q Qskill;
    private final F Fskill;


    public lustCore(Core plugin, coreConfig tag, Luster config, Cool cool) {
        super(tag, cool);

        this.plugin = plugin;
        this.config = config;

        this.Rskill = new R(config, plugin, cool);
        this.Qskill = new Q(config, plugin, cool);
        this.Fskill = new F(config, plugin, cool);


        getLogger().info("Luster downloaded...");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void passiveAttackEffect(PlayerInteractEvent event) {

        if(tag.Luster.contains(event.getPlayer())) {
            if (!skillUsing.contains(event.getPlayer().getUniqueId())) {

                Player player = event.getPlayer();

                if (hasProperItems(player)) {
                    if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {

                        if (cool.isReloading(player, "iron")) {
                            player.playSound(player.getLocation(), Sound.BLOCK_IRON_PLACE, 1, 1);
                            return;
                        }

                        cool.setCooldown(player, 3000L, "iron");

                        World world = player.getWorld();
                        Location playerLocation = player.getLocation();
                        Vector direction = playerLocation.getDirection().normalize().multiply(1.4);

                        player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue((double) 1 /3);
                        player.playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_STEP, 1, 1);

                        config.collision.put(player.getUniqueId(), false);

                        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(126, 126, 126), 1.3f);
                        Particle.DustOptions dustOptions_gra = new Particle.DustOptions(Color.fromRGB(255, 255, 255), 0.7f);

                        new BukkitRunnable() {
                            int ticks = 0;

                            @Override
                            public void run() {
                                if (ticks >= 14 || config.collision.getOrDefault(player.getUniqueId(), true)) {
                                    config.collision.remove(player.getUniqueId());
                                    this.cancel();
                                    return;
                                }

                                Location particleLocation = playerLocation.clone()
                                        .add(direction.clone().multiply(ticks * 1.4))
                                        .add(0, 1.4, 0);

                                player.spawnParticle(Particle.DUST, particleLocation, 1, 0.1, 0.1, 0.1, 0, dustOptions);
                                player.spawnParticle(Particle.DUST, particleLocation, 2, 0.1, 0.1, 0.1, 0, dustOptions_gra);

                                for (Entity entity : world.getNearbyEntities(particleLocation, 0.5, 0.5, 0.5)) {
                                    if (entity instanceof LivingEntity target && entity != player) {

                                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1, 1);

                                        ForceDamage forceDamage = new ForceDamage(target, 4);
                                        forceDamage.applyEffect(player);

                                        if (Math.random() < 0.26) {
                                            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
                                            ForceDamage additionalForceDamage = new ForceDamage(target, 13);
                                            additionalForceDamage.applyEffect(player);
                                        }

                                        config.collision.put(player.getUniqueId(), true);
                                        break;
                                    }
                                }

                                ticks++;
                            }
                        }.runTaskTimer(plugin, 0L, 1L);

                        event.setCancelled(true);
                    }
                } else {
                    player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(4.0);
                }
            } else {
                skillUsing.remove(event.getPlayer().getUniqueId());
            }
        }
    }

    @Override
    protected boolean contains(Player player) {
        return tag.Luster.contains(player);
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
        return off.getType() == Material.LODESTONE || off.getType() == Material.IRON_INGOT;
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
        return (off.getType() == Material.LODESTONE || off.getType() == Material.IRON_INGOT) &&
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
