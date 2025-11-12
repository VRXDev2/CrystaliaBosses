package me.vrxdev.crystaliaBosses;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import me.vrxdev.crystaliaBosses.object.CBoss;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static me.vrxdev.crystaliaBosses.CrystaliaBosses.messageUtils;

public class BossListener implements Listener {
    private final CrystaliaBosses plugin;

    public BossListener(CrystaliaBosses plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        CBoss bossData = plugin.bossManager.getBossFromItem(item);
        if (bossData == null) return;
        event.setCancelled(true);

        boolean summoned = plugin.bossManager.summonBoss(event.getPlayer(), bossData);
        if (!summoned) {
            messageUtils.sendMessage(event.getPlayer(), "&cThe arena is already in use! Wait for the current boss to be slain before summoning another!");
            return;
        }
        item.setAmount(item.getAmount() - 1);
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent event) {
        MythicMob mythicMob = event.getMobType();
        if (mythicMob.getFaction().equalsIgnoreCase("boss")) {
            plugin.bossManager.arenaUsed = false;
            String id = mythicMob.getInternalName();

            CBoss bossData = plugin.bossManager.getBoss(id);
            if (bossData == null) return;

            event.setDrops(bossData.getDrops());
        }
    }
}
