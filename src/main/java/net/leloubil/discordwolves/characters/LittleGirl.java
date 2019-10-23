package net.leloubil.discordwolves.characters;

import net.leloubil.discordwolves.Game;
import net.leloubil.discordwolves.characters.base.NightCharacter;

import java.util.Comparator;

public class LittleGirl extends Villager implements NightCharacter {

    public static Comparator<? super NightCharacter> afterWolfComp(){
        return (Comparator<NightCharacter>) (o1, o2) -> {
            if(o1.getClass().getSimpleName().equals(Werewolf.class.getSimpleName())){
                if(o2.getClass().getSimpleName().equals(LittleGirl.class.getSimpleName())){
                    return -1;
                }
            }
            else if(o2.getClass().getSimpleName().equals(Werewolf.class.getSimpleName())){
                if(o1.getClass().getSimpleName().equals(LittleGirl.class.getSimpleName())){
                    return 1;
                }
            }
            return 0;
        };
    }

    public LittleGirl(Game g){
        super("Petite Fille",g);
    }

    @Override
    public boolean startNight(boolean first) {
        getGame().lock(getGame().getGirlChannel(),getPlayer().getUser());
        getGame().show(getGame().getGirlChannel(),getPlayer().getUser());
        getGame().beginRelay(this.getPlayer(),Werewolf.class);
        return false;
    }

    @Override
    public boolean endNight() {
        getGame().endRelay(this.getPlayer());
        return false;
    }

    @Override
    public int getNightTime() {
        return 0;
    }

    @Override
    public int getTime() {
        return 0;
    }
}
