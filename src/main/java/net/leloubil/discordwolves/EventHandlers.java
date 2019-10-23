package net.leloubil.discordwolves;


import net.dv8tion.jda.core.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class EventHandlers extends ListenerAdapter {

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        Game.ifGameChannel(event.getChannelLeft(),g -> {
            if(!event.getVoiceState().inVoiceChannel()){
                g.removePlayer(event.getMember());
            } else if(!Game.isGameChannel(event.getVoiceState().getChannel())){
                g.removePlayer(event.getMember());
            }
        });
    }
}
