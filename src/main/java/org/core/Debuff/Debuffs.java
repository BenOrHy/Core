package org.core.Debuff;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface Debuffs {
    void applyEffect(Entity entity);

    void removeEffect(Entity entity);

}
