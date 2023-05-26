package funbot.obj;

import funbot.bot.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Context {
    public User user;
    public State state;
    public Bot bot;

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public Update update;

    public Context(User user, Bot bot, Update update) {
        this.user = user;
        this.bot = bot;
        this.update = update;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
    public Long getUserId(){
        return user.getId();
    }
    public Long getChatId(){
        return update.getMessage().getChatId();
    }
}
