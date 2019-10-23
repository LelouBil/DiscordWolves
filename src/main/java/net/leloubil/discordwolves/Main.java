package net.leloubil.discordwolves;


import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import lombok.Getter;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.reflections.Reflections;

import javax.security.auth.login.LoginException;

public class Main {

    @Getter
    private static EventWaiter waiter;

    @Getter
    private static JDA bot;

    public static void main(String[] args) {
        try {
            CommandClientBuilder builder = new CommandClientBuilder();
            builder.setPrefix("!");
            addCommands(builder);
            builder.setOwnerId("203874311696547851");
            waiter = new EventWaiter();
            bot = new JDABuilder("<bot-id>").build();
            bot.addEventListener(new EventHandlers());
            bot.addEventListener(waiter);
            bot.addEventListener(builder.build());
        } catch (LoginException e) {
            e.printStackTrace();
        }

        bot.addEventListener(new WolvesListener());
    }

    private static void addCommands(CommandClientBuilder builder) {
        Reflections r = new Reflections();
        r.getSubTypesOf(Command.class)
                .stream()
                .filter(c -> c != null &&
                        c.getCanonicalName() != null && c.getCanonicalName().startsWith("net.leloubil.discordwolves.commands"))
                .forEach(c -> {
                    try {
                        builder.addCommand(c.newInstance());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

    }
}
