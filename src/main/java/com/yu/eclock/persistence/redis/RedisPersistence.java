package com.yu.eclock.persistence.redis;

import com.yu.eclock.exception.PersistenceInstanceException;
import com.yu.eclock.persistence.DataModel;
import com.yu.eclock.persistence.EclockPersistence;
import com.yu.eclock.persistence.Persistence;
import com.yu.eclock.utils.JsonUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;
import java.util.List;

public class RedisPersistence implements EclockPersistence<RedisTemplate<String,String>>, Persistence {
    private final static Logger        LOGGER = LoggerFactory.getLogger(RedisPersistence.class);
    private final        StringRedisTemplate redisTemplate;
    public RedisPersistence(String dbString,String dbName,int port,String password){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(dbString,port);
        redisStandaloneConfiguration.setDatabase(Strings.isBlank(dbName)?0:Integer.parseInt(dbName));
        if(Strings.isNotBlank(password)){
            redisStandaloneConfiguration.setPassword(password.toCharArray());
        }
        RedisConnectionFactory redisConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
    }

    @Override
    public StringRedisTemplate getTemplate() {
        return redisTemplate;
    }

    @Override
    public boolean add(DataModel model) {
        try {
            if(model == null){
                // LOGGER.error("convert model is null");
                return false;
            }
           redisTemplate.opsForHash().put("eclock",model.getTaskId(),JsonUtils.toJsonString(model));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<DataModel> get() {
        // String query = (String)key;
        // Boolean exist = redisTemplate.hasKey("eclock");
        // if(exist){
        //     return redisTemplate.opsForHash().values("eclock").stream()
        //         .filter(Objects::nonNull)
        //         .map(t -> (DataModel)t).collect(
        //             Collectors.toList());
        // }
        return Collections.emptyList();
    }

    @Override
    public boolean remove(String key) {
        if (redisTemplate == null) throw new PersistenceInstanceException("redisClient instance exception");
        Long id = redisTemplate.opsForHash().delete("eclock", key);
        return id > 0;
    }
}
