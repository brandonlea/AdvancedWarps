package dev.sha256.advancedwarps.task;

import dev.sha256.advancedwarps.setting.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;

public class TeleportTask extends BukkitRunnable {

    private final Player player;

    private final String id;

    private int count = 5;

    public TeleportTask(Player player, String id) {
        this.player = player;
        this.id = id;
    }

    @Override
    public void run() {

        if(count == 2) {
            Common.tell(player, "&7Teleporting in &f" + (count) + " &7seconds...");
        }

        if(count <= 0) {
            Common.tell(player, "&7You have been teleported to &f" + id + "&7.");
            player.teleport(PlayerData.from(player.getUniqueId()).getWarp(id).getLocation());
            cancel();
        }

        count--;
    }
}
