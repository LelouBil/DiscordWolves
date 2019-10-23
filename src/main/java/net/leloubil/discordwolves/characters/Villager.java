package net.leloubil.discordwolves.characters;

import net.leloubil.discordwolves.Game;
import net.leloubil.discordwolves.Player;
import net.leloubil.discordwolves.characters.base.Character;
import net.leloubil.discordwolves.characters.base.DayCharacter;

public class Villager extends Character implements DayCharacter {
    protected Villager(String name,Game game) {
        super(name,game);
    }

    public Villager(Game game){
        super("Villageois",game);
    }

    @Override
    public boolean atDay() {
        //vote machin et tout
        return true; //todo
    }


    @Override
    public boolean hasWon() {
        return false; //todo
    }
}
