package org.core;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class coreConfig {

    private final JavaPlugin plugin;

    public Set<Player> Benjamin;
    public Set<Player> Benzene;
    public Set<Player> Bambo;
    public Set<Player> Carpenter;
    public Set<Player> Dagger;
    public Set<Player> Pyro;
    public Set<Player> Glacier;

    public coreConfig(JavaPlugin plugin) {
        this.plugin = plugin;

        this.Benjamin = new PersistentPlayerSet(plugin, "setting_benjamin");
        this.Benzene = new PersistentPlayerSet(plugin, "setting_benzene");
        this.Bambo = new PersistentPlayerSet(plugin, "setting_bambo");
        this.Carpenter = new PersistentPlayerSet(plugin, "setting_carpenter");
        this.Dagger = new PersistentPlayerSet(plugin, "setting_dagger");
        this.Pyro = new PersistentPlayerSet(plugin, "setting_pyro");
        this.Glacier = new PersistentPlayerSet(plugin, "setting_glacier");
    }

    public String getPlayerCore(Player player) {
        if (Benjamin.contains(player)) return "benjamin";
        if (Benzene.contains(player)) return "benzene";
        if (Bambo.contains(player)) return "bambo";
        if (Carpenter.contains(player)) return "carpenter";
        if (Dagger.contains(player)) return "dagger";
        if (Pyro.contains(player)) return "pyro";
        if (Glacier.contains(player)) return "glacier";
        return "none";
    }

    public void clearPlayerCore(Player player){
        player.setWalkSpeed(0.2f);
        player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(4.0);
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "setting_benjamin"), PersistentDataType.BYTE, (byte) 0);
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "setting_benzene"), PersistentDataType.BYTE, (byte) 0);
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "setting_bambo"), PersistentDataType.BYTE, (byte) 0);
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "setting_carpenter"), PersistentDataType.BYTE, (byte) 0);
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "setting_dagger"), PersistentDataType.BYTE, (byte) 0);
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "setting_pyro"), PersistentDataType.BYTE, (byte) 0);
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "setting_glacier"), PersistentDataType.BYTE, (byte) 0);
    }

    public void setSetting(Player player, String setting, boolean value) {
        NamespacedKey key = getSettingKey(setting);
        if (key == null) return;

        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(key, PersistentDataType.BYTE, value ? (byte) 1 : (byte) 0);
    }

    private NamespacedKey getSettingKey(String setting) {
        return switch (setting.toLowerCase()) {
            case "benjamin" -> new NamespacedKey(plugin, "setting_benjamin");
            case "benzene" -> new NamespacedKey(plugin, "setting_benzene");
            case "bambo" -> new NamespacedKey(plugin, "setting_bambo");
            case "carpenter" -> new NamespacedKey(plugin, "setting_carpenter");
            case "dagger" -> new NamespacedKey(plugin, "setting_dagger");
            case "pyro" -> new NamespacedKey(plugin, "setting_pyro");
            case "glacier" -> new NamespacedKey(plugin, "setting_glacier");
            default -> null;
        };
    }

    private class PersistentPlayerSet extends AbstractSet<Player> {
        private final JavaPlugin plugin;
        private final NamespacedKey key;

        public PersistentPlayerSet(JavaPlugin plugin, String keyName) {
            this.plugin = plugin;
            this.key = new NamespacedKey(plugin, keyName);
        }

        @Override
        public boolean add(Player player) {
            player.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            return true;
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Player player) {
                player.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 0);
                return true;
            }
            return false;
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Player player) {
                Byte result = player.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
                return result != null && result == (byte) 1;
            }
            return false;
        }

        @Override
        public @NotNull Iterator<Player> iterator() {
            Set<Player> result = new HashSet<>();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (contains(player)) {
                    result.add(player);
                }
            }
            return result.iterator();
        }

        @Override
        public int size() {
            return (int) plugin.getServer().getOnlinePlayers().stream()
                    .filter(this::contains).count();
        }
    }
}
