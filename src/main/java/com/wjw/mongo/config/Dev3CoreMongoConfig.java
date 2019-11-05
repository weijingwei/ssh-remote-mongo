package com.wjw.mongo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb.dev3.core")
public class Dev3CoreMongoConfig extends AbstractMongoConfig {

	@Bean(name = "dev3CoreMongoTemplate")
	@Override
	public MongoTemplate getMongoTemplate() {
		return new MongoTemplate(mongoDbFactory());
	}
}
