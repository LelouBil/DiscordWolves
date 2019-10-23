package net.leloubil.discordwolves.models;

import lombok.Getter;

@Getter
public class GameConfig {

    final int minPlayers;
    int simple;

    int wolvesCount;


    public GameConfig(int minPlayers) {
        this.minPlayers = minPlayers;
        int tmp = minPlayers;
        if(this.minPlayers %2 != 0){
            tmp++;
        }

        this.wolvesCount = (tmp/2) - 1;
        choosev();
    }

    public GameConfig(int minPlayers,int wolvesCount) {
        this.minPlayers = minPlayers;
        this.wolvesCount = wolvesCount;
        choosev();
    }

    private void choosev(){
        int l = this.wolvesCount;
        if(this.minPlayers == 2){
            this.simple = 0;
        }
        else this.simple = l <= 2 ? l : l - 1;
    }
}
