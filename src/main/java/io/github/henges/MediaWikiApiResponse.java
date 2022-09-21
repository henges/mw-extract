package io.github.henges;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MediaWikiApiResponse {

    @JsonProperty("continue")
    private final MediaWikiContinue continueResponse;
    @JsonProperty("query")
    private final MediaWikiQuery query;

    public MediaWikiApiResponse(
            @JsonProperty("continue") final MediaWikiContinue continueResponse,
            @JsonProperty("query") final MediaWikiQuery query) {
        this.continueResponse = continueResponse;
        this.query = query;
    }

    public MediaWikiContinue getContinueResponse() {
        return continueResponse;
    }

    public MediaWikiQuery getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return "MediaWikiApiResponse{" +
                "continueResponse=" + continueResponse +
                ", query=" + query +
                '}';
    }

    public static class MediaWikiQuery {

        @JsonProperty("search")
        private final List<MediaWikiSearchResult> results;

        public MediaWikiQuery(
                @JsonProperty("search") final List<MediaWikiSearchResult> results) {
            this.results = results;
        }

        public List<MediaWikiSearchResult> getResults() {
            return results;
        }

        @Override
        public String toString() {
            return "MediaWikiQuery{" +
                    "results=" + results +
                    '}';
        }
    }

    public static class MediaWikiSearchResult {

        @JsonProperty("ns")
        private final int namespace;
        @JsonProperty("title")
        private final String title;
        @JsonProperty("pageid")
        private final long pageId;
        @JsonProperty("size")
        private final long size;

        @JsonCreator
        public MediaWikiSearchResult(
                @JsonProperty("ns") final int namespace,
                @JsonProperty("title") final String title,
                @JsonProperty("pageid") final long pageId,
                @JsonProperty("size") final long size) {
            this.namespace = namespace;
            this.title = title;
            this.pageId = pageId;
            this.size = size;
        }

        public int getNamespace() {
            return namespace;
        }

        public String getTitle() {
            return title;
        }

        public long getPageId() {
            return pageId;
        }

        public long getSize() {
            return size;
        }

        @Override
        public String toString() {
            return "MediaWikiSearchResult{" +
                    "namespace=" + namespace +
                    ", title='" + title + '\'' +
                    ", pageId=" + pageId +
                    ", size=" + size +
                    '}';
        }
    }

    public static class MediaWikiContinue {

        @JsonProperty("sroffset")
        private final long searchResultOffset;

        @JsonCreator
        public MediaWikiContinue(@JsonProperty("sroffset") long searchResultOffset) {
            this.searchResultOffset = searchResultOffset;
        }

        public long getSearchResultOffset() {
            return searchResultOffset;
        }

        @Override
        public String toString() {
            return "MediaWikiContinue{" +
                    "searchResultOffset=" + searchResultOffset +
                    '}';
        }
    }
}
