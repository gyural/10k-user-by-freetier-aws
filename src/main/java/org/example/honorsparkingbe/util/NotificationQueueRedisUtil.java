package org.example.honorsparkingbe.util;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.NotificationQueueItem;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationQueueRedisUtil {

  private static final String QUEUE_KEY = "notification:queue";
  private final RedisTemplate<String, NotificationQueueItem> notificationRedisTemplate;

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
