package net.leloubil.discordwolves.characters;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.leloubil.discordwolves.Game;
import net.leloubil.discordwolves.Player;
import net.leloubil.discordwolves.characters.base.NightCharacter;


public class Werewolf extends Villager implements NightCharacter {

    public Werewolf(Game game){
        super("Loup garou",game);
    }

    @Override
    public boolean startNight(boolean first) {
        if(first){
            getGame().show(getGame().getWolveschannel(),Werewolf.class);
            getGame().startWolves(getTime());
            System.out.println("Werewolf.startNight");
        }
        getGame().unlock(getGame().getWolveschannel(),getPlayer().getUser());
        return true;
    }

    @Override
    public boolean endNight() {
        System.out.println("Werewolf.endNight");
        getGame().lock(getGame().getWolveschannel(),getPlayer().getUser());
        return false;
    }

    @Override
    public int getNightTime() {
        return 0;
    }

    @Override
    public int getTime() {
        return 35;
    }

    @Override
    public boolean hasWon() {
        return false; //todo
    }
}
