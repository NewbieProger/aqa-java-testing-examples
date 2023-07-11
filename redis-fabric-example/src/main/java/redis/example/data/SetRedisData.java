package redis.example.data;

public class SetRedisData implements RedisData {

    @Override
    public String getDataType() {
        return "SET";
    }
}
