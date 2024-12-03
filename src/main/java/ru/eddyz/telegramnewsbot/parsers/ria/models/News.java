package ru.eddyz.telegramnewsbot.parsers.ria.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class News {

    private String imageUrl;
    private String title;
    private String text;
    private String newsUrl;

    @Override
    public String toString() {
        return "News{" +
               "imageUrl='" + imageUrl + '\'' +
               ", title='" + title + '\'' +
               ", text='" + text + '\'' +
               ", newsUrl='" + newsUrl + '\'' +
               '}';
    }
}
