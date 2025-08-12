package org.core;

import com.destroystokyo.paper.event.entity.EntityJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.core.Cool.Cool;
import org.core.Effect.Grounding;
import org.core.Effect.Stun;
import org.core.coreProgram.Cores.Bambo.coreSystem.Bambo;
import org.core.coreProgram.Cores.Bambo.coreSystem.bambCore;
import org.core.coreProgram.Cores.Benjamin.coreSystem.Benjamin;
import org.core.coreProgram.Cores.Benjamin.coreSystem.benCore;
import org.core.coreProgram.Cores.Benzene.coreSystem.Benzene;
import org.core.coreProgram.Cores.Benzene.coreSystem.benzCore;
import org.core.coreProgram.Cores.Carpenter.coreSystem.Carpenter;
import org.core.coreProgram.Cores.Carpenter.coreSystem.carpCore;
import org.core.coreProgram.Cores.Dagger.coreSystem.Dagger;
import org.core.coreProgram.Cores.Dagger.coreSystem.dagCore;
import org.core.coreProgram.Cores.Glacier.coreSystem.Glacier;
import org.core.coreProgram.Cores.Glacier.coreSystem.glaCore;
import org.core.coreProgram.Cores.Knight.coreSystem.Knight;
import org.core.coreProgram.Cores.Knight.coreSystem.knightCore;
import org.core.coreProgram.Cores.Pyro.coreSystem.Pyro;
import org.core.coreProgram.Cores.Pyro.coreSystem.pyroCore;

public final class Core extends JavaPlugin {

    private coreConfig config;

    private static Core instance;
    private benCore ben;
    private benzCore benz;
    private bambCore bamb;
    private carpCore carp;
    private dagCore dag;
    private pyroCore pyro;
    private glaCore glacier;
    private knightCore knight;

    public static Core getInstance() {
        return instance;
    }


    @Override
    public void onEnable() {

        instance = this;

        Benjamin benConfig = new Benjamin();
        Benzene benzConfig = new Benzene();
        Bambo bambConfig = new Bambo();
        Carpenter carpConfig = new Carpenter();
        Dagger dagConfig = new Dagger();
        Pyro pyroConfig = new Pyro();
        Glacier glaConfig = new Glacier();
        Knight knightConfig = new Knight();

        Cool cool = new Cool(this);

        this.config = new coreConfig(this);

        this.ben  = new benCore(this, this.config, benConfig, cool);
        Bukkit.getPluginManager().registerEvents(this.ben, this);

        this.benz = new benzCore(this, this.config, benzConfig, cool);
        Bukkit.getPluginManager().registerEvents(this.benz, this);

        this.bamb = new bambCore(this, this.config, bambConfig, cool);
        Bukkit.getPluginManager().registerEvents(this.bamb, this);

        this.carp = new carpCore(this, this.config, carpConfig, cool);
        Bukkit.getPluginManager().registerEvents(this.carp, this);

        this.dag = new dagCore(this, this.config, dagConfig, cool);
        Bukkit.getPluginManager().registerEvents(this.dag, this);

        this.pyro = new pyroCore(this, config, pyroConfig, cool);
        Bukkit.getPluginManager().registerEvents(this.pyro, this);

        this.glacier = new glaCore(this, config, glaConfig, cool);
        Bukkit.getPluginManager().registerEvents(this.glacier, this);

        this.knight = new knightCore(this, config, knightConfig, cool);
        Bukkit.getPluginManager().registerEvents(this.knight, this);

        getLogger().info("Cores downloaded!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Cores disabled!");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Stun.handlePlayerMove(event);
    }

    @EventHandler
    public void onEntityJump(EntityJumpEvent event) {
        Grounding.handleEntityJump(event);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("core")) {
            if (args.length != 3) {
                sender.sendMessage("§c사용법: /core <플레이어 닉네임> <설정 이름> <true|false>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§c해당 플레이어를 찾을 수 없습니다.");
                return true;
            }

            String setting = args[1].toLowerCase();
            boolean value;
            if (args[2].equalsIgnoreCase("true")) {
                value = true;
            } else if (args[2].equalsIgnoreCase("false")) {
                value = false;
            } else {
                sender.sendMessage("§ctrue 또는 false를 입력하세요.");
                return true;
            }

            this.config.setSetting(target, setting, value);
            sender.sendMessage("§a" + target.getName() + "의 " + setting + " 값을 " + value + "로 설정했습니다.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("corecheck")) {

            if(args.length == 1){

                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    sender.sendMessage("§c해당 플레이어를 찾을 수 없습니다.");
                    return true;
                }

                sender.sendMessage( "§a" + target.getName() + " 소유의 core : " + this.config.getPlayerCore(target));

                return true;

            }else if(args.length == 0){

                if (!(sender instanceof Player player)) return true;
                sender.sendMessage( "§a본인 소유의 core : " + this.config.getPlayerCore(player));
                return true;

            }else{

                sender.sendMessage("§c사용법: /corecheck <플레이어 닉네임|공백>");
                return true;

            }

        }

        if (command.getName().equalsIgnoreCase("coreclear")) {

            if(args.length == 1){

                Player target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    sender.sendMessage("§c해당 플레이어를 찾을 수 없습니다.");
                    return true;
                }

                this.config.clearPlayerCore(target);
                sender.sendMessage( "§c" + target.getName() + " 소유의 core을 모두 제거했습니다");

                return true;

            }else if(args.length == 0){

                if (!(sender instanceof Player player)) return true;
                this.config.clearPlayerCore(player);
                sender.sendMessage( "§c본인 소유의 core을 모두 제거했습니다.");
                return true;

            }else{

                sender.sendMessage("§c사용법: /coreclear <플레이어 닉네임|공백>");
                return true;

            }
        }

        if (command.getName().equalsIgnoreCase("gc")) {

            if (args.length != 0) {
                sender.sendMessage("§c사용법: /gc");
                return true;
            }

            System.gc();
            sender.sendMessage("가비지 컬렉션 작동");
            return true;
        }

        return false;
    }
}
