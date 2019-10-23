package net.leloubil.discordwolves.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.leloubil.discordwolves.models.SimpleVote;

public class VoteCommand extends Command {

    public VoteCommand() {
        this.guildOnly = false;
        this.name = "vote";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        if(args.length != 1){
            event.reactError();
            return;
        }
        String choose = args[0];
        if(choose.isEmpty()){
            event.reactError();
            return;
        }
        if(!SimpleVote.vote(event.getChannel(),event.getMember(),choose.toLowerCase())){
            event.reactError();
            return;
        }
        else {
            event.reactSuccess();
        }
    }
}
