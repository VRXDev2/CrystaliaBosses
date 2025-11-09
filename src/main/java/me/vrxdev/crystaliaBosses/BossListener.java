package me.vrxdev.crystaliaBosses;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import me.vrxdev.crystaliaBosses.object.CBoss;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BossListener implements Listener {
    private final CrystaliaBosses plugin;

    public BossListener(CrystaliaBosses plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent event) {
        MythicMob mythicMob = event.getMobType();
        if (mythicMob.getFaction().equalsIgnoreCase("boss")) {
            String id = mythicMob.getInternalName();

            CBoss bossData = plugin.bossManager.getBoss(id);
            if (bossData == null) return;

            event.setDrops(bossData.getDrops());
        }
    }
}
