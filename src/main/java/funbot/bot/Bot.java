package funbot.bot;

import funbot.db.ImgDb;
import funbot.db.UserDb;
import funbot.obj.State;
import funbot.obj.User;
import funbot.obj.AccessLvl;
import funbot.obj.ContentType;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bot extends TelegramLongPollingBot {
    private final String name;
    private long ownerId;
    private final ImgDb imgDb;
    private final UserDb userDb;
    public final MessageSender messageSender = new MessageSender(this);
    public final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    public HashMap<String, State> states;
    public ArrayList<Long> banList = new ArrayList<>();
    public ExecutorService executorService = Executors.newCachedThreadPool();

    public Bot(String name, String token, long ownerId, ImgDb imgDb, UserDb userDb) {
        super(token);
        this.name = name;
        this.ownerId = ownerId;
        this.imgDb = imgDb;
        this.userDb = userDb;
        setState();
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);
        executorService.execute(new UpdateProcessing(update, this));
    }
    private void setState(){
        states = Stream.of(Bot.class.getMethods())
                    .filter(method -> method.getReturnType().equals(State.StateBuilder.class))
                    .map(this::extractState)
                    .collect(Collectors.toMap(state -> {
                        assert state != null;
                        return state.getName();
                    }, state -> state, (x, y) -> x,HashMap::new));
    }
    private State extractState(Method method){
        Bot bot = this;
        try {
            State.StateBuilder stateBuilder = (State.StateBuilder) method.invoke(bot);
            return stateBuilder.build();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    public State getState(String string){
        return states.get(string);
    }
    public boolean isAdmin(Long id){
        return userDb.isAdmin(id);
    }
    public boolean isOwner(Long id){
        return userDb.isOwner(id);
    }
    public void addBan(Long id){
        banList.add(id);
    }
    public State.StateBuilder sendRandomImg(){
        return new State.StateBuilder()
                .name("random")
                .accessLvl(AccessLvl.ALL)
                .contentType(ContentType.TEXT)
                .action(context -> {
                    String name = context.bot.imgDb.getRandomName();
                    try {
                        FileInputStream stream = new FileInputStream(imgDb.getDir() + "\\" + name);
                        context.bot.messageSender.send(new InputFile(stream, name), context.getChatId().toString());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                });
    }
    public State.StateBuilder start(){
        return new State.StateBuilder()
                .name("start")
                .accessLvl(AccessLvl.ALL)
                .contentType(ContentType.TEXT)
                .action(context -> context.bot.messageSender.send("Hi, My name is Giovanni Giorgio, but everyone calls me Giorgio",
                        context.getChatId().toString()));
    }
    public State.StateBuilder ban(){
        return new State.StateBuilder()
                .name("ban")
                .accessLvl(AccessLvl.OWNER)
                .contentType(ContentType.TEXT)
                .nextState("banSecondState")
                .action(context -> context.bot.messageSender.send("Forward the message from the user",
                        context.getUserId().toString()));
    }
    public State.StateBuilder ban2(){
        return new State.StateBuilder()
                .name("banSecondState")
                .accessLvl(AccessLvl.OWNER)
                .contentType(ContentType.FORWARD_MSG)
                .action(context -> {
                    Long id = Utils.getUserIdFromForwardMsg(context.getUpdate());
                    context.bot.addBan(id);
                    context.bot.messageSender.send("User " + id.toString() + " was banned", context.getUserId().toString());
                });
    }
}
