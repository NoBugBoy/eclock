package com.yu.eclock.persistence;

import com.yu.eclock.enums.PersistenceEnum;
import com.yu.eclock.persistence.mongo.MongoPersistence;
import com.yu.eclock.persistence.mongo.RedisPersistence;

/**
 * 抽象任务持久化工厂
 */
public class EclockFactory {
     // EclockPersistenceFactory getFactory(String name){
     //     PersistenceEnum persistenceEnum = PersistenceEnum.valueOf(name);
     //     switch (persistenceEnum){
     //         case MONGO: return new MongoPersistence();
     //         case REDIS: return new RedisPersistence();
     //         default: return null;
     //     }
     // }
}
