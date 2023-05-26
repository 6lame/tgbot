package funbot.bot;

import funbot.obj.ContentType;
import funbot.obj.Context;
import funbot.obj.User;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.stream.Stream;

public class UpdateProcessing implements Runnable {
    private final Bot bot;
    private final Update update;

    public UpdateProcessing(Update update, Bot bot) {
        this.bot = bot;
        this.update = update;
    }

    @Override
    public void run() {
        Stream.of(update)
                .filter(this::isBanned)
                .map(this::addUser)
                .map(this::getContext)
                .filter(this::isState)
                .map(this::setState)
                .filter(this::checkAccessLvl)
                .filter(this::checkContentType)
                .map(this::execute)
                .forEach(context -> {});
    }

    boolean isBanned(Update update) {
        Long id = Utils.getUserId(update);
        if (bot.banList.contains(id)) {
            bot.messageSender.send("You are banned", id.toString());
            return false;
        }
        return true;
    }

    Context getContext(Update update) {
        Long id = Utils.getUserId(update);
        return new Context(bot.users.get(id), bot, update);


    }

    Update addUser(Update update) {
        Long id = Utils.getUserId(update);
        if (!bot.users.containsKey(id)) {
            bot.users.put(id, new User(id));
        }
        return update;
    }

    boolean isState(Context context) {
        if (bot.states.containsKey(context.getUser().getState())) {
            return true;
        }
        StringBuffer command = new StringBuffer();
        if (Utils.getCommand(update, command)) {
            return bot.states.containsKey(command.toString());
        }
        bot.messageSender.send("Not found", context.getUser().getId().toString());
        return false;
    }

    Context setState(Context context) {
        if (context.getUser().getState() != null) {
            context.setState(bot.getState(context.getUser().getState()));
        } else {
            StringBuffer command = new StringBuffer();
            Utils.getCommand(update, command);
            context.setState(bot.getState(command.toString()));
        }
        return context;
    }

    boolean checkAccessLvl(Context context) {
        boolean isOk = false;
        switch (context.getState().getAccessLvl()) {
            case ALL:
                isOk = true;
                break;
            case ADMIN:
                isOk = bot.isAdmin(context.getUserId());
                break;
            case OWNER:
                isOk = bot.isOwner(context.getUserId());
                break;
        }
        if (!isOk) {
            bot.messageSender.send("You don't have access", context.getUserId().toString());
        }
        return isOk;
    }

    boolean checkContentType(Context context) {
        boolean isOk = false;
        for (ContentType type : context.getState().getContentType()){
            switch (type) {
                case TEXT:
                    isOk = update.hasMessage() & update.getMessage().hasText();
                    break;
                case IMAGE:
                    isOk = update.hasMessage() & update.getMessage().hasPhoto();
                    break;
                case FORWARD_MSG:
                    isOk = update.hasMessage() & (update.getMessage().getForwardFrom() != null);
                    break;
            }
            if (isOk){
                break;
            }
        }
        if (!isOk) {
            bot.messageSender.send("Invalid type", context.getUserId().toString());
        }
        return isOk;
    }

    Context execute(Context context) {
        context.getState().getAction().accept(context);
        context.getUser().setState(context.getState().getNextState());
        return context;
    }


}
