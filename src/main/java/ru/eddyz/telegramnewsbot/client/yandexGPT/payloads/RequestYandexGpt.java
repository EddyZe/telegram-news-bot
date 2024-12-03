package ru.eddyz.telegramnewsbot.client.yandexGPT.payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestYandexGpt {

    private String modelUri;
    private CompletionOptions completionOptions;
    private List<PromtMessage> messages;

}
