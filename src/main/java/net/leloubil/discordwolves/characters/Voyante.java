package net.leloubil.discordwolves.characters;

import net.dv8tion.jda.core.MessageBuilder;
import net.leloubil.discordwolves.Game;
import net.leloubil.discordwolves.Player;
import net.leloubil.discordwolves.characters.base.NightCharacter;
import net.leloubil.discordwolves.models.SimpleVote;

public class Voyante extends Villager implements NightCharacter {
    public Voyante(Game game) {
        super("La Voyante",game);
    }

    @Override
    public boolean startNight(boolean first) {
        getPlayer().getUser().getUser().openPrivateChannel().queue(p -> {
            SimpleVote.<Player>builder()
                    .draw(l -> getGame().getTimeTable().next())
                    .finish(this::showCard)
                    .timeLimit(getTime())
                    .one(true)
                    .display(Player::getName)
                    .text("Choose the person that you want to know about")
                    .channel(p)
                    .choices(getGame().getLiving())
                    .build().start();
        });
        return false;
    }

    private void showCard(Player player) {
        getPlayer().getUser().getUser().openPrivateChannel().queue(p -> {
            p.sendMessage("They are....").complete();
            p.sendFile(player.getCharacter().getImage(),player.getCharacter().getDisplayName() + ".jpg", new MessageBuilder("a **").append(player.getCharacter().getDisplayName()).append("**").build()).complete();
            getGame().getTimeTable().next();
        });
    }

    @Override
    public boolean endNight() {
        return false;
    }

    @Override
    public int getNightTime() {
        return 6;
    }

    @Override
    public int getTime() {
        return 35;
    }
}
