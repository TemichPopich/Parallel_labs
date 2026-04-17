package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static utils.Utils.BASE_LINK_PARSE_PATTERN;
import static utils.Utils.PAGE_SIZE;
import static utils.Utils.PARSE_ERROR_MESSAGE;
import static utils.Utils.SUB_LINK_PARSE_PATTERN;

public class Parser {
    public List<String> parseLinks(final String link) {
        Elements elements = getDocOpt(link)
            .map(doc -> doc.select(BASE_LINK_PARSE_PATTERN))
            .filter(el -> el.size() >= PAGE_SIZE)
            .orElseThrow(() -> new RuntimeException(PARSE_ERROR_MESSAGE));

        return elements.stream().map(el -> el.attr("href")).toList();
    }

    public String parseProducer(final String link) {
        return getDocOpt(link)
            .map(doc -> Objects.requireNonNull(doc.selectFirst(SUB_LINK_PARSE_PATTERN)).text())
            .orElseThrow(() -> new RuntimeException(PARSE_ERROR_MESSAGE));
    }

    private Optional<Document> getDocOpt(final String link) {
        Optional<Document> docOpt;
        try {
            docOpt = Optional.of(Jsoup.connect(link)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36...")
                .timeout(10000)
                .get());
        } catch (IOException e) {
            throw new RuntimeException(PARSE_ERROR_MESSAGE);
        }
        return docOpt;
    }
}
