package com.xjbg.sso.server.config;

import com.xjbg.sso.core.cache.CacheTemplate;
import com.xjbg.sso.core.util.AESOperator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kesc
 * @since 2019/2/27
 */
@Configuration
public class RedisConfig {
    private AESOperator aesOperator;

    private final RedisProperties properties;

    private final RedisSentinelConfiguration sentinelConfiguration;

    private final RedisClusterConfiguration clusterConfiguration;

    public RedisConfig(RedisProperties properties, AESOperator aesOperator,
                       ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration,
                       ObjectProvider<RedisClusterConfiguration> clusterConfiguration) {
        this.aesOperator = aesOperator;
        this.properties = properties;
        this.sentinelConfiguration = sentinelConfiguration.getIfAvailable();
        this.clusterConfiguration = clusterConfiguration.getIfAvailable();
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory()
            throws UnknownHostException {
        return applyProperties(createJedisConnectionFactory());
    }

    protected final RedisSentinelConfiguration getSentinelConfig() {
        if (this.sentinelConfiguration != null) {
            return this.sentinelConfiguration;
        }
        RedisProperties.Sentinel sentinelProperties = this.properties.getSentinel();
        if (sentinelProperties != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(sentinelProperties.getMaster());
            config.setSentinels(createSentinels(sentinelProperties));
            return config;
        }
        return null;
    }

    /**
     * Create a {@link RedisClusterConfiguration} if necessary.
     *
     * @return {@literal null} if no cluster settings are set.
     */
    protected final RedisClusterConfiguration getClusterConfiguration() {
        if (this.clusterConfiguration != null) {
            return this.clusterConfiguration;
        }
        if (this.properties.getCluster() == null) {
            return null;
        }
        RedisProperties.Cluster clusterProperties = this.properties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(
                clusterProperties.getNodes());

        if (clusterProperties.getMaxRedirects() != null) {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        return config;
    }

    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<RedisNode>();
        for (String node : StringUtils
                .commaDelimitedListToStringArray(sentinel.getNodes())) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            } catch (RuntimeException ex) {
                throw new IllegalStateException(
                        "Invalid redis sentinel " + "property '" + node + "'", ex);
            }
        }
        return nodes;
    }

    private JedisConnectionFactory createJedisConnectionFactory() {
        JedisPoolConfig poolConfig = (this.properties.getPool() != null)
                ? jedisPoolConfig() : new JedisPoolConfig();
        if (getSentinelConfig() != null) {
            return new JedisConnectionFactory(getSentinelConfig(), poolConfig);
        }
        if (getClusterConfiguration() != null) {
            return new JedisConnectionFactory(getClusterConfiguration(), poolConfig);
        }
        return new JedisConnectionFactory(poolConfig);
    }

    private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        RedisProperties.Pool props = this.properties.getPool();
        config.setMaxTotal(props.getMaxActive());
        config.setMaxIdle(props.getMaxIdle());
        config.setMinIdle(props.getMinIdle());
        config.setMaxWaitMillis(props.getMaxWait());
        return config;
    }

    protected final JedisConnectionFactory applyProperties(
            JedisConnectionFactory factory) {
        configureConnection(factory);
        if (this.properties.isSsl()) {
            factory.setUseSsl(true);
        }
        factory.setDatabase(this.properties.getDatabase());
        if (this.properties.getTimeout() > 0) {
            factory.setTimeout(this.properties.getTimeout());
        }
        return factory;
    }

    private void configureConnection(JedisConnectionFactory factory) {
        if (StringUtils.hasText(this.properties.getUrl())) {
            configureConnectionFromUrl(factory);
        } else {
            factory.setHostName(this.properties.getHost());
            factory.setPort(this.properties.getPort());
            if (this.properties.getPassword() != null) {
                try {
                    factory.setPassword(aesOperator.decrypt(this.properties.getPassword()));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    private void configureConnectionFromUrl(JedisConnectionFactory factory) {
        String url = this.properties.getUrl();
        if (url.startsWith("rediss://")) {
            factory.setUseSsl(true);
        }
        try {
            URI uri = new URI(url);
            factory.setHostName(uri.getHost());
            factory.setPort(uri.getPort());
            if (uri.getUserInfo() != null) {
                String password = uri.getUserInfo();
                int index = password.indexOf(":");
                if (index >= 0) {
                    password = password.substring(index + 1);
                }
                factory.setPassword(password);
            }
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Malformed 'spring.redis.url' " + url,
                    ex);
        }
    }

    @Bean
    public CacheTemplate getCacheTemplate(StringRedisTemplate stringRedisTemplate) {
        CacheTemplate template = new CacheTemplate();
        template.setRedisSerializer(new StringRedisSerializer());
        template.setStringRedisTemplate(stringRedisTemplate);
        return template;
    }
}
