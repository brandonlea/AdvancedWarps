package dev.sha256.advancedwarps.command.advancedwarps;

import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;


public class AdvancedWarpsCommand extends SimpleCommandGroup {

    public AdvancedWarpsCommand() {
        super("advancedwarps|aw");
    }

    @Override
    protected void registerSubcommands() {
        registerSubcommand(new ReloadCommand());
    }
}
