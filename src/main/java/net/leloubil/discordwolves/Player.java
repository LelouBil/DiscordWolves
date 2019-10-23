package net.leloubil.discordwolves;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.ChannelManager;
import net.leloubil.discordwolves.characters.base.Character;

@Getter @RequiredArgsConstructor
public class Player {

    final Member user;

    @Setter
    int gameId;

    @Setter
    boolean ready;

    final VoiceChannel source;

    Character character = null;

    @Setter
    boolean alive = true;

    @Setter
    boolean leaved = false;

    public String asMention() {
        return user.getAsMention();
    }

    public void setCard(Character character) {
        this.character = character;
        this.character.setPlayer(this);
        PrivateChannel c = this.user.getUser().openPrivateChannel().complete();

        c.sendFile(this.character.getImage(),this.character.getDisplayName() + ".jpg", new MessageBuilder("Tu est : **" + this.character.getDisplayName() + "** !").build()).complete();

    }

    public String getName() {
        return user.getUser().getName().replaceAll("[^a-zA-Z0-9 -]","").split(" ")[0];
    }

    @Override
    public String toString() {
        return getName();
    }

    public void kill(Game g) {
        alive = false;
        Game.mute(getUser(),g.voiceChannel);
        g.lock(g.textChannel,getUser());
        g.hide(g.getWolveschannel(),getUser());
        g.hide(g.getGirlChannel(),getUser());
        g.show(g.getDeadChannel(),getUser());
        g.show(g.textChannel,getUser());
        g.getGuild().getController().moveVoiceMember(getUser(),g.getDeadChannel()).complete();
        String before = getUser().getNickname() == null ? getUser().getEffectiveName() : getUser().getNickname();
        before = "[DEAD] " + character.getDisplayName() + " " + before;
        g.getGuild().getController().setNickname(getUser(),before).queue();
    }
}
