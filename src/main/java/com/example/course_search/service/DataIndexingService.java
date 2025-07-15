// In src/main/java/com/example/course_search/service/DataIndexingService.java

package com.example.course_search.service;

import com.example.course_search.document.Course;
import com.example.course_search.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class DataIndexingService {

    private static final Logger logger = LoggerFactory.getLogger(DataIndexingService.class);
    private final CourseRepository courseRepository;

    @Autowired
    public DataIndexingService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @PostConstruct
    public void indexData() {
        // Check if data is already indexed to prevent duplication on restart
        if (courseRepository.count() > 0) {
            logger.info("Course data has already been indexed. Skipping data load.");
            return;
        }

        try {
            // Use ClassPathResource to read the file from the classpath
            ClassPathResource resource = new ClassPathResource("sample-courses.json");
            InputStream inputStream = resource.getInputStream();

            // Create an ObjectMapper and register the JavaTimeModule to handle Instant
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Map the JSON to a list of Course objects
            List<Course> courses = objectMapper.readValue(inputStream, new TypeReference<List<Course>>() {
            });

            // Use the repository to save the course documents in bulk
            courseRepository.saveAll(courses);

            logger.info("Successfully indexed {} course documents.", courses.size());

        } catch (Exception e) {
            logger.error("An error occurred while indexing data.", e);
        }
    }
}