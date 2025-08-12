package org.core.coreProgram.Cores.Knight.Skill;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Effect.ForceDamage;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Knight.coreSystem.Knight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.StampedLock;

public class Q implements SkillBase {
    private final Knight config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public Q(Knight config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player){

        if(!config.isFocusing.getOrDefault(player.getUniqueId(), false)) {

            Entity target = getTargetedEntity(player, 7, 0.3);

            if(target != null) {

                if (target instanceof LivingEntity ltarget) {

                    config.isFocusing.put(player.getUniqueId(), true);

                    AttributeInstance maxHealth = ltarget.getAttribute(Attribute.MAX_HEALTH);
                    if (maxHealth == null) return;

                    double originalMax = maxHealth.getBaseValue();

                    player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                    cool.setCooldown(player, 2000L, "Focus");
                    Focus(player, ltarget, originalMax);

                }
            }else{
                player.sendActionBar(Component.text("not designated").color(NamedTextColor.BLACK));
                player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
                long cools = 100L;
                cool.updateCooldown(player, "Q", cools);
            }

        }else if(config.isFocusing.getOrDefault(player.getUniqueId(), false)){
            config.isFocusCancel.put(player.getUniqueId(), true);
        }
    }

    public void Focus(Player player, LivingEntity target, double originalMax){

        new BukkitRunnable() {
            double ticks = 0;

            @Override
            public void run() {

                if (ticks > 4 || player.isDead() || config.isFocusCancel.getOrDefault(player.getUniqueId(), false)) {
                    if(!config.isFocusCancel.getOrDefault(player.getUniqueId(), false)) {
                        Slice(player, target, originalMax);
                    }else{
                        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
                        player.sendActionBar(Component.text("Focus Cancelled").color(NamedTextColor.BLACK));
                        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1);
                        long cools = 0L;
                        cool.updateCooldown(player, "Focus", cools);
                    }

                    config.isFocusing.remove(player.getUniqueId());
                    config.isFocusCancel.remove(player.getUniqueId());

                    cancel();
                    return;
                }

                player.getWorld().spawnParticle(Particle.ENCHANTED_HIT, player.getLocation().clone().add(0, 1.3, 0), 14, 0.4, 0.4, 0.4, 1);
                player.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, target.getLocation().clone().add(0, 1.3, 0), 2, 0.2, 0.2, 0.2, 1);
                AttributeInstance maxHealth = target.getAttribute(Attribute.MAX_HEALTH);

                if(maxHealth != null && !target.isDead()) {
                    double newMax = Math.max(1.0, originalMax - 1);
                    maxHealth.setBaseValue(newMax);

                    if (target.getHealth() > newMax) {
                        target.setHealth(newMax);
                    }
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    public void Slice(Player player, LivingEntity target, double originalMax) {
        player.swingMainHand();
        World world = player.getWorld();

        double slashLength = 4.7;
        double maxAngle = Math.toRadians(47);
        int maxTicks = 3;
        double innerRadius = 3.7;
        long stepDelay = 0;

        Location eyeLoc = player.getEyeLocation();

        Vector forward = eyeLoc.getDirection().normalize();
        Vector up = new Vector(0, 1, 0);
        Vector right = forward.clone().crossProduct(up).normalize();
        up = right.clone().crossProduct(forward).normalize();

        double[] angleOffsets = {0, Math.toRadians(60), -Math.toRadians(60)};
        double[] xAngleOffsets = {0, Math.toRadians(70), -Math.toRadians(70)};

        startComboSlash(player, target, originalMax, world, slashLength, maxAngle, maxTicks,
                innerRadius, stepDelay, eyeLoc, forward, right, up, angleOffsets, xAngleOffsets, 0);
    }

    private void startComboSlash(Player player, LivingEntity target, double originalMax, World world,
                                 double slashLength, double maxAngle, int maxTicks, double innerRadius,
                                 long stepDelay, Location eyeLoc, Vector forward, Vector right, Vector up,
                                 double[] angleOffsets, double[] xAngleOffsets, int comboIndex) {

        if (comboIndex >= angleOffsets.length || player.isDead()) {
            resetTargetMaxHealth(target, originalMax);
            config.damaged.remove(player.getUniqueId());
            return;
        }

        world.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1, 1);
        player.swingMainHand();
        world.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
        world.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THROW, 1, 1);

        config.damaged.put(player.getUniqueId(), new HashSet<>());

        Vector baseDir = forward.clone()
                .rotateAroundY(angleOffsets[comboIndex]);
        baseDir = rotateAroundVector(baseDir, right, xAngleOffsets[comboIndex]).normalize();

        Vector finalBaseDir = baseDir;

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= maxTicks || player.isDead()) {
                    this.cancel();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            startComboSlash(player, target, originalMax, world, slashLength, maxAngle,
                                    maxTicks, innerRadius, stepDelay, eyeLoc, forward, right, up,
                                    angleOffsets, xAngleOffsets, comboIndex + 1);
                        }
                    }.runTaskLater(plugin, stepDelay);
                    return;
                }

                spawnSlashParticlesAndDamage(player, world, eyeLoc, right, up, finalBaseDir, maxAngle,
                        slashLength, innerRadius, maxTicks, ticks);

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        damageMainHandItem(player);
    }

    private Vector rotateAroundVector(Vector v, Vector axis, double angle) {
        axis = axis.clone().normalize();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        return v.clone().multiply(cos)
                .add(axis.clone().crossProduct(v).multiply(sin))
                .add(axis.clone().multiply(axis.dot(v) * (1 - cos)));
    }

    private void spawnSlashParticlesAndDamage(Player player, World world, Location eyeLoc,
                                              Vector right, Vector up, Vector baseDir,
                                              double maxAngle, double slashLength,
                                              double innerRadius, int maxTicks, int ticks) {

        double progress = (ticks + 1) * (maxAngle * 2 / maxTicks) - maxAngle;

        Vector rotatedDir = baseDir.clone().rotateAroundY(progress);

        for (double length = 0; length <= slashLength; length += 0.1) {
            for (double angle = -maxAngle; angle <= maxAngle; angle += Math.toRadians(2)) {
                Vector angleDir = rotatedDir.clone().rotateAroundY(angle);
                Vector localPos = angleDir.clone().multiply(length);

                Vector worldOffset = right.clone().multiply(localPos.getX())
                        .add(up.clone().multiply(localPos.getY()))
                        .add(baseDir.clone().multiply(localPos.getZ()));

                Location particleLocation = eyeLoc.clone().add(worldOffset);

                double distanceFromOrigin = particleLocation.distance(eyeLoc);

                if (distanceFromOrigin >= innerRadius) {
                    Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 0, 0), 0.7f);
                    world.spawnParticle(Particle.DUST, particleLocation, 1, 0, 0, 0, 0, dustOptions);

                    for (Entity entity : world.getNearbyEntities(particleLocation, 0.7, 0.7, 0.7)) {
                        if (entity instanceof LivingEntity target && entity != player &&
                                !config.damaged.getOrDefault(player.getUniqueId(), new HashSet<>()).contains(entity)) {
                            config.damaged.get(player.getUniqueId()).add(entity);
                            ForceDamage forceDamage = new ForceDamage(target, config.q_Skill_Damage);
                            forceDamage.applyEffect(player);
                            target.setVelocity(new Vector(0, 0, 0));
                        }
                    }
                }
            }
        }
    }

    private void damageMainHandItem(Player player) {
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemMeta meta = mainHand.getItemMeta();
        if (meta instanceof org.bukkit.inventory.meta.Damageable && mainHand.getType().getMaxDurability() > 0) {
            org.bukkit.inventory.meta.Damageable damageable = (org.bukkit.inventory.meta.Damageable) meta;
            int newDamage = damageable.getDamage() + 1;
            damageable.setDamage(newDamage);
            mainHand.setItemMeta(meta);

            if (newDamage >= mainHand.getType().getMaxDurability()) {
                player.getInventory().setItemInMainHand(null);
            }
        }
    }

    private void resetTargetMaxHealth(LivingEntity target, double originalMax) {
        AttributeInstance maxHealth = target.getAttribute(Attribute.MAX_HEALTH);

        if (maxHealth != null && !target.isDead()) {
            double newMax = Math.max(1.0, originalMax - 1);
            maxHealth.setBaseValue(newMax);

            if (target.getHealth() > newMax) {
                target.setHealth(newMax);
            }
        }
    }

    public static LivingEntity getTargetedEntity(Player player, double range, double raySize) {
        World world = player.getWorld();
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        List<LivingEntity> candidates = new ArrayList<>();

        for (Entity entity : world.getNearbyEntities(eyeLocation, range, range, range)) {
            if (!(entity instanceof LivingEntity) || entity.equals(player)) continue;

            RayTraceResult result = world.rayTraceEntities(
                    eyeLocation, direction, range, raySize, e -> e.equals(entity)
            );

            if (result != null) {
                candidates.add((LivingEntity) entity);
            }
        }

        return candidates.stream()
                .min(Comparator.comparingDouble(Damageable::getHealth))
                .orElse(null);
    }
}
