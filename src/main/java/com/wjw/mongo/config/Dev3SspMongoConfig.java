package com.wjw.mongo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb.dev3.ssp")
public class Dev3SspMongoConfig extends AbstractMongoConfig {

	@Primary
	@Bean(name = "dev3SspMongoTemplate")
	@Override
	public MongoTemplate getMongoTemplate() {
		return new MongoTemplate(mongoDbFactory());
	}
}
