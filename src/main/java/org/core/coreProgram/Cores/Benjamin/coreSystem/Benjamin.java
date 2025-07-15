package org.core.coreProgram.Cores.Benjamin.coreSystem;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Benjamin {

    //CoolHashmap
    public HashMap<UUID, Long> R_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> Q_COOLDOWN = new HashMap<>();
    public HashMap<UUID, Long> F_COOLDOWN = new HashMap<>();

    public long frozenCool = 10000;

    //passive
    public final Map<UUID, BukkitRunnable> reinforcing = new HashMap<>();
    public HashMap<UUID, Double> Ampli = new HashMap<>();

    public HashMap<UUID, Double> R_stack = new HashMap<>();
    public HashMap<UUID, Double> Q_stack = new HashMap<>();
    public HashMap<UUID, Double> F_stack = new HashMap<>();

    //R
    public HashMap<UUID, HashSet<Entity>> damaged = new HashMap<>();
    public HashMap<UUID, Boolean> rskill_using = new HashMap<>();
    public double r_Skill_dash = 1.5;
    public double r_Skill_damage = 2;
    public long r_Skill_Cool = 3000;

    //Q
    public HashMap<UUID, HashSet<Entity>> damaged_1 = new HashMap<>();
    public HashMap<UUID, Boolean> qskill_using = new HashMap<>();
    public double q_Skill_damage = 2;
    public long q_Skill_Cool = 2000;

    //F
    public HashMap<UUID, HashSet<Entity>> damaged_2 = new HashMap<>();
    public HashMap<UUID, Boolean> fskill_using = new HashMap<>();
    public double f_Skill_damage = 6;
    public long f_Skill_Cool = 2000;

    public void variableReset(Player player){

        R_COOLDOWN.remove(player.getUniqueId());
        Q_COOLDOWN.remove(player.getUniqueId());
        F_COOLDOWN.remove(player.getUniqueId());

        R_stack.remove(player.getUniqueId());
        Q_stack.remove(player.getUniqueId());
        F_stack.remove(player.getUniqueId());

        damaged.remove(player.getUniqueId());
        damaged_2.remove(player.getUniqueId());

    }
}
