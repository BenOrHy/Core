package org.core.coreProgram.Cores.Nox.coreSystem;

import it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Nox {

    //CoolHashmap
    public HashMap<UUID, Long> R_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> Q_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> F_COOLDOWN = new HashMap<>();

    public long frozenCool = 10000;

    //passive
    public final Map<UUID, BukkitRunnable> skillDream = new HashMap<>();
    public HashMap<UUID, HashMap<String, Double>> dreamPoint = new HashMap<>();

    //R
    public HashMap<UUID, HashSet<Entity>> damaged = new HashMap<>();
    public HashMap<UUID, Boolean> rskill_using = new HashMap<>();
    public double r_Skill_dash = 1.6;
    public double r_Skill_damage = 2;
    public long r_Skill_Cool = 3000;

    //Q
    public HashMap<UUID, Boolean> qskill_using = new HashMap<>();
    public double q_Skill_damage = 1;
    public long q_Skill_Cool = 2000;

    //F
    public HashMap<UUID, HashSet<Entity>> damaged_2 = new HashMap<>();
    public HashMap<UUID, Boolean> fskill_using = new HashMap<>();
    public double f_Skill_damage = 1;
    public long f_Skill_Cool = 2000;

    public void variableReset(Player player){

        R_COOLDOWN.remove(player.getUniqueId());
        Q_COOLDOWN.remove(player.getUniqueId());
        F_COOLDOWN.remove(player.getUniqueId());

        damaged.remove(player.getUniqueId());
        damaged_2.remove(player.getUniqueId());

    }
}
