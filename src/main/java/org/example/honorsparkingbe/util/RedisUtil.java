package org.example.honorsparkingbe.util;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.NotificationQueueItem;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

  private static final String QUEUE_KEY = "notification:queue";
  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisTemplate<String, NotificationQueueItem> notificationRedisTemplate;

  // RedisTemplate을 사용하는 경우 RedisConnectionFactory를 설정하는 방법
  private final RedisConnectionFactory redisConnectionFactory;


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

  // Notification 큐(notification:queue) 관련 기능
  // 1. 단일 객체 enqueue
  public void notiEnqueue(NotificationQueueItem value) {
    notificationRedisTemplate.opsForList().rightPush(QUEUE_KEY, value);
  }

  // 2. 여러 객체 한 번에 enqueue
  public void notiEnqueueAll(List<NotificationQueueItem> values) {
    notificationRedisTemplate.opsForList().rightPushAll(QUEUE_KEY, values);
  }

  // 3. 하나 dequeue (왼쪽 pop)
  public NotificationQueueItem notiDequeue() {
    return notificationRedisTemplate.opsForList().leftPop(QUEUE_KEY);
  }

  // 4. 큐 길이 확인 (선택)
  public Long notiQueueSize() {
    return notificationRedisTemplate.opsForList().size(QUEUE_KEY);
  }
}
