package ua.team3.carsharingservice.telegram;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBot extends TelegramLongPollingBot {

    public TelegramBot(DefaultBotOptions options, String botToken) {
        super(options, botToken);
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var text = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();
            SendMessage sendMessage = new SendMessage(chatId.toString(), text);
            sendApiMethod(sendMessage);
        }
    }

    @Override
    public String getBotUsername() {
        return "Car Sharing Service";
    }
}
