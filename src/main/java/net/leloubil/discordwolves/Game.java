package net.leloubil.discordwolves;

import com.jagrosh.jdautilities.command.CommandEvent;
import javafx.util.Pair;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.webhook.WebhookClient;
import net.leloubil.discordwolves.characters.Villager;
import net.leloubil.discordwolves.characters.Werewolf;
import net.leloubil.discordwolves.characters.base.Character;
import net.leloubil.discordwolves.models.DrawManager;
import net.leloubil.discordwolves.models.GameConfig;
import net.leloubil.discordwolves.models.SimpleVote;
import net.leloubil.discordwolves.models.TimeTable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class Game {

    private static final HashMap<Integer,Game> gameList = new HashMap<>();
    private static final List<Player> playerList = new ArrayList<>();

    private Category category;

    private TextChannel createdChannel;

    private Message waitingMessage;

    private int id = Math.abs(new Random().nextInt());
    private int revTim = 20;
    private TextChannel wolveschannel;
    private TextChannel girlChannel;
    private VoiceChannel deadChannel;


    private Game(GameConfig config){
        this.config = config;
    }

    public static void create(CommandEvent event,GameConfig config) {
        if(!event.getMember().getVoiceState().inVoiceChannel()){
            event.reply("You need to be in a voice channel first !");
            return;
        }
        Game g = new Game(config);
        g.createdChannel = event.getTextChannel();
        g.createChannels(event.getGuild(),() -> {
            g.waitingMessage = g.textChannel.sendMessage("Waiting for players : *1/" + config.getMinPlayers() + "*").complete();
            g.addPlayer(event.getMember());
            event.reply("New game started by " + event.getMember().getAsMention());
            gameList.put(g.id,g);
        });

    }

    public static Game get(int id){
        return gameList.getOrDefault(id,null);
    }

    public static Game get(String str){
        if(str.startsWith("game-"))return get(Integer.parseInt(str.split("-")[1]));
        else return null;
    }

    public static boolean inGame(User user) {
        return playerList.stream().anyMatch(p -> p.user.getUser() == user);
    }

    public static Game get(User user) {
        Optional<Player> p = playerList.stream().filter(u -> u.user.getUser() == user).findFirst();
        return p.map(playerIntegerEntry -> gameList.getOrDefault(playerIntegerEntry.gameId, null)).orElse(null);
    }

    public static void join(Member dest, Member joiner) {
        if(dest == joiner) return;
        get(dest.getUser()).addPlayer(joiner);
    }

    public static boolean exists(String s) {
        return gameList.containsKey(Integer.parseInt(s));
    }
    public static boolean exists(Integer s) {
        return gameList.containsKey(s);
    }

    public static void mute(Member user,VoiceChannel c) {
        PermissionOverride p = c.getPermissionOverride(user);
        if(p == null){
            c.putPermissionOverride(user).setDeny(Permission.VOICE_SPEAK).complete();
        }
        else p.getManager().deny(Permission.VOICE_SPEAK).complete();
    }

    public static void mute(List<Member> users,VoiceChannel c){
        users.forEach(u -> mute(u,c));
    }

    public static void unmute(Member user){
        VoiceChannel c = user.getVoiceState().getChannel();
        PermissionOverride p = c.getPermissionOverride(user);
        if(p == null){
            c.putPermissionOverride(user).setAllow(Permission.VOICE_SPEAK).complete();
        }
        else p.getManager().grant(Permission.VOICE_SPEAK).complete();
    }

    public void beginRelay(Player player, Class<? extends Character> characterClass) {
        //todo
        ArrayList<String> cp = new ArrayList<>(alpha);
        Collections.shuffle(cp);
        getRoles(Werewolf.class).forEach(p -> {
            relayMap.put(p.user,new Pair<>(wolveschannel,girlHook.newClient().build()));
            anonymity.put(p.user,cp.remove(0));
        });
        this.girlChannel.sendMessage("You will now receive a copy of the wolves's messages here").queue();
    }

    private List<String> alpha = new ArrayList<>(Arrays.asList(("alpha\n" +
            "bravo\n" +
            "charlie\n" +
            "delta\n" +
            "echo\n" +
            "foxtrot\n" +
            "golf\n" +
            "hotel\n" +
            "india\n" +
            "juliet\n" +
            "kilo\n" +
            "lima\n" +
            "mike\n" +
            "november\n" +
            "oscar\n" +
            "papa\n" +
            "quebec\n" +
            "romeo\n" +
            "sierra\n" +
            "tango\n" +
            "uniform\n" +
            "victor\n" +
            "whiskey\n" +
            "x-ray\n" +
            "yankee").split("\n")));

    @Getter
    private static HashMap<Member, Pair<Channel, WebhookClient>> relayMap = new HashMap<>();

    public void endRelay(Player player) {
        //todo
        relayMap.clear();
        anonymity.clear();
        this.girlChannel.sendMessage("You will now **stop** receiving a copy of the wolves's messages").queue();
    }

    @Getter
    private static HashMap<Member,String> anonymity =  new HashMap<>();


    public static boolean isGameChannel(Channel channel) {
        return channel.getName().startsWith("game-");
    }

    public static boolean isGameChannel(MessageChannel channel) {
        return channel.getName().startsWith("game-");
    }

    private void addPlayer(Member member) {
        if(!member.getVoiceState().inVoiceChannel()) return;
        show(textChannel,member);
        hide(wolveschannel,member);
        hide(deadChannel,member);
        hide(nightChannel,member);
        mute(member,nightChannel);
        hide(girlChannel,member);
        Player p = new Player(member,member.getVoiceState().getChannel());
        p.setGameId(this.id);
        playerList.add(p);
        this.voiceChannel.getGuild().getController().moveVoiceMember(member,this.voiceChannel).queue();
        players.add(p);
        say(member.getAsMention() + " has joined the game !");
        updatePlayerCount();
    }

    private void updatePlayerCount() {
        if(this.waitingMessage == null) return;
        String mes = "Waiting for players : *";
        String sa = getPlayers().size() + "/" + config.getMinPlayers();
        String ge = "*";

        this.waitingMessage.editMessage(mes + sa + ge).queue();
    }

    public void say(String s) {
        this.textChannel.sendMessage(new MessageBuilder(s).build()).queue();
    }

    public static void ifGameChannel(Channel c, Consumer<Game> callable){
        Game g = get(c.getName());
        if(g != null) callable.accept(g);
    }

    private void createChannels(Guild g, Runnable onfinish) {
        this.category = (Category) g.getController()
                .createCategory("DiscordWolves - " +id)
                .addPermissionOverride(g.getPublicRole(), Collections.emptyList(), Collections.singletonList(Permission.VIEW_CHANNEL))
        .complete();
        this.textChannel = (TextChannel) g.getController().createTextChannel("game-" + id)
                .setParent(this.category)
                .complete();
        this.voiceChannel = (VoiceChannel) g.getController().createVoiceChannel("game-" + id)
                .setParent(this.category)
                .complete();
        val wolvetmp = g.getController().createTextChannel("game-" + id + "-wolves")
                .setParent(this.category);
        getPlayers().forEach(p -> wolvetmp.addPermissionOverride(p.getUser(),Collections.emptyList(),Collections.singleton(Permission.MESSAGE_READ)));
        this.wolveschannel = (TextChannel) wolvetmp.complete();
        val chtmp = g.getController().createTextChannel("game-" + id + "-littlegirl")
                .setParent(this.category);
        getPlayers().forEach(p -> chtmp.addPermissionOverride(p.getUser(),Collections.emptyList(),Arrays.asList(Permission.MESSAGE_READ,Permission.MESSAGE_WRITE)));
        this.girlChannel = (TextChannel) chtmp.complete();
        this.girlHook = this.girlChannel.createWebhook("GirlHook").complete();
        val ntmp = g.getController().createVoiceChannel("game-" + id + "-night")
                .setParent(this.category);
        getPlayers().forEach(p -> ntmp.addPermissionOverride(p.getUser(),Collections.emptyList(),Arrays.asList(Permission.VOICE_SPEAK,Permission.VIEW_CHANNEL)));
        this.nightChannel = (VoiceChannel) ntmp.complete();
        val dmp = g.getController().createVoiceChannel("game-" + id + "-dead")
                .setParent(this.category);
        getPlayers().forEach(p -> dmp.addPermissionOverride(p.getUser(),Collections.emptyList(),Arrays.asList(Permission.VOICE_SPEAK,Permission.VIEW_CHANNEL)));
        this.deadChannel = (VoiceChannel) dmp.complete();
        onfinish.run();
    }

    private Webhook girlHook;

    private void prepare() {
        lock(textChannel);
        lock(wolveschannel);

        this.textChannel.getHistory().retrievePast(100).complete().forEach(message -> message.delete().complete());
        this.start();
    }

    public List<Player> getRoles(Class<? extends Character> cls){
        return getPlayers().stream().filter(p -> p.character.getClass().isAssignableFrom(cls)).collect(Collectors.toList());
    }

    //region Lock channel methods
    public void lock(TextChannel c){
        lock(c,getPlayers().stream().map(p -> p.user).collect(Collectors.toList()));
    }
    public void lock(TextChannel c,List<Member> m){
        m.forEach(ma -> lock(c,ma));
    }

    public void lock(TextChannel c,Member m){
        PermissionOverride perm = c.getPermissionOverride(m);
        if(perm == null){
            c.createPermissionOverride(m).setDeny(Permission.MESSAGE_WRITE).queue();
        }
        else perm.getManager().deny(Permission.MESSAGE_WRITE).queue();

    }
    //endregion

    //region Unlock methods
    public void unlock(TextChannel c){
        unlock(c,getPlayers().stream().map(p -> p.user).collect(Collectors.toList()));
    }
    public void unlock(TextChannel c,List<Member> m){
        m.forEach(ma -> unlock(c,ma));
    }

    public void unlock(TextChannel c,Member m){
        PermissionOverride perm = c.getPermissionOverride(m);
        if(perm == null){
            c.createPermissionOverride(m).setAllow(Permission.MESSAGE_WRITE).queue();
        }
        else perm.getManager().grant(Permission.MESSAGE_WRITE).queue();
    }
    //endregion

    //region Hide methods
    public void hide(TextChannel c){
        hide(c,getPlayers().stream().map(p -> p.user).collect(Collectors.toList()));
    }
    public void hide(TextChannel c,List<Member> m){
        m.forEach(ma -> hide(c,ma));
    }

    public void hide(TextChannel c,Member m){
        PermissionOverride perm = c.getPermissionOverride(m);
        if(perm == null){
            c.createPermissionOverride(m).setDeny(Permission.MESSAGE_READ).queue();
        }
        else perm.getManager().deny(Permission.MESSAGE_READ).queue();
    }
    public void hide(VoiceChannel c,Member m){
        PermissionOverride perm = c.getPermissionOverride(m);
        if(perm == null){
            c.createPermissionOverride(m).setDeny(Permission.VIEW_CHANNEL).queue();
        }
        else perm.getManager().deny(Permission.VIEW_CHANNEL).queue();
    }
    //endregion

    //region Show methods
    public void show(TextChannel c){
        show(c,getPlayers().stream().map(p -> p.user).collect(Collectors.toList()));
    }
    public void show(TextChannel c,List<Member> m){
        m.forEach(ma -> show(c,ma));
    }
    public void show(TextChannel c,Class<? extends Character> cls){
        getRoles(cls).stream().map(ma -> ma.user).forEach(ma -> show(c,ma));
    }

    public void show(TextChannel c,Member m){
        PermissionOverride perm = c.getPermissionOverride(m);
        if(perm == null){
            c.createPermissionOverride(m).setAllow(Permission.VIEW_CHANNEL).queue();
        }
        else perm.getManager().grant(Permission.VIEW_CHANNEL).queue();
    }

    public void show(VoiceChannel c,Member m){
        PermissionOverride perm = c.getPermissionOverride(m);
        if(perm == null){
            c.createPermissionOverride(m).setAllow(Permission.VIEW_CHANNEL).queue();
        }
        else perm.getManager().grant(Permission.VIEW_CHANNEL).queue();
    }
    //endregion

    public TextChannel textChannel;

    public VoiceChannel voiceChannel;

    public GameConfig config;

    public List<Player> players = new ArrayList<>();

    public List<Player> getLiving(){
        return players.stream().filter(Player::isAlive).collect(Collectors.toList());
    }

    int currentDay;

    public List<Player> getPlayers(){
        return players.stream().filter(p -> !p.leaved).collect(Collectors.toList());
    }

    public void atNight(){
        timeTable = new TimeTable();
        say("NightTime is about to start...");
        getLiving().forEach(p -> getGuild().getController().moveVoiceMember(p.getUser(),nightChannel).complete());
        timeTable.run(this);
    }

    private VoiceChannel nightChannel;

    public void ready(Member m){
        getPlayer(m).ifPresent(p -> {
            if(getPlayers().size() < config.getMinPlayers()){
                say("You need to be at least " + config.getMinPlayers() + " before being ready !");
                return;
            }
            p.setReady(!p.isReady());
            say(p.asMention() + " is " + (!p.isReady() ? "not " : "") + "ready " + (!p.isReady() ? "anymore  " : "") + "!");
            checkReady();
        });
    }

    private void checkReady() {
        if(getPlayers().stream().allMatch(Player::isReady) && getPlayers().size() == config.getMinPlayers()) this.prepare();
    }

    private void start() {
        say("Starting game !");
        say("Drawing roles...");
        DrawManager drawManager = new DrawManager(this,config,getPlayers());
        drawManager.draw();

        say(getGuild().getPublicRole().getAsMention() + ", you have " + revTim + " secs to review your role before the first night !");
        Timer t = new Timer("firstNight");
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                t.cancel();
                Game.this.atNight();
            }}, TimeUnit.SECONDS.toMillis(revTim));
    }

    @Getter
    private TimeTable timeTable;

    private Optional<Player> getPlayer(Member m) {
        return getPlayers().stream().filter(p -> p.user == m).findFirst();
    }

    public void removePlayer(Member member) {
        players.stream().filter(pu -> pu.user == member).findFirst().ifPresent(p -> {
            if(member.getVoiceState().inVoiceChannel() && p.source != null) {
                getGuild().getController().moveVoiceMember(member, p.source).queue(removeMember(member, p));
            }
            else {
                removeMember(member,p);
            }
            updatePlayerCount();
        });
    }

    @NotNull
    private Consumer<Void> removeMember(Member member, Player p) {
        return v -> {
            getGuild().getController().setNickname(p.user,"").queue();
            if(started) {
                p.setLeaved(true);
                p.setAlive(false);
            }
            else players.remove(p);
            this.category.putPermissionOverride(member).setDeny(Permission.VIEW_CHANNEL).queue();
            playerList.remove(p);
            checkVoid();
        };
    }

    boolean started;
    boolean ended;

    private void checkVoid() {
        if(ended) return;
        if(getPlayers().isEmpty() && !started){
            cancel("All players left");
        }
    }

    public Guild getGuild(){
        return this.textChannel.getGuild();
    }

    private List<Channel> toDelete = new ArrayList<>();

    private void cancel(String reason) {
        players.forEach(p -> getGuild().getController().moveVoiceMember(p.user,p.source).queue());
        cleanup();
        this.createdChannel.sendMessage("The game " + id + " has been canceled for reason : " + reason).queue();
    }

    private void cleanup() {
        players.clear();
        Game.gameList.remove(this.id);
        playerList.removeIf(p -> p.gameId == this.id);
        toDelete.forEach(c -> c.delete().queue());
        this.voiceChannel.delete().queue(v ->this.textChannel.delete().queue(va ->this.category.delete().queue()));
    }

    private boolean wolves;

    public void startWolves(int tie) {
        wolves = true;
        SimpleVote.<Player>builder()
                .channel(wolveschannel)
                .choices(getLiving().stream().filter(p -> p.character.getClass() != Werewolf.class).collect(Collectors.toList()))
                .display(Player::getName)
                .text("Vote for your victim....")
                .timeLimit(tie)
                .draw(this::wolvewDraw)
                .finish(this::wolvesChoosed)
                .build().start();
        System.out.println("Game.startWolves");
    }

    private void wolvesChoosed(Player player) {
        if(player == null){
            wolvewDraw(null);
            return;
        }
        wolveschannel.sendMessage("Alright, **" + player.getName() + "** will die tonight !").complete();
        toKill.add(player);
        endWolves();
    }

    private void wolvewDraw(List<Player> players) {
        wolveschannel.sendMessage("Draw !").complete();
        endWolves();
    }

    private List<Player> toKill = new ArrayList<>();

    public void endWolves() {
        wolves = false;
        timeTable.next();
    }

    public void atDay() {
        lock(wolveschannel);
        toKill.forEach(p -> p.kill(this));
        getLiving().forEach(pa -> getGuild().getController().moveVoiceMember(pa.getUser(),voiceChannel).complete());
        if(toKill.isEmpty()){
            say("A new day rises, with nobody killed !");
        }
        else {
            StringBuilder b = new StringBuilder("A new day rises,but...\n");
            toKill.forEach(p -> b.append(p.getName()).append(" was killed this night, they were ").append(p.getCharacter().getDisplayName()).append("\n"));
            say(b.toString());
        }

        say("You will now vote for the traitors in the village");
        unlock(textChannel);
        SimpleVote.<Player>builder()
                .channel(textChannel)
                .text("Vote for the traitor !")
                .choices(getLiving())
                .display(Player::getName)
                .timeLimit(120)
        .finish(this::voteEnd)
        .draw(this::voteDraw)
        .build().start();
    }

    private void voteDraw(List<Player> players) {
        say("There was a draw, you need to vote again between the players that were drawed");
        SimpleVote.<Player>builder()
                .channel(textChannel)
                .text("Vote for the traitor !")
                .choices(players)
                .display(Player::getName)
                .timeLimit(40)
                .finish(this::voteEnd)
                .draw(this::doubleDraw)
                .build().start();
    }

    private void doubleDraw(List<Player> players) {
        say("Another draw... then it will be all for today..");
        atNight();
    }

    private void voteEnd(Player player) {
        if(player == null){
            voteDraw(getLiving());
            return;
        }
        say("The vote has ended, and " + player + " will be killed today");
        player.kill(this);
        say(player + " died and they were " + player.getCharacter().getDisplayName());
        if(!checkEnd()){
            atNight();
        }
    }

    public boolean checkEnd() {
        if(getRoles(Werewolf.class).isEmpty()){
            wolvesWin();
            return true;
        }
        else if (getRoles(Villager.class).isEmpty()) {
            villWin();
            return true;
        }
        else return false;
    }

    private void villWin() {
        endThing("tout les loups ont été éliminés, les villagois ont gagnés ! ");
    }

    private void endClean() {
        getPlayers().forEach(p -> {
            getGuild().getController().moveVoiceMember(p.user,voiceChannel).queue();
            getGuild().getController().setNickname(p.user,"").queue();
            unmute(p.user);
            unlock(textChannel);
            show(textChannel);
        });
    }

    private void timeChannels() {
        say("The game will be deleted in 50 seconds !");
        Timer r = new Timer();
        r.schedule(new TimerTask() {
            @Override
            public void run() {
                r.cancel();
                cleanup();
            }
        },TimeUnit.SECONDS.toMillis(50));
    }

    private void dumpRoles() {
        MessageBuilder dump = new MessageBuilder("Voici la liste des joueurs et leur roles : \n");
        playerList.stream().filter(p -> p.getCharacter() != null)
                .forEach(p -> {
                    dump.append(" - ").append(p.user).append(" : **").append(p.character.getDisplayName()).append("**");
                    if(!p.alive) dump.append(" [DEAD]");
                    dump.append("\n");
                });
        say(dump.build());
    }

    private void say(Message build) {
        this.textChannel.sendMessage(build).queue();
    }

    private void wolvesWin() {
        endThing("Les loups ont éliminés tout les villageaois, ils ont gagnés !");
    }

    private void endThing(String s) {
        say(s);
        dumpRoles();
        endClean();
        timeChannels();
    }
}
