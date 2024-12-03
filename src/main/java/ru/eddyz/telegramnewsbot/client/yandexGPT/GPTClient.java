package ru.eddyz.telegramnewsbot.client.yandexGPT;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.eddyz.telegramnewsbot.client.yandexGPT.payloads.RequestYandexGpt;
import ru.eddyz.telegramnewsbot.client.yandexGPT.payloads.ResponseYandexGpt;

@Component
public class GPTClient {

    private final RestClient restClient;

    public GPTClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ResponseYandexGpt sendPromt(RequestYandexGpt promt) {
        return restClient.post()
                .uri("completion")
                .body(promt)
                .retrieve()
                .body(ResponseYandexGpt.class);
    }


}
