package ru.eddyz.telegramnewsbot.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.eddyz.telegramnewsbot.client.yandexGPT.GPTClient;
import ru.eddyz.telegramnewsbot.client.yandexGPT.payloads.CompletionOptions;
import ru.eddyz.telegramnewsbot.client.yandexGPT.payloads.PromtMessage;
import ru.eddyz.telegramnewsbot.client.yandexGPT.payloads.RequestYandexGpt;
import ru.eddyz.telegramnewsbot.client.yandexGPT.payloads.ResponseYandexGpt;
import ru.eddyz.telegramnewsbot.domain.entities.NewsUrl;
import ru.eddyz.telegramnewsbot.parsers.ria.RiaParser;
import ru.eddyz.telegramnewsbot.parsers.ria.models.Item;
import ru.eddyz.telegramnewsbot.parsers.ria.models.News;
import ru.eddyz.telegramnewsbot.parsers.ria.models.NewsCategoryURL;
import ru.eddyz.telegramnewsbot.repositories.NewsRepository;
import ru.eddyz.telegramnewsbot.util.BrowserUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class NewsService {

    private final AbsSender sender;
    private final RiaParser riaParser;
    private final BrowserUtil browserUtil;
    private final GPTClient gptClient;
    private final Long groupId;
    private final NewsRepository newsRepository;
    private final String modelUri;


    public NewsService(@Lazy AbsSender sender,
                       RiaParser riaParser,
                       BrowserUtil browserUtil, GPTClient gptClient,
                       @Value("${telegram.bot.group.id}") Long groupId,
                       NewsRepository newsRepository,
                       @Value("${yandex.gpt.model.uri}") String modelUri) {
        this.sender = sender;
        this.riaParser = riaParser;
        this.browserUtil = browserUtil;
        this.gptClient = gptClient;
        this.groupId = groupId;
        this.newsRepository = newsRepository;
        this.modelUri = modelUri;
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkAndSendNews() {
        try {
            List<Item> allNewsList = new ArrayList<>();

            for (NewsCategoryURL c : NewsCategoryURL.values()) {
                allNewsList.addAll(riaParser.getNewsList(c));
            }

            for (Item item : allNewsList.reversed()) {
                if (newsRepository.findByUrl(item.getPostUrl()).isEmpty()) {
                    sendNews(riaParser.getInfoItem(item));
                    newsRepository.save(NewsUrl.builder()
                            .url(item.getPostUrl())
                            .build());
                }
            }
        } catch (IOException e) {
            log.error("Ошибка при получении новостей {}", e.toString());
        }
    }

    private void sendNews(News news) {
        Optional<File> image;
        try {
            image = browserUtil.downlandImage(new URI(news.getImageUrl()).toURL());
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("Ошибка при загрузки фото!");
            return;
        }

        if (image.isEmpty())
            return;


        ResponseYandexGpt responseText = gptClient.sendPromt(
                generateRequestYandexGpt("перескажи текст в один абзац", news.getText()));
        ResponseYandexGpt responseTitle = gptClient.sendPromt(
                generateRequestYandexGpt("перефразируй текст в одну строку", news.getTitle()));

        String caption = getCaption(news, responseTitle, responseText);


        try {

            sender.execute(SendPhoto.builder()
                    .caption(caption)
                    .chatId(groupId)
                    .parseMode(ParseMode.HTML)
                    .photo(new InputFile(image.get()))
                    .build());
            Files.deleteIfExists(image.get().toPath());
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке новости в группу. {}", e.toString());
        } catch (IOException e) {
            log.error("Ошибка при удалении файла.");
        }
    }

    private String getCaption(News news, ResponseYandexGpt responseTitle, ResponseYandexGpt responseText) {
        String title = responseTitle.getResult().getAlternatives().getFirst().getMessage().getText();
        String text = responseText.getResult().getAlternatives().getFirst().getMessage().getText();


        if (text.contains("В интернете есть много сайтов с информацией на эту тему. [Посмотрите, что нашлось в поиске]")) {
            text = news.getText().split("\n\n")[0];

            String rex = "^[А-ЯЁ][^.]*РИА Новости.";
            Pattern pattern = Pattern.compile(rex);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find())
                text = text.substring(text.indexOf(".") + 1)
                        .trim();
        }

        if (title.contains("В интернете есть много сайтов с информацией на эту тему. [Посмотрите, что нашлось в поиске]"))
            title = news.getTitle();

        return "<b>%s</b>\n\n%s".formatted(title, text);
    }

    private RequestYandexGpt generateRequestYandexGpt(String promt, String textNews) {
        PromtMessage messageSystem = PromtMessage.builder()
                .role("system")
                .text(promt)
                .build();
        PromtMessage messageUser = PromtMessage.builder()
                .text(textNews)
                .role("user")
                .build();

        CompletionOptions completionOptions = CompletionOptions.builder()
                .maxTokens(2000)
                .stream(false)
                .temperature(0.6)
                .build();

        return RequestYandexGpt.builder()
                .modelUri(modelUri)
                .messages(List.of(messageSystem, messageUser))
                .completionOptions(completionOptions)
                .build();
    }
}
