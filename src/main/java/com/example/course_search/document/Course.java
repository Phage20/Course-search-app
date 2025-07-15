package com.example.course_search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "courses")
public class Course {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "title", analyzer = "english")
    private String title;

    // This is the new field for autocomplete suggestions
    @CompletionField(analyzer = "standard", searchAnalyzer = "standard")
    private Completion titleSuggest;

    @Field(type = FieldType.Text, name = "description", analyzer = "english")
    private String description;

    @Field(type = FieldType.Keyword, name = "category")
    private String category;

    @Field(type = FieldType.Keyword, name = "type")
    private String type;

    @Field(type = FieldType.Keyword, name = "gradeRange")
    private String gradeRange;

    @Field(type = FieldType.Integer, name = "minAge")
    private Integer minAge;

    @Field(type = FieldType.Integer, name = "maxAge")
    private Integer maxAge;

    @Field(type = FieldType.Double, name = "price")
    private Double price;

    @Field(type = FieldType.Date, name = "nextSessionDate")
    private Instant nextSessionDate;
}