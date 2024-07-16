package dev.sha256.advancedwarps.setting;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class PlayerData extends YamlConfig {

    @Getter
    private static final HashMap<UUID, PlayerData> playerData = new HashMap<>();

    private final UUID uuid;

    private List<Warp> warps;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.warps = new ArrayList<>();

        this.loadConfiguration(NO_DEFAULT, "players/" + uuid + ".yml");
        this.save();
    }

    @ToString
    @Getter
    @Setter
    public static class Warp implements ConfigSerializable {

        // Variables added when a new warp is created
        private String id;
        private String name;
        private Location location;

        // Additional variables to make it more custom
        private Boolean isPublic;
        private Material material;
        private Double cost;
        private String description;

        public Warp(String id, String name, Location location) {
            this.id = id;
            this.name = name;
            this.location = location;
            this.isPublic = true;
        }

        public Warp() {}

        @Override
        public SerializedMap serialize() {
            SerializedMap map = new SerializedMap();

            map.put("id", id);
            map.put("name", name);
            map.put("location", location);
            map.put("isPublic", isPublic);

            if(material != null) map.put("material", material.name());

            if(cost != null) map.put("cost", cost);

            if(description != null) map.put("description", description);

            return map;
        }

        public static Warp deserialize(SerializedMap map) {
            Warp warp = new Warp();

            warp.id = map.getString("id");
            warp.name = map.getString("name");
            warp.location = map.getLocation("location");
            warp.isPublic = map.getBoolean("isPublic");

            if(map.getString("material") != null) warp.material = Material.valueOf(map.getString("material"));

            if(map.getDouble("cost") != null) warp.cost = map.getDouble("cost");

            if(map.getString("description") != null) warp.description = map.getString("description");

            return warp;
        }
    }


    @Override
    protected void onLoad() {
        warps = getList("warps", Warp.class);
    }

    @Override
    protected void onSave() {
        set("warps", warps);
    }

    // Add a new warp
    public void setWarp(String id, String name, Location location) {
        warps.add(new Warp(id, name, location));
        save();
    }

    // Get the warp by ID
    public Warp getWarp(String id) {
        for(Warp warp : warps) {
            if(warp.id.equalsIgnoreCase(id)) {
                return warp;
            }
        }

        return null;
    }

    // Update the warp
    public void saveWarp() {
        save("warps", warps);
    }

    public void deleteWarp(String id) {
        warps.removeIf(warp -> warp.id.equalsIgnoreCase(id));
        save();
    }

    public static PlayerData from(UUID uuid) {
        if(!playerData.containsKey(uuid)) {
            playerData.put(uuid, new PlayerData(uuid));
        }

        return playerData.get(uuid);
    }

    @Override
    protected boolean saveComments() {
        return false;
    }
}
