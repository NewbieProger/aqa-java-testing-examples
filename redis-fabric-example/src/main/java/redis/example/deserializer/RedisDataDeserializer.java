package redis.example.deserializer;

import redis.example.data.RedisData;

public interface RedisDataDeserializer<T extends RedisData, RT> {
    RT deserialize(T data);
    String getDeserializerType();
}
