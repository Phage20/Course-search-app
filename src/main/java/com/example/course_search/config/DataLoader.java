// src/main/java/com/example/course_search/config/DataLoader.java
package com.example.course_search.config;

import com.example.course_search.document.Course;
import com.example.course_search.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void init() {
        log.info("Starting data loading process...");
        try {
            IndexOperations indexOperations = elasticsearchOperations.indexOps(Course.class);
            if (!indexOperations.exists()) {
                log.info("Creating Elasticsearch index 'courses' for Course document.");
                indexOperations.create();
                indexOperations.putMapping(indexOperations.createMapping(Course.class));
                log.info("Index and mapping created successfully.");
            } else {
                log.info("Elasticsearch index 'courses' already exists.");
            }

            long existingCount = courseRepository.count();
            if (existingCount == 0) {
                log.info("Elasticsearch 'courses' index is empty. Loading sample data...");
                objectMapper.registerModule(new JavaTimeModule());

                ClassPathResource resource = new ClassPathResource("sample-courses.json");
                try (InputStream inputStream = resource.getInputStream()) {
                    List<Course> courses = objectMapper.readValue(inputStream, new TypeReference<List<Course>>() {
                    });
                    log.info("Read {} courses from sample-courses.json", courses.size());

                    // --- NEW ---
                    // Populate the suggest field for each course
                    for (Course course : courses) {
                        course.setTitleSuggest(new Completion(new String[] { course.getTitle() }));
                    }
                    // --- END NEW ---

                    Iterable<Course> savedCourses = courseRepository.saveAll(courses);
                    long savedCount = 0;
                    for (Course course : savedCourses) {
                        savedCount++;
                    }
                    log.info("Successfully indexed {} courses into Elasticsearch.", savedCount);
                }
            } else {
                log.info("Elasticsearch 'courses' index already contains {} documents. Skipping sample data load.",
                        existingCount);
            }
        } catch (IOException e) {
            log.error("Error reading sample data file: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("An unexpected error occurred during data loading: {}", e.getMessage(), e);
        }
    }
}