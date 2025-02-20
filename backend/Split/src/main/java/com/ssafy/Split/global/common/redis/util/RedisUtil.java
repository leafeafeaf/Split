package com.ssafy.Split.global.common.redis.util;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

  private final RedisTemplate<String, String> redisTemplate;

  public RedisUtil(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  // 데이터 저장 (만료 시간 설정 가능)
  public void setValue(String key, String value, long timeoutSeconds) {
    redisTemplate.opsForValue().set(key, value, timeoutSeconds, TimeUnit.SECONDS);
  }

  // 데이터 조회
  public String getValue(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  // 데이터 삭제
  public void deleteValue(String key) {
    redisTemplate.delete(key);
  }

  // Key 존재 여부 확인
  public boolean hasKey(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

}
