package com.example.product.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import lombok.Data;

import com.example.graphql.annotation.GraphQLEntity;

@Data
@Entity
@GraphQLEntity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private BigDecimal price;

    @Version
    private int version;

    private Date created;
    private Date lastModified;

    @PrePersist
    private void preSave() {
        created = new Date();
        lastModified = new Date();
    }

    @PreUpdate
    private void preUpdate() {
        lastModified = new Date();
    }

}
