// In src/main/java/com/example/course_search/controller/CourseController.java

package com.example.course_search.controller;

import com.example.course_search.document.Course;
import com.example.course_search.service.CourseSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
public class CourseController {

    private final CourseSearchService courseSearchService;

    @Autowired
    public CourseController(CourseSearchService courseSearchService) {
        this.courseSearchService = courseSearchService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> searchCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false, defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        SearchHits<Course> searchHits = courseSearchService.searchCourses(
                q, minAge, maxAge, category, type, minPrice, maxPrice, startDate, sort, page, size);

        List<Course> courses = searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

        Map<String, Object> response = Map.of(
                "total", searchHits.getTotalHits(),
                "courses", courses);

        return ResponseEntity.ok(response);
    }
}