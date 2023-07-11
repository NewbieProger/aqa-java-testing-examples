package redis.example.deserializer;

import redis.example.data.CustomDataResult;
import redis.example.data.RedisData;

public class SetDeserializer implements RedisDataDeserializer<RedisData, CustomDataResult> {
    @Override
    public CustomDataResult deserialize(RedisData data) {
        // implement custom mapping

        return null;
    }

    @Override
    public String getDeserializerType() {
        return "SET";
    }
}
