package com.yu.eclock.persistence.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClientFactory;
import com.mongodb.client.MongoClients;
import com.mongodb.client.result.DeleteResult;
import com.yu.eclock.core.AbstractTask;
import com.yu.eclock.exception.PersistenceInstanceException;
import com.yu.eclock.persistence.DataModel;
import com.yu.eclock.persistence.EclockPersistence;
import com.yu.eclock.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class MongoPersistence implements EclockPersistence<MongoTemplate>, Persistence {
    private final static Logger        LOGGER = LoggerFactory.getLogger(MongoPersistence.class);
    private final        MongoTemplate mongoTemplate;
    public MongoPersistence(String dbString,String dbName){
        MongoClient mongoClient = MongoClients.create(dbString);
        SimpleMongoClientDbFactory simpleMongoClientDbFactory = new SimpleMongoClientDbFactory(mongoClient,dbName);
        this.mongoTemplate = new MongoTemplate(simpleMongoClientDbFactory);
    }

    @Override
    public MongoTemplate getTemplate() {
        return mongoTemplate;
    }

    @Override
    public boolean add(DataModel model) {
        try {
            if(model == null){
                // LOGGER.error("convert model is null");
                return false;
            }
            mongoTemplate.insert(model);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<DataModel> get() {
        // String query = (String)key;
        return mongoTemplate.find(new Query(), DataModel.class);
    }

    @Override
    public boolean remove(String key) {
        if (mongoTemplate == null) throw new PersistenceInstanceException("mongoClient instance exception");
        DeleteResult id = mongoTemplate.remove(new Query(Criteria.where("taskId").is(key)), DataModel.class);
        return id.getDeletedCount() > 0;
    }
}
