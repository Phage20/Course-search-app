// In src/main/java/com/example/course_search/repository/CourseRepository.java

package com.example.course_search.repository;

import com.example.course_search.document.Course;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends ElasticsearchRepository<Course, String> {
}