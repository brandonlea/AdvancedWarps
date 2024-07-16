package dev.sha256.advancedwarps.setting;

import lombok.Getter;
import org.mineacademy.fo.settings.SimpleSettings;

@Getter
public class Settings extends SimpleSettings {

    public static String PlayerWarpsMenu;


    private static void init() {
        PlayerWarpsMenu = getString("Player_Warp_Menu");
    }
}
