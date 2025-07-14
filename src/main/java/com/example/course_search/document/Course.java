package com.example.course_search.document;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import lombok.Data;

@Data
@Document(indexName = "courses")
public class Course {

    @Id
    private String id;
    @Field(type = FieldType.Text, name = "title")
    private String title;
    @Field(type = FieldType.Text, name = "description")
    private String description;
    @Field(type = FieldType.Keyword, name = "category")
    private String category;
    @Field(type = FieldType.Keyword, name = "type")
    private String type;
    @Field(type = FieldType.Text, name = "gradeRange")
    private String gradeRange;
    @Field(type = FieldType.Integer, name = "minAge")
    private int minAge;
    @Field(type = FieldType.Integer, name = "maxAge")
    private int maxAge;
    @Field(type = FieldType.Double, name = "price")
    private double price;
    @Field(type = FieldType.Date, name = "nextSessionDate")
    private Instant nextSessionDate;
}