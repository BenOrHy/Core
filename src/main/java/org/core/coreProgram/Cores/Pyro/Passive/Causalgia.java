package org.core.coreProgram.Cores.Pyro.Passive;

import org.bukkit.plugin.java.JavaPlugin;
import org.core.Cool.Cool;
import org.core.Effect.EffectManager;
import org.core.coreConfig;
import org.core.coreProgram.Cores.Pyro.coreSystem.Pyro;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Causalgia {

    private final coreConfig tag;
    private final Pyro config;
    private final JavaPlugin plugin;
    private final Cool cool;
    private EffectManager effectManager = new EffectManager();

    public Causalgia(coreConfig tag, Pyro config, JavaPlugin plugin, Cool cool) {
        this.tag = tag;
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    public void addCausalgia(Player player, Entity entity){
        config.causalgia.put(entity.getUniqueId(), 1);
    }

    public void handleCausalgia(){

    }

}
