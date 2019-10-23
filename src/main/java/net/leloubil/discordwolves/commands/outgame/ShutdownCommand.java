package net.leloubil.discordwolves.commands.outgame;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.leloubil.discordwolves.custom.OutGameCommand;

public class ShutdownCommand extends OutGameCommand {
    public ShutdownCommand() {
        super("shutdown");
    }

    @Override
    protected void exec(CommandEvent event) {
        event.getJDA().shutdownNow();
    }
}
