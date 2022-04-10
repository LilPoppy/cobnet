package com.cobnet.spring.boot.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

//TODO queue buffer
public class RandomImageProvider {

    @Async
    public Optional<URL> getFromGeneratorMix() throws IOException {

        Optional<String> url = getImageElementsFromUrl("https://www.generatormix.com/random-image-generator").stream().filter(element -> element.absUrl("alt").split(",").length > 1).map(element -> element.absUrl("data-src").toString()).findFirst();

        if(url.isPresent()) {

            return Optional.empty();
        }

        return Optional.of(new URL(url.get()));
    }

    @Async
    public List<String> getFromCoolGenerator() throws IOException {

        return getImageElementsFromUrl("https://www.coolgenerator.com/random-image-generator").stream().filter(element -> element.absUrl("alt").split(",").length > 1).map(element -> element.absUrl("src")).toList();
    }

    @Async
    public Elements getImageElementsFromUrl(String url) throws IOException {

        Document document = Jsoup.connect("https://www.coolgenerator.com/random-image-generator").get();

        return document.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
    }

    @Async
    public Optional<URL> getFromPicsum(int width, int height) throws IOException {

        URLConnection connection = new URL("https://picsum.photos/" + width +"/" + height).openConnection();
        connection.connect();
        connection.getInputStream();

        return Optional.ofNullable(connection.getURL());
    }
}
