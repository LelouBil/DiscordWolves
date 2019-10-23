package net.leloubil.discordwolves.commands.ingame;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.leloubil.discordwolves.Game;

public class InCommand extends net.leloubil.discordwolves.custom.InGameCommand {

    public InCommand(){
        this.name = "ingame";
    }
    @Override
    protected void exec(CommandEvent event, Game g) {
        event.reply("yep");
    }
}
