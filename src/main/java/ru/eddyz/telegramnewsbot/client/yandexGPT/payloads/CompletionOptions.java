package ru.eddyz.telegramnewsbot.client.yandexGPT.payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CompletionOptions {

    private Boolean stream;
    private Double temperature;
    private Integer maxTokens;

}
