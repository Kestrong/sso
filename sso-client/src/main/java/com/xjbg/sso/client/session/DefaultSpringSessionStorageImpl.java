package com.xjbg.sso.client.session;

import lombok.Getter;
import lombok.Setter;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

/**
 * @author kesc
 * @since 2019/6/3
 */
@Getter
@Setter
public class DefaultSpringSessionStorageImpl extends AbstractRedisSessionStorage {
    private RedisOperationsSessionRepository sessionRepository;

    @Override
    public void removeBySessionById(String sessionId) {
        sessionRepository.delete(sessionId);
    }

}
