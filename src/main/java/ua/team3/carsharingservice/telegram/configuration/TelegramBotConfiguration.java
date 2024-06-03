package ua.team3.carsharingservice.telegram.configuration;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ua.team3.carsharingservice.telegram.TelegramBot;

@Profile("!test")
@Configuration
public class TelegramBotConfiguration {

    @Bean
    @SneakyThrows
    public TelegramBot telegramBot(
            @Value("${bot.token}") String token,
            @Value("${bot.name}") String botName,
            @Value("${admin.chat.id}") String adminChatId,
            TelegramBotsApi telegramBotsApi
    ) {
        var bot = new TelegramBot(token, botName, adminChatId);
        telegramBotsApi.registerBot(bot);
        return bot;
    }

    @Bean
    @SneakyThrows
    public TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi(DefaultBotSession.class);
    }
}
