package com.wjw.mongo.config;

import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClientURI;

public abstract class AbstractMongoConfig {
	public MongoDbFactory mongoDbFactory() {
		MongoClientURI mongoURI = new MongoClientURI(uri);
		return new SimpleMongoDbFactory(mongoURI);
	}

	public abstract MongoTemplate getMongoTemplate();

	private String uri;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}