package org.core.coreProgram.Cores.Dagger.Passive;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.core.Cool.Cool;
import org.core.Effect.EffectManager;
import org.core.Effect.ForceDamage;
import org.core.coreConfig;
import org.core.coreProgram.Cores.Dagger.coreSystem.Dagger;

public class DamageStroke {

    private final coreConfig tag;
    private final Dagger config;
    private final JavaPlugin plugin;
    private final Cool cool;
    private EffectManager effectManager = new EffectManager();

    public DamageStroke(coreConfig tag, Dagger config, JavaPlugin plugin, Cool cool) {
        this.tag = tag;
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    public void damageStroke(Player player, LivingEntity entity){

        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1.0f);
        player.getWorld().spawnParticle(Particle.DUST, entity.getLocation().add(0, 1.5, 0), 16, 0.4, 0.4, 0.4, 0, dustOptions);

        ForceDamage forceDamage = new ForceDamage(entity, entity.getHealth() * 0.13);
        forceDamage.applyEffect(player);
        entity.setVelocity(new Vector(0, 0, 0));

    }
}
