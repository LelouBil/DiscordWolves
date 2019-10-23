package net.leloubil.discordwolves.models;

import lombok.Builder;
import lombok.val;
import net.dv8tion.jda.core.entities.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public class SimpleVote<T> {

    private final List<T> ochoices;
    private int timeLimit;

    @Builder
    private SimpleVote(int timeLimit,boolean one, List<T> choices, Function<T, String> display, MessageChannel channel, String text, Consumer<T> finish, Consumer<List<T>> draw) {
        this.timeLimit = timeLimit;
        this.currentTime = timeLimit;
        this.force = one;
        this.choices = choices.stream().collect(Collectors.toMap(display.andThen(String::toLowerCase), e -> e));
        this.ochoices = choices;
        this.display = display;
        this.channel = channel;
        this.text = text;
        this.finish = finish;
        this.draw = draw;
    }

    private boolean force; //todo

    private Map<String,T> choices;

    private Function<T,String> display;

    private MessageChannel channel;

    private String text;

    private Consumer<T> finish;

    private Consumer<List<T>> draw;

    private HashMap<Member,String> avote = new HashMap<>();

    private static HashMap<MessageChannel, SimpleVote> voteHashMap = new HashMap<>();

    public static boolean vote(MessageChannel channel, Member member, String choose) {
        if(voteHashMap.containsKey(channel)){
            return voteHashMap.get(channel).voted(member,choose);
        }
        return false;
    }

    private boolean voted(Member member, String choose) {
        if(choices.containsKey(choose) || choose == null){
            if(avote.containsKey(member) && avote.get(member).equals(choose)){
                return false;
            }
            if(choose == null){
                avote.remove(member);
            }
            else avote.put(member,choose);
            updateVotes();
            return true;
        }
        return false;
    }

    private void updateVotes() {
        List<String> collect = avote.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        for (Map.Entry<String, Integer> entry : this.votes.entrySet()) {
            String i = entry.getKey();
            this.votes.put(i, Collections.frequency(collect, i));
        }
        if(this.force){
            end();
        }
        listMessage = listMessage.editMessage(buildMessage()).complete();
    }

    public void start(){
        if(voteHashMap.containsKey(channel)) voteHashMap.remove(channel).end();
        channel.sendMessage(text).queue();
        listMessage = channel.sendMessage(buildMessage()).complete();
        timeMessage = channel.sendMessage(format(currentTime)).complete();
        timer = new Timer();
        voteHashMap.put(this.channel,this);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateTime();
            }
        },0, TimeUnit.SECONDS.toMillis(1));
    }

    private Message listMessage;


    private String format(int time) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(new Date(time * 1000));
    }

    Message timeMessage;

    int currentTime;
    private void updateTime() {
        currentTime--;
        timeMessage.editMessage(format(currentTime)).queue(t -> this.timeMessage = t);
        if(currentTime == 0) end();
    }
    Timer timer;

    private void end(){
        if(!force)updateVotes();
        voteHashMap.remove(channel);
        timer.cancel();
        int max = votes.entrySet().stream().mapToInt(Map.Entry::getValue).max().orElse(-1);
        if(max == -1) {
            finish.accept(null);
            return;
        }
        val list = votes.entrySet().stream().filter(p -> p.getValue() == max).collect(Collectors.toList());
        if(list.size() != 1){
            draw.accept(list.stream().map(p -> choices.get(p.getKey())).collect(Collectors.toList()));
        }
        else if(list.get(0).getValue() == 0){
            finish.accept(null);
        }
        else finish.accept(choices.get(list.get(0).getKey()));
    }

    private HashMap<String,Integer> votes = new HashMap<>();

    private String buildMessage() {
        StringBuilder b = new StringBuilder();
        ochoices.forEach(sa -> {
            String s = display.apply(sa);
            String low = display.andThen(String::toLowerCase).apply(sa);
            b.append(s).append(" - ").append(" ( **").append(votes.getOrDefault(low, 0)).append("** Votes) \n");
            votes.putIfAbsent(low,0);
        });
        return b.toString();
    }

}
