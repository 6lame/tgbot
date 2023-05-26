package funbot.bot;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageSender {
    private final Bot bot;

    public MessageSender(Bot bot){
        this.bot = bot;
    }

    public void send(String message, String chatId) {
        SendMessage sm = SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .build();

        try {
            bot.executeAsync(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void send(InputFile file, String chatId) {
        SendDocument sd = SendDocument.builder()
                .document(file)
                .chatId(chatId)
                .build();
        try {
            bot.execute(sd);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
