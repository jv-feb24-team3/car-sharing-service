package ua.team3.carsharingservice.telegram.configuration;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ua.team3.carsharingservice.telegram.TelegramBot;

@Configuration
public class TelegramBotConfiguration {

    @Bean
    @SneakyThrows
    public TelegramBot telegramBot(
            @Value("${bot.token}") String token,
            TelegramBotsApi telegramBotsApi
    ) {
        var botOptions = new DefaultBotOptions();
        var bot = new TelegramBot(botOptions, token);
        telegramBotsApi.registerBot(bot);
        return bot;
    }

    @Bean
    @SneakyThrows
    public TelegramBotsApi telegramBotsApi() {
        return new TelegramBotsApi(DefaultBotSession.class);
    }
}
