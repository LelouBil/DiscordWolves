package net.leloubil.discordwolves.models;


import lombok.Getter;
import lombok.val;
import net.leloubil.discordwolves.Game;
import net.leloubil.discordwolves.Player;
import net.leloubil.discordwolves.characters.LittleGirl;
import net.leloubil.discordwolves.characters.base.NightCharacter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TimeTable {

    @Getter
    int currentTime;

    public void next(){
        sorted.stream().filter(c -> c.getNightTime() == currentTime).forEach(NightCharacter::endNight);
        currentTime++;
        System.out.println("TimeTable.next");
        call();

    }

    private List<NightCharacter> sorted = new ArrayList<>();

    private Game g;
    public void run(Game game) {
        this.g = game;
        currentTime = 0;
        sorted = g.getLiving().stream().map(Player::getCharacter).filter(p -> p instanceof NightCharacter).map(p -> (NightCharacter) p).sorted(Comparator.comparingInt(NightCharacter::getNightTime))
                .sorted(LittleGirl.afterWolfComp()).collect(Collectors.toList());
        call();
    }

    private void call() {
        AtomicBoolean first = new AtomicBoolean(false);
        val str = sorted.stream().filter(c -> c.getNightTime() == currentTime).collect(Collectors.toList());
        if(sorted.isEmpty()){
            if(g.checkEnd()){
                return;
            }
            g.atDay();
            return;
        }
        if(str.isEmpty()){
            next();
        }
        for (NightCharacter c : str) {
            sorted.remove(c);
            if (!first.get()) {
                g.say("The " + c.getClass().getSimpleName() + " are now awake");
                c.startNight(true);

                first.set(true);
            } else c.startNight(false);
        }
    }
}
