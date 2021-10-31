package com.example.graphql.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

import com.example.graphql.annotation.GraphQLEntity;

@Data
@GraphQLEntity
public class Product {

    private Long id;

    private String title;
    private String description;
    private BigDecimal price;

    private int version;

    private Date created;
    private Date lastModified;

}
