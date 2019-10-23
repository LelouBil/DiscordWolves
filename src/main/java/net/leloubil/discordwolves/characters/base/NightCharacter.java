package net.leloubil.discordwolves.characters.base;

public interface NightCharacter {

    boolean startNight(boolean first);

    boolean endNight();

    int getNightTime();

    int getTime();
}
