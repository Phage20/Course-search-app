package com.example.course_search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "courses") // Maps this class to an Elasticsearch index named "courses"
public class Course { // Class name is Course as requested

    @Id // Marks this field as the ID of the Elasticsearch document
    private String id;

    @Field(type = FieldType.Text, name = "title", analyzer = "english") // Text field with English analyzer for
                                                                        // full-text search
    private String title;

    @Field(type = FieldType.Text, name = "description", analyzer = "english") // Text field with English analyzer for
                                                                              // full-text search
    private String description;

    @Field(type = FieldType.Keyword, name = "category") // Keyword for exact matching/filtering
    private String category;

    @Field(type = FieldType.Keyword, name = "type") // Keyword for exact matching/filtering
    private String type;

    @Field(type = FieldType.Keyword, name = "gradeRange") // Changed to Keyword for exact filtering
    private String gradeRange;

    @Field(type = FieldType.Integer, name = "minAge") // Integer for numeric age field
    private Integer minAge; // Using Integer wrapper to allow nulls

    @Field(type = FieldType.Integer, name = "maxAge") // Integer for numeric age field
    private Integer maxAge; // Using Integer wrapper

    @Field(type = FieldType.Double, name = "price") // Double for decimal price field
    private Double price; // Using Double wrapper

    @Field(type = FieldType.Date, name = "nextSessionDate") // Date field for ISO-8601 date-time
    private Instant nextSessionDate;
}