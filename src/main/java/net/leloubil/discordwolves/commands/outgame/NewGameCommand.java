package net.leloubil.discordwolves.commands.outgame;

import com.jagrosh.jdautilities.command.CommandEvent;
import lombok.val;
import net.leloubil.discordwolves.Game;
import net.leloubil.discordwolves.models.GameConfig;

public class NewGameCommand extends OutCommand {

    public NewGameCommand() {
        this.name = "newgame";
        this.help = "démarre une nouvelle partie";
    }

    @Override
    protected void execute(CommandEvent event) {
        val args = event.getArgs().split(" ");
        System.out.println(event.getArgs());
        int minplayers = 8;
        int wolves = -1;
        boolean wolvesSet = false;
        try {
            if (args.length == 2) {
                minplayers = Integer.parseInt(args[0]);
                wolves = Integer.parseInt(args[1]);
                wolvesSet = true;
            } else if (args.length == 1 && !event.getArgs().isEmpty()) {
                minplayers = Integer.parseInt(args[0]);
            }
        }
        catch (NumberFormatException e){
            event.replyError("Usage : `newgame [minPlayers] [wolfNumber]`");
            return;
        }
        /*if( minplayers <= 2){
            event.replyError("You need at least 3 players !");
            return;
        }
        if(wolves <= minplayers && wolvesSet){
            event.replyError("You need at least one villager");
            return;
        }*/

        Game.create(event,!wolvesSet ? new GameConfig(minplayers) : new GameConfig(minplayers,wolves));
    }
}
