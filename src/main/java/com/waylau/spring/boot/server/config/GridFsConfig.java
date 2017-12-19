package com.waylau.spring.boot.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
public class GridFsConfig extends AbstractMongoConfiguration{
	
	
	@Autowired
	Environment env;
	
	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}

	@Override
	protected String getDatabaseName() {
		// TODO Auto-generated method stub
		System.err.println(env.getProperty("database"));
		return env.getProperty("database");
	}

	@Override
	public Mongo mongo() throws Exception {
		// TODO Auto-generated method stub
		System.err.println(env.getProperty("host"));
		return new MongoClient(env.getProperty("host"));
	}
	

}
