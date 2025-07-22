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

  public boolean hasKey(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  public List<Object> getByIds(List<String> keys) {
    return redisTemplate.opsForValue().multiGet(keys);
  }

  public void mset(Map<String, Object> keyValueMap) {
    redisTemplate.opsForValue().multiSet(keyValueMap);
  }
}
