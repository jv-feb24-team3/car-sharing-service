package ua.team3.carsharingservice.telegram;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private final String botName;
    @Value("${bot.token}")
    private final String botToken;

    public TelegramBot(
            @Value("${bot.token}") String botToken,
            @Value("${bot.name}") String botName
    ) {
        super(botToken);
        this.botName = botName;
        this.botToken = botToken;
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
        return botName;
    }
}
