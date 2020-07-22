package com.yu.eclock.persistence.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.yu.eclock.persistence.EclockPersistenceFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;

public class MongoPersistence implements EclockPersistenceFactory<MongoTemplate> {
    private String dbString;
    private String dbName;
    private final MongoTemplate mongoTemplate;
    public MongoPersistence(String dbString){
        this.dbString = dbString;
        MongoClient mongoClient = MongoClients.create(dbString);
        SimpleMongoClientDbFactory simpleMongoClientDbFactory = new SimpleMongoClientDbFactory(mongoClient,"123");
        MongoTemplate mongoTemplate = new MongoTemplate(simpleMongoClientDbFactory);
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public MongoTemplate getTemplate() {
        return mongoTemplate;
    }
}
