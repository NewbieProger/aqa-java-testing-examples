package redis.example.factory;

import redis.example.data.RedisData;
import redis.example.deserializer.RedisDataDeserializer;

import java.util.HashMap;
import java.util.Map;

public class RedisDeserializerFactoryImpl<RT> implements RedisDeserializerFactory<RT> {
    private final Map<String, RedisDataDeserializer<? super RedisData, RT>> deserializers = new HashMap<>();

    public RedisDeserializerFactoryImpl(RedisDataDeserializer<? super RedisData, RT>... deserializers) {
        for (RedisDataDeserializer<? super RedisData, RT> deserializer : deserializers) {
            this.deserializers.put(deserializer.getDeserializerType(), deserializer);
        }
    }

    @Override
    public RedisDataDeserializer<? super RedisData, RT> getDeserializer(RedisData data) {
        RedisDataDeserializer<? super RedisData, RT> deserializer = deserializers.get(data.getDataType());

        if (deserializer == null) {
            throw new RuntimeException("Everything is bad");
        }

        return deserializer;
    }
}
