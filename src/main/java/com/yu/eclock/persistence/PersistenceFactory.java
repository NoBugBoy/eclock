package com.yu.eclock.persistence;

import com.yu.eclock.config.TimeWheelStartConfig;
import com.yu.eclock.enums.PersistenceEnum;
import com.yu.eclock.exception.PersistenceNameException;
import com.yu.eclock.persistence.mongo.MongoPersistence;
import com.yu.eclock.persistence.redis.RedisPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@EnableConfigurationProperties(TimeWheelStartConfig.class)
public class PersistenceFactory {
    private static TimeWheelStartConfig timeWheelStartConfig;

    @Autowired
    public void TimeWheelStartConfig(TimeWheelStartConfig twsc) {
        timeWheelStartConfig = twsc;
    }

    private static final Logger           LOGGER = LoggerFactory.getLogger(PersistenceFactory.class);
    private final static Object           lock   = new Object();
    private static       MongoPersistence mongoPersistence;
    private static       RedisPersistence redisPersistence;

    public static Persistence getPersistence(String name) {
        final PersistenceEnum persistenceEnum;
        try {
            persistenceEnum = PersistenceEnum.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new PersistenceNameException("not found persistence name" + name);
        }
        if (persistenceEnum.equals(PersistenceEnum.MONGO)) {
            if (mongoPersistence == null) {
            synchronized (lock) {
                if(mongoPersistence == null){
                    String dbUrl = timeWheelStartConfig.getPersistence().getMongo().getDbUrl();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("dburl {}", dbUrl);
                    }
                    mongoPersistence = new MongoPersistence(dbUrl);
                }
                return mongoPersistence;
            }
        }
            return mongoPersistence;
        } else if (persistenceEnum.equals(PersistenceEnum.REDIS)) {
            if (redisPersistence == null) {
                synchronized (lock) {
                    if (redisPersistence == null) {
                        TimeWheelStartConfig.Redis redis    = timeWheelStartConfig.getPersistence().getRedis();
                        String                     dbname   = redis.getDbName();
                        String                     dbUrl    = redis.getDbUrl();
                        String                     password = redis.getPassword();
                        String                     port     = redis.getPort();
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("dbname {}", dbname);
                            LOGGER.debug("dburl {}", dbUrl);
                        }
                        redisPersistence = new RedisPersistence(dbUrl, dbname, Integer.parseInt(port), password);
                    }
                    return redisPersistence;
                }
            }
            return redisPersistence;
        }
        throw new PersistenceNameException("not found persistence name\"+ name");
    }
}