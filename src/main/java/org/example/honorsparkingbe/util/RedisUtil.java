package org.example.honorsparkingbe.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.NotificationQueueItem;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

  private static final String QUEUE_KEY = "notification:queue";
  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisTemplate<String, NotificationQueueItem> notificationRedisTemplate;

  public void set(String key, Object value, long timeout, TimeUnit unit) {
    redisTemplate.opsForValue().set(key, value, timeout, unit);
  }

  public Object get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public boolean delete(String key) {
    return Boolean.TRUE.equals(redisTemplate.delete(key));
  }

  /**
   * Checks whether the specified key exists in Redis.
   *
   * @param key the key to check for existence
   * @return {@code true} if the key exists, {@code false} otherwise
   */
  public boolean hasKey(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  /**
   * Retrieves the values associated with the specified list of keys from Redis.
   *
   * @param keys the list of Redis keys to retrieve values for
   * @return a list of values corresponding to the provided keys, with nulls for missing keys
   */
  public List<Object> getByIds(List<String> keys) {
    return redisTemplate.opsForValue().multiGet(keys);
  }

  /**
   * Stores multiple key-value pairs in Redis in a single batch operation.
   *
   * @param keyValueMap a map containing the keys and their corresponding values to be stored in Redis
   */
  public void mset(Map<String, Object> keyValueMap) {
    redisTemplate.opsForValue().multiSet(keyValueMap);
  }
}
