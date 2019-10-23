package net.leloubil.discordwolves.custom;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.leloubil.discordwolves.Game;

public abstract class InGameCommand extends Command {

    protected InGameCommand(String name){
        this.name = name;
    }
    protected InGameCommand(){

    }

    @Override
    protected void execute(CommandEvent event) {
        if(!Game.inGame(event.getMember().getUser())) return;
        Game g = Game.get(event.getMember().getUser());
        if(!Game.isGameChannel(event.getChannel())) return;
        exec(event,g);
    }

    protected abstract void exec(CommandEvent event,Game g);
}
