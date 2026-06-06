package com.example.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Configuration for Spring Data Elasticsearch.
 * The underlying client is auto-configured by Spring Boot via application.yml (spring.elasticsearch.uris).
 * We enable the repositories here if we have them in specific packages in the future.
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.infrastructure.adapter.out.persistence.elasticsearch")
public class ElasticsearchConfig {

}
