package net.leloubil.discordwolves.custom;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.leloubil.discordwolves.Game;

public abstract class OutGameCommand extends Command {

    protected OutGameCommand(String name){
        this.name = name;
    }

    protected OutGameCommand(){}

    @Override
    protected void execute(CommandEvent event) {
        if(Game.inGame(event.getAuthor())) return;
        exec(event);
    }

    protected abstract void exec(CommandEvent event);
}
