package com.luanvan.searchservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.luanvan.searchservice.repository")
@EnableElasticsearchAuditing
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    String elasticsearchUrl;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUrl)
                .build();
    }
}


