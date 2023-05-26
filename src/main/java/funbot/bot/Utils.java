package funbot.bot;

import org.telegram.telegrambots.meta.api.objects.Update;

public class Utils {
    public static Long getUserId(Update update){
        return update.getMessage().getFrom().getId();
    }
    public static Long getUserIdFromForwardMsg(Update update){
        return update.getMessage().getForwardFrom().getId();
    }
    public static boolean getCommand(Update update, StringBuffer string){
        if (update.hasMessage() & update.getMessage().hasText()){
            String text = update.getMessage().getText();
            if (text.startsWith("/")){
                string.append(text.substring(1));
                return true;
            }
        }
        return false;
    }
}
