package io.github.henges;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.henges.MediaWikiApiResponse.MediaWikiSearchResult;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MediaWikiSearch {

    private final static Logger log = LoggerFactory.getLogger(MediaWikiSearch.class);

    private static final String WIKTIONARY_API_URL = "https://en.wiktionary.org/w/api.php";
    private static final int LIMIT = 500;
    private static final int WIKTIONARY_MAXIMUM_OFFSET = 10000;

    private final CloseableHttpClient client;

    private final ObjectMapper mapper;

    public MediaWikiSearch() {

        client = HttpClientBuilder.create().build();
        mapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            log.error("You must supply an argument.");
            System.exit(1);
        }

        final String searchString = String.join(" ", args);

        log.info("Beginning Wiktionary search for query: \"{}\"", searchString);

        final MediaWikiSearch extract = new MediaWikiSearch();
        final List<String> results = new ArrayList<>();
        long offset = 0;

        while (offset < WIKTIONARY_MAXIMUM_OFFSET) {

            final MediaWikiApiResponse response = extract.search(searchString, offset);

            final List<String> titles = response.getQuery().getResults().stream()
                    .map(MediaWikiSearchResult::getTitle)
                    .collect(Collectors.toList());

            results.addAll(titles);

            log.info("Got {} results from Wiktionary ({} total)", titles.size(), results.size());

            if (response.getContinueResponse() == null) {
                break;
            }
            offset = response.getContinueResponse().getSearchResultOffset();
        }

        if (offset >= WIKTIONARY_MAXIMUM_OFFSET) {
            log.warn("Retrieved {} results from Wiktionary but current offset ({}) has exceeded the Wiktionary " +
                            "maximum of {}",
                    results.size(), offset, WIKTIONARY_MAXIMUM_OFFSET);
        } else {
            log.info("Retrieved all search results from Wiktionary, result set size: {}", results.size());
        }

        final String outfileName = "results-for-search-" + ThreadLocalRandom.current().nextInt() + ".csv";

        try (PrintWriter out = new PrintWriter(outfileName, "UTF-8")) {

            results.forEach(out::println);
        }

        log.info("Successfully wrote results to {}. Done.", outfileName);
    }

    public MediaWikiApiResponse search(final String searchString, final long offset) {

        final ClassicRequestBuilder builder = ClassicRequestBuilder
                        .get()
                        .setUri(WIKTIONARY_API_URL)
                        .addParameter("action", "query")
                        .addParameter("list", "search")
                        .addParameter("format", "json")
                        .addParameter("srsearch", searchString)
                        .addParameter("srlimit", String.valueOf(LIMIT))
                        .addParameter("sroffset", String.valueOf(offset));

        log.info("Querying Wiktionary with offset {}", offset);

        CloseableHttpResponse response = null;

        try {
            response = client.execute(builder.build());
            return mapper.readValue(response.getEntity().getContent(), MediaWikiApiResponse.class);

        } catch (IOException e) {
            log.error("Failed to execute request!", e);
            throw new IOError(e);
        }
    }


}
