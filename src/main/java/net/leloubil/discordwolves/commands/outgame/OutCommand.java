package net.leloubil.discordwolves.commands.outgame;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.User;
import net.leloubil.discordwolves.custom.OutGameCommand;
import net.leloubil.discordwolves.models.SimpleVote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OutCommand extends OutGameCommand {

    public OutCommand(){
        this.guildOnly = false;
        this.name = "outgame";
    }
    @Override
    protected void exec(CommandEvent event) {
        SimpleVote.<String>builder()
                .choices(new ArrayList<>(Arrays.asList("Moi,Toi,Pa".split(","))))
                .text("Vote pm lol")
                .display(s ->s)
                .timeLimit(60)
                .finish(s -> System.out.println("s = " + s))
                .draw(s -> System.out.println("s = " + s))
                .channel(event.getPrivateChannel())
                .one(true)
                .build().start();
    }

    private void onDraw(List<String> ts) {
        System.out.println("Draw : " + Arrays.toString(ts.toArray()));
    }

    private void onFinish(String t) {
        System.out.println("Victoire : " + t);
    }
}
