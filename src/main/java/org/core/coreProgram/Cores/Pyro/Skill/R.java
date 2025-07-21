package org.core.coreProgram.Cores.Pyro.Skill;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.Cool.Cool;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Pyro.coreSystem.Pyro;

public class R implements SkillBase {

    private final Pyro config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public R(Pyro config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player){

    }

}
