package com.yu.eclock.persistence.redis;

import com.alibaba.fastjson.JSONObject;
import com.yu.eclock.exception.PersistenceInstanceException;
import com.yu.eclock.persistence.DataModel;
import com.yu.eclock.persistence.EclockPersistence;
import com.yu.eclock.persistence.Persistence;
import com.yu.eclock.utils.JsonUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RedisPersistence implements EclockPersistence<StringRedisTemplate>, Persistence {
    private final static Logger        LOGGER = LoggerFactory.getLogger(RedisPersistence.class);
    private final        StringRedisTemplate redisTemplate;
    public RedisPersistence(String dbString,String dbName,int port,String password){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(dbString,port);
        redisStandaloneConfiguration.setDatabase(Strings.isBlank(dbName)?0:Integer.parseInt(dbName));
        if(Strings.isNotBlank(password)){
            redisStandaloneConfiguration.setPassword(password.toCharArray());
        }

        JedisConnectionFactory lettuceConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);
        this.redisTemplate = new StringRedisTemplate(lettuceConnectionFactory);
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
        Boolean exist = redisTemplate.hasKey("eclock");
        if(exist){
            return redisTemplate.opsForHash().values("eclock").stream()
                .filter(Objects::nonNull)
                .map(t -> JSONObject.parseObject((String)t,DataModel.class))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean remove(String key) {
        if (redisTemplate == null) throw new PersistenceInstanceException("redisClient instance exception");
        Long id = redisTemplate.opsForHash().delete("eclock", key);
        return id > 0;
    }
}
