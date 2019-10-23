package net.leloubil.discordwolves.commands.outgame;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.leloubil.discordwolves.Game;
import net.leloubil.discordwolves.custom.OutGameCommand;

public class CleanCommand extends OutGameCommand {
    public CleanCommand() {
        super("clean");
    }

    @Override
    protected void exec(CommandEvent event) {
        event.getGuild().getCategories().stream()
                .filter(c -> c.getName().startsWith("DiscordWolves - "))
                .filter(c -> !Game.exists(c.getName().split(" - ")[1]))
                .forEach(c -> {
                    c.getChannels()
                            .forEach(ch -> ch.delete()
                                    .queue());
                    c.delete().queue();
                });
    }
}
