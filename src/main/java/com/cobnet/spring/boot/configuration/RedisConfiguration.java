package com.cobnet.spring.boot.configuration;

import com.cobnet.spring.boot.service.RedisMessageListener;
import com.cobnet.cache.redis.RedisMode;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Configuration
@ConfigurationProperties("spring.redis")
@EnableRedisRepositories
//@EnableTransactionManagement
public class RedisConfiguration {

    private MessageConfiguration message;

    private RedisMode mode;

    private String host;

    private String port;

    private int database;

    private String username;

    private String password;

    private Duration timeout;

    private String namespace;

    private int pipeliningBuffered;

    private SentinelConfiguration sentinel;

    private ClusterConfiguration cluster;

    private PoolConfiguration pool;

    @Bean
    public LettuceConnectionFactory connectionFactoryBean() {

        org.springframework.data.redis.connection.RedisConfiguration configuration = null;

        switch (this.mode) {
            case STAND_ALONE -> {
                RedisStandaloneConfiguration standalone = new RedisStandaloneConfiguration();
                standalone.setHostName(this.getHost());
                standalone.setPort(this.getPort());
                standalone.setDatabase(this.getDatabase());
                standalone.setPassword(this.getPassword());
                standalone.setUsername(this.getUsername());
                configuration = standalone;
            }
            case CLUSTER -> {
                RedisClusterConfiguration cluster = new RedisClusterConfiguration(this.getCluster().getNodes());
                cluster.setUsername(this.getUsername());
                cluster.setPassword(this.getPassword());
                cluster.setMaxRedirects(this.getCluster().getMaxRedirects());
                configuration = cluster;
            }
            case SENTINEL -> {
                RedisSentinelConfiguration sentinel = new RedisSentinelConfiguration();
                sentinel.setDatabase(this.getDatabase());
                sentinel.setMaster(this.getSentinel().getMaster());
                sentinel.setUsername(this.getUsername());
                sentinel.setPassword(this.getPassword());
                sentinel.setSentinelPassword(this.sentinel.getPassword());
                sentinel.setSentinels(this.getSentinel().getNodes().stream().map(node -> {

                    String[] args = node.split(":");

                    return new RedisNode(args[0], Integer.parseInt(args[1]));

                }).toList());
                configuration = sentinel;
            }
        }

        GenericObjectPoolConfig<?> pool = new GenericObjectPoolConfig<>();
        pool.setMaxIdle(this.getPool().getMaxIdle());
        pool.setMinIdle(this.getPool().getMinIdle());
        pool.setMaxTotal(this.getPool().getMaxActive());
        pool.setMaxWait(this.getPool().getMaxWait());

        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration, LettucePoolingClientConfiguration.builder().clientName(this.getNamespace()).commandTimeout(this.getTimeout()).poolConfig(pool).build());

        factory.setPipeliningFlushPolicy(LettuceConnection.PipeliningFlushPolicy.buffered(this.getPipeliningBuffered()));

        return factory;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateBean(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(factory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<?> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.registerModule(new JavaTimeModule());
        valueSerializer.setObjectMapper(mapper);

        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashKeySerializer(keySerializer);
        template.setEnableTransactionSupport(true);
//        template.setHashValueSerializer(valueSerializer);     we keep the hash value as binary
        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfigurationBean() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(10 * 60))
                .disableCachingNullValues();
    }

    @Bean
    public RedisCacheManager redisCacheManagerBean(RedisTemplate<String, Object> template) {

        return RedisCacheManager.builder(Objects.requireNonNull(template.getConnectionFactory()))
                .cacheDefaults(redisCacheConfigurationBean())
                .transactionAware()
                .build();
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapterBean(RedisTemplate<String, Object> template, RedisMessageListenerContainer container, RedisMessageListener listener) {

        MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
        container.setConnectionFactory(Objects.requireNonNull(template.getConnectionFactory()));
        container.addMessageListener(adapter, this.getMessage().getTopics().stream().map(ChannelTopic::new).toList());

        return adapter;
    }

    @Bean
    public HashOperations<String, String, Object> hashOperationsBean(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    public ValueOperations<String, Object> valueOperationsBean(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    public ListOperations<String, Object> listOperationsBean(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    @Bean
    public SetOperations<String, Object> setOperationsBean(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    @Bean
    public ZSetOperations<String, Object> zSetOperationsBean(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    public static class MessageConfiguration {

        private List<String> topics;

        public List<String> getTopics() {
            return topics;
        }

        public void setTopics(List<String> topics) {
            this.topics = topics;
        }
    }

    public static class ClusterConfiguration {

        private List<String> nodes;

        private int maxRedirects;

        public void setMaxRedirects(int maxRedirects) {
            this.maxRedirects = maxRedirects;
        }

        public int getMaxRedirects() {
            return maxRedirects;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        public List<String> getNodes() {
            return nodes;
        }
    }

    public static class SentinelConfiguration {

        private String master;

        private List<String> nodes;

        private String password;

        public void setMaster(String master) {
            this.master = master;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        public String getMaster() {
            return master;
        }

        public List<String> getNodes() {
            return nodes;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class PoolConfiguration {

        private int maxActive;

        private int maxIdle;

        private int minIdle;

        private Duration maxWait;

        public int getMaxActive() {
            return maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        public int getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMinIdle() {
            return minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public Duration getMaxWait() {
            return maxWait;
        }

        public void setMaxWait(Duration maxWait) {
            this.maxWait = maxWait;
        }
    }

    public MessageConfiguration getMessage() {
        return message;
    }

    public void setMessage(MessageConfiguration message) {
        this.message = message;
    }

    public RedisMode getMode() {
        return mode;
    }

    public void setMode(RedisMode mode) {
        this.mode = mode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {

        return (int) Float.parseFloat(port);
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ClusterConfiguration getCluster() {

        return cluster;
    }

    public void setCluster(ClusterConfiguration cluster) {

        this.cluster = cluster;
    }

    public SentinelConfiguration getSentinel() {
        return sentinel;
    }

    public void setSentinel(SentinelConfiguration sentinel) {

        this.sentinel = sentinel;
    }

    public PoolConfiguration getPool() {
        return pool;
    }

    public void setPool(PoolConfiguration pool) {
        this.pool = pool;
    }

    public int getPipeliningBuffered() {
        return pipeliningBuffered;
    }

    public void setPipeliningBuffered(int pipeliningBuffered) {
        this.pipeliningBuffered = pipeliningBuffered;
    }
}
