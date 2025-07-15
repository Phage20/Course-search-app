// src/main/java/com/example/course_search/config/DataLoader.java
package com.example.course_search.config;

import com.example.course_search.document.Course; // Ensure this import matches your Course class location
import com.example.course_search.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Required for Instant deserialization
import jakarta.annotation.PostConstruct; // Correct import for PostConstruct in Spring Boot 3+
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration // Marks this class as a Spring configuration component
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @PostConstruct // Ensures this method runs after dependency injection is complete
    public void init() {
        log.info("Starting data loading process...");
        try {
            // Ensure the Elasticsearch index exists and has the correct mapping
            IndexOperations indexOperations = elasticsearchOperations.indexOps(Course.class);
            if (!indexOperations.exists()) {
                log.info("Creating Elasticsearch index 'courses' for Course document.");
                indexOperations.create(); // Create the index
                indexOperations.putMapping(indexOperations.createMapping(Course.class)); // Put the mapping based on
                                                                                         // Course.java annotations
                log.info("Index and mapping created successfully.");
            } else {
                log.info(
                        "Elasticsearch index 'courses' already exists. Skipping creation, but ensuring mapping is up-to-date (optional).");
                // In a real application, you might want to compare mappings or explicitly
                // update.
                // For this assignment, checking existence is sufficient to avoid errors.
            }

            long existingCount = courseRepository.count();
            if (existingCount == 0) { // Only load data if the index is empty
                log.info("Elasticsearch 'courses' index is empty. Loading sample data...");
                // Register JavaTimeModule to correctly deserialize Instant (ISO-8601 dates)
                objectMapper.registerModule(new JavaTimeModule());

                // Read sample-courses.json from src/main/resources
                ClassPathResource resource = new ClassPathResource("sample-courses.json");
                try (InputStream inputStream = resource.getInputStream()) {
                    List<Course> courses = objectMapper.readValue(inputStream, new TypeReference<List<Course>>() {
                    });
                    log.info("Read {} courses from sample-courses.json", courses.size());

                    // Bulk index the courses
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