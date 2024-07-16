package dev.sha256.advancedwarps.command.warp;

import dev.sha256.advancedwarps.setting.MenuData;
import dev.sha256.advancedwarps.setting.PlayerData;
import dev.sha256.advancedwarps.setting.Settings;
import dev.sha256.advancedwarps.task.TeleportTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
import org.mineacademy.fo.model.ChatPaginator;
import org.mineacademy.fo.model.SimpleComponent;
import org.mineacademy.fo.settings.Lang;

import java.util.ArrayList;
import java.util.List;

@AutoRegister
public final class PlayerWarpCommand extends SimpleCommand {

    public PlayerWarpCommand() {
        super("pwarps|pwarp");
        setDescription("Create warps and customize them so everyone can use them.");
        setPermission("advancedwarps.playerwarp");
        setMinArguments(1);
    }

    @Override
    protected String[] getMultilineUsageMessage() {
        List<String> lines = Lang.ofList("Pwarps.usage");
        return lines.toArray(new String[0]);
    }

    @Override
    protected void onCommand() {

        checkConsole();

        Player player = getPlayer();

        String subCommand = args[0];

        switch (subCommand) {
            case "menu":
                MenuData.findMenu(Settings.PlayerWarpsMenu).displayTo(player);
                break;
            case "set":
                checkArgs(3, "/warp set <id> <name>");

                String id = args[1];
                String warpName = args[2];
                Location location = player.getLocation();

                checkBoolean(PlayerData.from(player.getUniqueId()).getWarp(id) == null, "Warp ID already exists.");

                PlayerData.from(player.getUniqueId()).setWarp(id, warpName, location);

                tell("&7Warp &f" + warpName + " &7has been set at your current location.");

                break;
            case "delete":
                checkArgs(2, "/warp delete <id>");

                checkBoolean(PlayerData.from(player.getUniqueId()).getWarp(args[1]) != null, "Warp not found.");

                PlayerData.from(player.getUniqueId()).deleteWarp(args[1]);

                tell("&7Warp &f" + args[1] + " &7has been deleted.");

                break;
            case "list":
                List<SimpleComponent> lines = new ArrayList<>();

                for(PlayerData.Warp warp : PlayerData.from(player.getUniqueId()).getWarps()) {
                    lines.add(SimpleComponent.of("&7- &f" + warp.getName() + " &7(ID: &f" + warp.getId() + "&7)"));
                }

                ChatPaginator pages = new ChatPaginator(5, ChatColor.GOLD);

                pages.setHeader("&7Your Warps");

                pages.setPages(lines);

                pages.send(player);
                break;
            default:
                checkBoolean(PlayerData.from(player.getUniqueId()).getWarp(args[0]) != null, "Warp not found.");

                tell("Teleport in 5 seconds...");
                Common.runTimer(20, new TeleportTask(player, args[0]));
                break;
        }

    }
}
