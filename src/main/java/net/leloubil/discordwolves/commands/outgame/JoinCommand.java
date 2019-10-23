package net.leloubil.discordwolves.commands.outgame;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import lombok.val;
import net.dv8tion.jda.core.entities.Member;
import net.leloubil.discordwolves.Game;
import net.leloubil.discordwolves.custom.OutGameCommand;

public class JoinCommand extends OutGameCommand {

    public JoinCommand(){
        super("join");
    }

    @Override
    protected void exec(CommandEvent event) {
        val args = event.getArgs().split(" ");
        if(args.length != 1) {
            event.replyError("Usage : `join <user>`");
            return;
        }
        if(event.getMessage().getMentionedMembers().isEmpty()){
            event.reactError();
            return;
        }

        Member m = event.getMessage().getMentionedMembers().get(0);
        if(m == event.getMember()){
            event.replyError("You can't join yourself !");
            return;
        }
        if(!Game.inGame(m.getUser())){
            event.replyError("The user must be in game !");
            return;
        }
        Game.join(m,event.getMember());
        event.reactSuccess();
    }
}
