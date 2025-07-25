package org.core.coreProgram.Cores.Pyro.coreSystem;

import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.UUID;

public class Pyro {

    //CoolHashmap
    public HashMap<UUID, Long> R_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> Q_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> F_COOLDOWN = new HashMap<>();

    public long frozenCool = 10000;

    //passive
    public HashMap<UUID, Boolean> collision = new HashMap<>();

    public LinkedHashMap<UUID, Integer> causalgia = new LinkedHashMap<>();
    public long coolCausalgia = 7000;
    public long burnCoolExtends = 3000;
    public double burnDownStats = 0.7;

    //R
    public double r_Skill_Damage = 1;
    public long r_Skill_Cool = 10000;
    public long r_Skill_stun = 3000;

    //Q
    public double q_Skill_Damage_Percent = 50;
    public long q_Skill_Cool = 17000;
    public double q_Skill_Range = 5;

    //F
    public double f_Skill_Damage = 40;
    public long f_Skill_Cool = 80000;
    public double f_Skill_Range = 7;
    public double hpDownPercent = 25;
    public double atkDownPercent = 25;
    public double dffDownPercent = 25;
    public double spdDownPercent = 50;

    public void variableReset(Player player){

        R_COOLDOWN.remove(player.getUniqueId());
        Q_COOLDOWN.remove(player.getUniqueId());
        F_COOLDOWN.remove(player.getUniqueId());

    }
}
