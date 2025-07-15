package org.core.coreProgram.Cores.Benzene.Passive;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Effect.EffectManager;
import org.core.Effect.ForceDamage;
import org.core.coreProgram.Cores.Benzene.coreSystem.Benzene;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class DamageShare {
    private final Benzene config;
    private final JavaPlugin plugin;
    private final Cool cool;
    private EffectManager effectManager = new EffectManager();

    public DamageShare(Benzene config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    public void damageShareTrigger(Player player, Entity target, double damage) {

        World world = player.getWorld();
        double times = world.getTime();

        if(!config.damageTimes.getOrDefault(target, new LinkedHashMap<>()).containsValue(times)) {
            damageShare(player, target, damage, times);
        }

    }

    private void damageShare(Player player, Entity target, double damage, double times) {


        for (Entity chainedEntity : new ArrayList<>(config.Chain.getOrDefault(player.getUniqueId(), new LinkedHashMap<>()).values())) {
            if (chainedEntity instanceof LivingEntity && chainedEntity != target && !config.damageTimes.getOrDefault(target, new LinkedHashMap<>()).containsValue(times)) {

                Location loc1 = player.getLocation().add(0, player.getHeight() / 2 + 0.2, 0);
                Location loc2 = chainedEntity.getLocation().add(0, chainedEntity.getHeight() / 2 + 0.2, 0);
                double distance = loc1.distance(loc2);

                if(distance <= 24) {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1.0f, 1.0f);

                    chainedEntity.getWorld().playSound(player.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1.0f, 1.0f);

                    config.damageTimes.putIfAbsent(chainedEntity, new LinkedHashMap<>());
                    config.damageTimes.get(chainedEntity).put(target, times);

                    double shareDamage = ((damage * (config.Chain.getOrDefault(player.getUniqueId(), new LinkedHashMap<>()).size())/10) );

                    if (config.q_Skill_effect_2.containsValue(target)) {
                        shareDamage = shareDamage * ((double) 5 / 3);
                    }

                    ForceDamage forceDamage = new ForceDamage((LivingEntity) chainedEntity, shareDamage);
                    forceDamage.applyEffect(player);
                    chainedEntity.setVelocity(new Vector(0, 0, 0));

                    chainedEntity.getWorld().spawnParticle(Particle.SWEEP_ATTACK, chainedEntity.getLocation().add(0, 1, 0), 1, 0.1, 0.1, 0.1, 1);
                    chainedEntity.getWorld().spawnParticle(Particle.ENCHANTED_HIT, chainedEntity.getLocation().add(0, 1, 0), 10, 0.4, 0, 0.4, 1);
                    chainedEntity.getWorld().playSound(chainedEntity.getLocation().add(0, 1, 0), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
                    chainedEntity.getWorld().playSound(chainedEntity.getLocation().add(0, 1, 0), Sound.ITEM_TRIDENT_HIT_GROUND, 1.0f, 1.0f);

                    if (chainedEntity.isDead()) {
                        config.Chain.forEach((uuid, entityMap) -> {
                            entityMap.values().removeIf(entity -> entity.equals(chainedEntity));
                        });

                        config.Chain.entrySet().removeIf(entry -> entry.getValue().isEmpty());

                    }
                }
            }
        }

        for (Entity chainedEntity : new ArrayList<>(config.Chain.getOrDefault(player.getUniqueId(), new LinkedHashMap<>()).values())) {
            if (chainedEntity instanceof LivingEntity && chainedEntity != target) {
                config.damageTimes.getOrDefault(chainedEntity, new LinkedHashMap<>()).remove(target, times);
                if(config.damageTimes.getOrDefault(chainedEntity, new LinkedHashMap<>()).isEmpty()){
                    config.damageTimes.remove(chainedEntity);
                }
            }
        }
    }


}
