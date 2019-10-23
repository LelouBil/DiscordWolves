package net.leloubil.discordwolves.commands.ingame;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.leloubil.discordwolves.Game;
import net.leloubil.discordwolves.custom.InGameCommand;

public class LeaveCommand extends InGameCommand {

    public LeaveCommand() {
        super("leave");
    }

    @Override
    protected void exec(CommandEvent event, Game g) {
        g.removePlayer(event.getMember());
    }
}
