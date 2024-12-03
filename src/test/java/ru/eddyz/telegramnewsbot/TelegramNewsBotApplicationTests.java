package ru.eddyz.telegramnewsbot;

import org.glassfish.grizzly.http.util.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import ru.eddyz.telegramnewsbot.parsers.ria.RiaParser;
import ru.eddyz.telegramnewsbot.parsers.ria.models.Item;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class TelegramNewsBotApplicationTests {


}
