package me.vrxdev.crystaliaBosses.manager;

import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.adapters.BukkitWorld;
import me.vrxdev.crystaliaBosses.object.CBoss;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.sculkdev.sculklibs.data.MapDataManager;

import java.util.List;

import static me.vrxdev.crystaliaBosses.CrystaliaBosses.logMessage;

@SuppressWarnings("unused")
public class BossManager extends MapDataManager<CBoss> {
    public BossManager(Plugin plugin) {
        super(plugin, "bossConfig.yml");
    }

    public boolean arenaUsed = false;

    @Override
    protected void init() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (CBoss boss : getValues()) {
                if (boss.cooldown > 0) {
                    boss.cooldown--;
                }
            }
        }, 1,20);
    }

    public boolean summonBoss(Player player, CBoss boss) {
        if (arenaUsed) return false;
        arenaUsed = true;

        return MythicProvider.get().getMobManager().getMythicMob(boss.id).map(mythicMob -> {
                    var location = new AbstractLocation(
                            new BukkitWorld("void"),
                            0.5, 0, 0.5, -90, 0
                    );
                    return mythicMob.spawn(location, 1) != null;
                }).orElse(false);
    }


    @Nullable
    public CBoss getBoss(String name) {
        return get(name);
    }

    public CBoss getBossFromItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        boolean hasId = meta.getPersistentDataContainer().has(new NamespacedKey(plugin, "id"), PersistentDataType.STRING);
        if (!hasId) return null;

        String id = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "id"), PersistentDataType.STRING);
        return getBoss(id);
    }

    @Override
    protected CBoss loadFromConfig(FileConfiguration fileConfiguration, String key) {
        ConfigurationSection section = fileConfiguration.getConfigurationSection(key);
        if (section == null) {
            logMessage("&cCould not find section " + key);
            return null;
        }

        boolean mmExists = MythicProvider.get().getMobManager().getMythicMob(key).isPresent();
        if (!mmExists) {
            logMessage("&cCould not find MythicMob " + key);
            return null;
        }

        int cooldown = section.getInt("cooldown");

        ConfigurationSection summonSection = section.getConfigurationSection("summonItem");
        if (summonSection == null) {
            logMessage("&cCould not find " + key + ".summonItem");
            return null;
        }

        String name = summonSection.getString("name");
        if (name == null) {
            logMessage("&cCould not find " + key + ".summonItem" + ".name");
            return null;
        }

        String material = summonSection.getString("material");
        if (material == null) {
            logMessage("&cCould not find " + key + ".summonItem" + ".material");
            return null;
        }
        Material materialEnum = Material.getMaterial(material);
        if (materialEnum == null) {
            logMessage("&cInvalid material at " + key + ".summonItem" + ".material");
            return null;
        }

        List<String> lore = summonSection.getStringList("lore");

        List<String> drops = section.getStringList("drops");

        return new CBoss(plugin, key, cooldown, name, materialEnum, lore, drops);
    }

    @Override
    protected void saveToConfig(FileConfiguration fileConfiguration, String key, CBoss data) {}
}
