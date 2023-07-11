package redis.example.factory;

import redis.example.data.RedisData;
import redis.example.deserializer.RedisDataDeserializer;

public interface RedisDeserializerFactory<RT> {
    RedisDataDeserializer<? super RedisData, RT> getDeserializer(RedisData data);
}
