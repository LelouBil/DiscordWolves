package net.leloubil.discordwolves.commands.ingame;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.leloubil.discordwolves.Game;
import net.leloubil.discordwolves.custom.InGameCommand;

public class ReadyCommand extends InGameCommand {
    public ReadyCommand() {
        super("ready");
    }

    @Override
    protected void exec(CommandEvent event, Game g) {
        g.ready(event.getMember());
    }
}
