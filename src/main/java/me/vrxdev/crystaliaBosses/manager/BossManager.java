package me.vrxdev.crystaliaBosses.manager;

import me.vrxdev.crystaliaBosses.object.CBoss;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
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

    @Nullable
    public CBoss getBoss(String name) {
        return get(name);
    }

    public CBoss getBossFromItem(ItemStack item) {
        for (CBoss boss : getValues()) {
            if (boss.getSummonItem().isSimilar(item)) return boss;
        }
        return null;
    }

    @Override
    protected CBoss loadFromConfig(FileConfiguration fileConfiguration, String key) {
        ConfigurationSection section = fileConfiguration.getConfigurationSection(key);
        if (section == null) {
            logMessage("&cCould not find section " + key);
            return null;
        }

        String id = section.getString("id");
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
        return new CBoss(id, name, materialEnum, lore, drops);
    }

    @Override
    protected void saveToConfig(FileConfiguration fileConfiguration, String key, CBoss data) {}
}
