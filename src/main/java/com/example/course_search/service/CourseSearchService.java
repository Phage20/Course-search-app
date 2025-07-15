package com.example.course_search.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import co.elastic.clients.json.JsonData;
import com.example.course_search.document.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private static final String SUGGEST_TITLE = "suggest-title";

    @Autowired
    public CourseSearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public SearchHits<Course> searchCourses(
            String keyword, Integer minAge, Integer maxAge, String category, String type,
            Double minPrice, Double maxPrice, Instant startDate, String sort, int page, int size) {
        List<Query> filterQueries = new ArrayList<>();
        if (minAge != null) {
            filterQueries.add(Query.of(q -> q.range(r -> r.field("minAge").gte(JsonData.of(minAge)))));
        }
        if (maxAge != null) {
            filterQueries.add(Query.of(q -> q.range(r -> r.field("maxAge").lte(JsonData.of(maxAge)))));
        }
        if (StringUtils.hasText(category)) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("category").value(category))));
        }
        if (StringUtils.hasText(type)) {
            filterQueries.add(Query.of(q -> q.term(t -> t.field("type").value(type))));
        }
        if (minPrice != null || maxPrice != null) {
            filterQueries.add(Query.of(q -> q.range(r -> r
                    .field("price")
                    .gte(JsonData.of(minPrice != null ? minPrice : 0.0))
                    .lte(JsonData.of(maxPrice != null ? maxPrice : Double.MAX_VALUE)))));
        }
        if (startDate != null) {
            filterQueries.add(
                    Query.of(q -> q.range(r -> r.field("nextSessionDate").gte(JsonData.of(startDate.toString())))));
        }
        NativeQuery nativeQuery = NativeQuery.builder()
                .withPageable(PageRequest.of(page, size))
                .withQuery(buildQuery(keyword, filterQueries))
                .withSort(buildSort(sort))
                .build();
        return elasticsearchOperations.search(nativeQuery, Course.class);
    }

    private Query buildQuery(String keyword, List<Query> filterQueries) {
        return Query.of(q -> q
                .bool(b -> {
                    if (StringUtils.hasText(keyword)) {
                        b.must(m -> m
                                .multiMatch(mm -> mm
                                        .fields("title", "description")
                                        .query(keyword)
                                        .fuzziness("AUTO")));
                    }
                    if (!filterQueries.isEmpty()) {
                        b.filter(filterQueries);
                    }
                    if (!StringUtils.hasText(keyword) && filterQueries.isEmpty()) {
                        b.must(m -> m.matchAll(ma -> ma));
                    }
                    return b;
                }));
    }

    private Sort buildSort(String sort) {
        if ("priceAsc".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.ASC, "price");
        } else if ("priceDesc".equalsIgnoreCase(sort)) {
            return Sort.by(Sort.Direction.DESC, "price");
        }
        return Sort.by(Sort.Direction.ASC, "nextSessionDate");
    }

    // --- ENTIRE METHOD REWRITTEN FOR SIMPLICITY AND CORRECTNESS ---
    public List<String> suggestCourses(String query) {
        Suggester suggester = Suggester.of(s -> s
                .suggesters(SUGGEST_TITLE, FieldSuggester.of(fs -> fs
                        .prefix(query)
                        .completion(CompletionSuggester.of(cs -> cs
                                .field("titleSuggest")
                                .skipDuplicates(true)
                                .size(10))))));

        SearchHits<Course> searchHits = elasticsearchOperations.search(
                NativeQuery.builder().withSuggester(suggester).build(), Course.class);

        Suggest suggest = searchHits.getSuggest();
        if (suggest == null) {
            return Collections.emptyList();
        }

        List<String> suggestions = new ArrayList<>();
        // This is a simpler and safer way to get the suggestion text
        suggest.getSuggestions().forEach(suggestion -> {
            suggestion.getEntries().forEach(entry -> {
                entry.getOptions().forEach(option -> {
                    suggestions.add(option.getText());
                });
            });
        });

        return suggestions;
    }
}