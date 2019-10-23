package net.leloubil.discordwolves.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.leloubil.discordwolves.Game;
import net.leloubil.discordwolves.Player;
import net.leloubil.discordwolves.characters.LittleGirl;
import net.leloubil.discordwolves.characters.Villager;
import net.leloubil.discordwolves.characters.Voyante;
import net.leloubil.discordwolves.characters.Werewolf;
import net.leloubil.discordwolves.characters.base.Character;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@RequiredArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class DrawManager {

    Game game;

    GameConfig config;

    List<Player> playerList;

    private List<Class<? extends Character>> special = new ArrayList<>(Arrays.asList(Voyante.class,LittleGirl.class));

    @NonFinal List<Character> cards = new ArrayList<>();

    public void draw(){
        for (int i = 0; i < config.wolvesCount; i++) cards.add(new Werewolf(game));
        for (int i = 0; i < config.simple; i++) cards.add(new Villager(game));
        int rem = config.minPlayers - (config.wolvesCount + config.simple);
        for (int i = 0; i < rem; i++) cards.add(getVillager(game));
        Collections.shuffle(cards);
        for (int i = 0; i < playerList.size(); i++) playerList.get(i).setCard(cards.get(i));
        System.out.println("DrawManager.draw");
    }

    private Character getVillager(Game game) {
        Class<? extends Character> cls = special.remove(0);
        try {
            return cls.getConstructor(Game.class).newInstance(game);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
