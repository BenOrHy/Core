package org.core.coreProgram.Cores.Pyro.coreSystem;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.UUID;

public class Pyro {

    //CoolHashmap
    public HashMap<UUID, Long> R_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> Q_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> F_COOLDOWN = new HashMap<>();

    public long frozenCool = 10000;

    //passive

    //R
    public double r_Skill_damage = 10;
    public long r_Skill_Cool = 4000;

    //Q
    public double q_Skill_Damage = 8;
    public long q_Skill_Cool = 8000;

    //F
    public double f_Skill_Damage = 4;
    public long f_Skill_Cool = 0;


    public void variableReset(Player player){

        R_COOLDOWN.remove(player.getUniqueId());
        Q_COOLDOWN.remove(player.getUniqueId());
        F_COOLDOWN.remove(player.getUniqueId());

    }
}
