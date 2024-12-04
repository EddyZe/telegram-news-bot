package ru.eddyz.telegramnewsbot.parsers.ria;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import ru.eddyz.telegramnewsbot.parsers.ria.models.Item;
import ru.eddyz.telegramnewsbot.parsers.ria.models.News;
import ru.eddyz.telegramnewsbot.parsers.ria.models.NewsCategoryURL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class RiaParser {


    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0";



    public List<Item> getNewsList(NewsCategoryURL categoryURL) throws IOException {
        List<Item> itemList = new ArrayList<>();
        Document doc = Jsoup.connect(categoryURL.toString())
                .userAgent(USER_AGENT)
                .get();

        var itemElements = doc.getElementsByClass("list-item__content");

        itemElements.forEach(i -> {
            var itemPost = i.getElementsByAttributeValueContaining("class", "list-item__title").get(0);
            var postUrl = itemPost.attr("href");
            var postName = itemPost.text();
            itemList.add(Item.builder()
                    .postUrl(postUrl)
                    .itemName(postName)
                    .build());
        });
        return itemList;
    }

    public News getInfoItem(Item item) throws IOException {
        Document doc = Jsoup.connect(item.getPostUrl())
                .userAgent(USER_AGENT)
                .get();

        StringBuilder resultText = new StringBuilder();

        var newsMedia = doc.getElementsByClass("article__header").get(0)
                .getElementsByClass("media__size")
                .get(0);

        String urlImage = null;
        try {
            Elements photoviewOpen = newsMedia.getElementsByClass("photoview__open");
            if (!photoviewOpen.isEmpty())
                urlImage = photoviewOpen.get(0)
                    .attr("data-photoview-src");
        } catch (NoSuchElementException e) {
            urlImage = newsMedia.getElementsByClass("video-player videoplayer-block")
                    .get(0)
                    .getElementsByAttributeValueContaining("class", "video-js")
                    .get(0)
                    .attr("poster");
        }

        var blockText = doc.getElementsByClass("article__body js-mediator-article mia-analytics");

        for (Element e : blockText) {
            var textBlocks = e.getElementsByClass("article__block");
            for (Element ee : textBlocks) {
                var text = ee.getElementsByClass("article__text");
                if (!text.isEmpty())
                    resultText.append(text.get(0).text()).append("\n\n");

                var quote = ee.getElementsByClass("article__quote");
                if (!quote.isEmpty())
                    resultText.append(quote.get(0).text()).append("\n\n");
            }
        }

        return News.builder()
                .title(item.getItemName())
                .text(resultText.toString())
                .imageUrl(urlImage == null ? "" : urlImage)
                .newsUrl(item.getPostUrl())
                .build();
    }


}
