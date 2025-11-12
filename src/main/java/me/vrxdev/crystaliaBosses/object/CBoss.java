package me.vrxdev.crystaliaBosses.object;

import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static me.vrxdev.crystaliaBosses.CrystaliaBosses.logMessage;
import static org.sculkdev.sculklibs.util.MessageUtils.fixColor;
import static org.sculkdev.sculklibs.util.MessageUtils.fixLore;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class CBoss {
    public final String id;
    private final Material material;
    private final List<String> lore;
    public boolean announceKill = false;

    public final int initialCooldown;
    public int cooldown = 0;

    private final ItemStack summonItem;
    private final List<String> drops;

    public CBoss(Plugin plugin, String id, int cooldown, String name, Material material, List<String> lore, List<String> drops) {
        this.id = id;
        this.material = material;
        this.lore = lore;

        this.initialCooldown = cooldown;

        summonItem = new ItemStack(material);
        ItemMeta meta = summonItem.getItemMeta();
        if (meta != null) {
            meta.setItemName(fixColor(name));
            meta.setLore(fixLore(lore));
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "id"), PersistentDataType.STRING, this.id);

            summonItem.setItemMeta(meta);
        }

        this.drops = drops;
    }

    public ItemStack getSummonItem() {
        return summonItem;
    }

    public List<ItemStack> getDrops() {
        List<ItemStack> parsedDrops = new ArrayList<>();

        for (String data : drops) {
            String[] split = data.split("\\|");
            if (split.length != 2) {
                logMessage("&cInvalid drop data: " + data + " - missing chance <data>:<double>");
                continue;
            }
            String itemData = split[0];
            ItemStack item = parseItem(itemData);
            if (item == null) {
                logMessage("&cInvalid item data: " + data);
                continue;
            }

            double chance = parseDoubleSafe(split[1]);
            double random = Math.random();
            if (random > chance) continue;

            parsedDrops.add(item);
        }

        return parsedDrops;
    }

    // diamond
    // diamond;11
    // mythicmobs;bloodmoon_scythe;1
    public ItemStack parseItem(String data) {
        String[] split = data.split(";");
        if (split.length == 0) return null;

        switch (split.length) {
            case 1 -> {
                Material mat = parseMaterial(split[0]);
                return mat == null ? null : new ItemStack(mat);
            }
            case 2 -> {
                Material mat = parseMaterial(split[0]);
                int amount = parseIntSafe(split[1]);
                return mat == null ? null : new ItemStack(mat, amount);
            }
            case 3 -> {
                String source = split[0];
                String id = split[1];
                int amount = parseIntSafe(split[2]);

                return switch (source.toLowerCase()) {
                    case "minecraft" -> {
                        Material mat = parseMaterial(id);
                        yield mat == null ? null : new ItemStack(mat, amount);
                    }
                    case "itemsadder" -> {
                        CustomStack stack = CustomStack.getInstance(id);
                        if (stack == null) yield null;
                        ItemStack item = stack.getItemStack();
                        item.setAmount(amount);
                        yield item;
                    }
                    case "mythicmobs" -> MythicProvider.get().getItemManager().getItem(id)
                            .map(mythicItem -> {
                                BukkitItemStack bukkit = (BukkitItemStack) mythicItem.generateItemStack(amount);
                                return bukkit.getItemStack();
                            }).orElse(null);
                    default -> null;
                };
            }
            default -> {
                return null;
            }
        }
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
    private double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
    private Material parseMaterial(String id) {
        Material material = Material.getMaterial(id);
        if (material == null) {
            logMessage("&cInvalid material " + id);
            return null;
        }
        return material;
    }
}
