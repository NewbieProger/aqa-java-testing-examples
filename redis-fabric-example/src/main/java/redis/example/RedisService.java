package redis.example;

import redis.example.data.CustomDataResult;
import redis.example.data.RedisData;
import redis.example.deserializer.SetDeserializer;
import redis.example.factory.RedisDeserializerFactoryImpl;

public class RedisService {
    private Redis redis;
    private RedisDeserializerFactoryImpl<CustomDataResult> deserializerFactory;

    public RedisService(Redis redis) {
        this.redis = redis;
        this.deserializerFactory = new RedisDeserializerFactoryImpl<>(
            new SetDeserializer()
            // new StringDeserializer()
            // new FloatSetDeserializer()
        );
    }

    public CustomDataResult getValue(String id) {
        RedisData data = redis.get(id);

        return deserializerFactory.getDeserializer(data).deserialize(data);
    }
}
