package com.example.idm_component.repository;

import com.example.idm_component.dto.BlacklistDto;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlacklistRedisRepository {
    private static final String REDIS_KEY_PREFIX = "blacklist:";
    private static final String ID_FIELD = "id";
    private static final String TIMESTAMP_FIELD = "timestamp";
    private static final String AUTH_TOKEN_FIELD = "authorizationToken";

    private Jedis jedis;

    public BlacklistRedisRepository() {
        jedis = new Jedis("localhost", 6379);
    }

    public BlacklistDto save(BlacklistDto blacklistDto) {
        String id = generateId();
        blacklistDto.setId(id);

        String redisKey = REDIS_KEY_PREFIX + id;

        jedis.hset(redisKey, ID_FIELD, id);
        jedis.hset(redisKey, TIMESTAMP_FIELD, blacklistDto.getTimestamp().toString());
        jedis.hset(redisKey, AUTH_TOKEN_FIELD, blacklistDto.getAuthorizationToken());

        return blacklistDto;
    }

    public List<BlacklistDto> findAll() {
        List<BlacklistDto> result = new ArrayList<>();
        try {
            for (String redisKey : jedis.keys(REDIS_KEY_PREFIX + "*")) {
                List<String> values = jedis.hmget(redisKey, ID_FIELD, TIMESTAMP_FIELD, AUTH_TOKEN_FIELD);
                String id = values.get(0);
                LocalDateTime timestamp = LocalDateTime.parse(values.get(1));
                String authToken = values.get(2);

                BlacklistDto blacklistDto = new BlacklistDto(timestamp, authToken);
                blacklistDto.setId(id);
                result.add(blacklistDto);
            }
        } catch (JedisDataException  | NullPointerException e) {
            System.out.print(e.getMessage());
        }
        return result;
    }

    private String generateId() {
        Random rand = new Random();
        return Long.toString(System.currentTimeMillis() + rand.nextInt(0,100000000));
    }
}