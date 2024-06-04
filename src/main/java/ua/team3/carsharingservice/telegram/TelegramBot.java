package ua.team3.carsharingservice.telegram;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private final String botName;
    @Value("${bot.token}")
    private final String botToken;
    @Value("${admin.chat.id}")
    private final String adminChatId;

    public TelegramBot(
            @Value("${bot.token}") String botToken,
            @Value("${bot.name}") String botName,
            @Value("${admin.chat.id}") String adminChatId
    ) {
        super(botToken);
        this.botName = botName;
        this.botToken = botToken;
        this.adminChatId = adminChatId;
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        SendMessage response = new SendMessage();
        response.setText("Hello team!");
        response.setChatId(update.getMessage().getChatId());
        execute(response);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    public String getAdminChatId() {
        return adminChatId;
    }

}
