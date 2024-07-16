package dev.sha256.advancedwarps;

import dev.sha256.advancedwarps.setting.MenuData;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

public final class AdvancedWarps extends SimplePlugin {

	@Override
	protected void onPluginStart() {
		Common.log("AdvancedWarps has been enabled!");

		MenuData.loadMenus();
	}

	@Override
	protected void onPluginStop() {
		Common.log("AdvancedWarps has been disabled!");
	}
}
