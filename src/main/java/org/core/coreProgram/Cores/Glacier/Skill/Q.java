package org.core.coreProgram.Cores.Glacier.Skill;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.Cool.Cool;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Glacier.coreSystem.Glacier;

public class Q implements SkillBase {
    private final Glacier config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public Q(Glacier config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player){

    }
}
