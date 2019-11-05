package com.wjw.mongo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb.qa1")
public class Qa1MongoConfig extends AbstractMongoConfig {

	@Bean(name = "qa1MongoTemplate")
	@Override
	public MongoTemplate getMongoTemplate() {
		return new MongoTemplate(mongoDbFactory());
	}
}
