package com.yu.eclock.persistence;

import com.yu.eclock.enums.PersistenceEnum;
import com.yu.eclock.exception.PersistenceNameException;
import com.yu.eclock.persistence.mongo.MongoPersistence;
import com.yu.eclock.persistence.redis.RedisPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PersistenceFactory {
    private static final Logger           LOGGER = LoggerFactory.getLogger(PersistenceFactory.class);
    private static MongoPersistence mongoPersistence;
    private static RedisPersistence redisPersistence;
    private static Properties       properties;
    private final static Object lock = new Object();
    static {
        //init properties
        ClassPathResource classPathResource = new ClassPathResource("application.properties");
        InputStream inputStream = null;
        Properties prop = new Properties();
        try {
            inputStream = classPathResource.getInputStream();
            prop.load(inputStream);
            properties = prop;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static Persistence getPersistence(String name) {
        final PersistenceEnum persistenceEnum;
        try {
            persistenceEnum = PersistenceEnum.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new PersistenceNameException("not found persistence name"+ name);
        }
        if(persistenceEnum.equals(PersistenceEnum.MONGO)){
            if(mongoPersistence == null){
                synchronized (lock){
                    if(mongoPersistence == null){
                        String dbUrl = properties.getProperty("eternal.clock.persistence.mongo.db-url","mongodb://localhost:27017/eclock");
                        if(LOGGER.isDebugEnabled()){
                            LOGGER.debug("dburl {}", dbUrl);
                        }
                        mongoPersistence = new MongoPersistence(dbUrl);
                    }
                    return mongoPersistence;
                }
            }else{
                return mongoPersistence;
            }
        }else if(persistenceEnum.equals(PersistenceEnum.REDIS)){
            if(redisPersistence == null){
                synchronized (lock){
                    if(redisPersistence == null){
                        String dbname = properties.getProperty("eternal.clock.persistence.redis.db-name","0");
                        String dbUrl = properties.getProperty("eternal.clock.persistence.redis.db-url","localhost");
                        String password = properties.getProperty("eternal.clock.persistence.redis.password","");
                        String port = properties.getProperty("eternal.clock.persistence.redis.port","6379");
                        if(LOGGER.isDebugEnabled()){
                            LOGGER.debug("dbname {}" , dbname);
                            LOGGER.debug("dburl {}", dbUrl);
                        }
                        redisPersistence = new RedisPersistence(dbUrl,dbname,Integer.parseInt(port),password);
                    }
                    return redisPersistence;
                }
            }else{
                return redisPersistence;
            }
        }
        throw new PersistenceNameException("not found persistence name\"+ name");
    }

}
