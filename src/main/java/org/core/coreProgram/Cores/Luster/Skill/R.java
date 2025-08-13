package org.core.coreProgram.Cores.Luster.Skill;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.core.Cool.Cool;
import org.core.coreProgram.Abs.SkillBase;
import org.core.coreProgram.Cores.Luster.coreSystem.Luster;

public class R implements SkillBase {
    private final Luster config;
    private final JavaPlugin plugin;
    private final Cool cool;

    public R(Luster config, JavaPlugin plugin, Cool cool) {
        this.config = config;
        this.plugin = plugin;
        this.cool = cool;
    }

    @Override
    public void Trigger(Player player) {

    }
}
