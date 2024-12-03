package ru.eddyz.telegramnewsbot.parsers.ria.models;



public enum NewsCategoryURL {
    ECONOMY("https://ria.ru/economy/"), POLITICS("https://ria.ru/politics/");

    private final String url;

    NewsCategoryURL(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return url;
    }
}
