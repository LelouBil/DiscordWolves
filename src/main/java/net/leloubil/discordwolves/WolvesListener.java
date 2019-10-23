package net.leloubil.discordwolves;

import lombok.val;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookMessage;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

public class WolvesListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(Game.getRelayMap().containsKey(event.getMember())){
            val p = Game.getRelayMap().get(event.getMember());
            if(p.getKey() == event.getChannel()){
                WebhookClient user = p.getValue();
                String name = Game.getAnonymity().get(event.getMember());
                val mess = new WebhookMessageBuilder(event.getMessage()).setUsername("Wolf " + name.substring(0, 1).toUpperCase() + name.substring(1))
                        .setAvatarUrl("https://drive.google.com/uc?export=download&id=1CWGpTbQM9uvB0i1QWzeAlCoB3TiLAJAC")
                        .build();
                user.send(mess);
            }
        }
    }
}
