package net.leloubil.discordwolves.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.TextChannel;
import net.leloubil.discordwolves.models.SimpleVote;

public class UnVoteCommand extends Command {

    public UnVoteCommand() {
        this.guildOnly = false;
        this.name = "unvote";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!SimpleVote.vote(event.getChannel(),event.getMember(),null)){
            event.reactError();
            return;
        }

    }
}
