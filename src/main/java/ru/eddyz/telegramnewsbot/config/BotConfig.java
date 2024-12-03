package ru.eddyz.telegramnewsbot.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.eddyz.telegramnewsbot.bot.TelegramBot;

@Configuration
@EnableScheduling
public class BotConfig {


    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(telegramBot);
        return api;
    }


    @Bean
    public RestClient restClient(@Value("${yandex.gpt.token}") String yandexGPTToken,
                                 @Value("${yandex.gpt.folder.id}") String yandexFolderId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Api-Key " + yandexGPTToken);
        headers.add("x-folder-id", yandexFolderId);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return RestClient.builder()
                .baseUrl("https://llm.api.cloud.yandex.net/foundationModels/v1/")
                .defaultHeaders(httpHeaders -> httpHeaders.addAll(headers))
                .build();
    }
}
