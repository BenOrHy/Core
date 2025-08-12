package org.core.coreProgram.Cores.Knight.coreSystem;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.UUID;

public class Knight {

    //CoolHashmap
    public HashMap<UUID, Long> R_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> Q_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> F_COOLDOWN = new HashMap<>();

    public long frozenCool = 10000;

    //passive
    public HashMap<UUID, Boolean> collision = new HashMap<>();


    //R
    public HashMap<UUID, Integer> swordCount = new HashMap<>();
    public double R_Skill_Damage = (double) 7 /3;
    public long r_Skill_Cool = 300;

    //Q
    public double q_Skill_Damage = 3;
    public double max_Health_release = 3;
    public long q_Skill_Cool = 12000;

    //F
    public HashMap<UUID, HashSet<Entity>> damaged = new HashMap<>();
    public double f_Skill_Damage = 15;
    public long f_Skill_Cool = 10000;


    public void variableReset(Player player){

        R_COOLDOWN.remove(player.getUniqueId());
        Q_COOLDOWN.remove(player.getUniqueId());
        F_COOLDOWN.remove(player.getUniqueId());

        swordCount.remove(player.getUniqueId());

    }
}
