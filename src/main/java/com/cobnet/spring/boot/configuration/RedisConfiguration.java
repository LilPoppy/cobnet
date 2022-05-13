package com.cobnet.spring.boot.configuration;

import com.cobnet.spring.boot.cache.support.*;
import com.cobnet.spring.boot.service.RedisMessageListener;
import com.cobnet.spring.boot.cache.support.RedisMode;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.convert.*;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.*;

@Configuration
@ConfigurationProperties("spring.redis")
@EnableRedisRepositories(value = "com.cobnet.interfaces.spring.repository", repositoryBaseClass = RedisKeyValueExtensionRepository.class, enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
//@EnableTransactionManagement
public class RedisConfiguration {

    private boolean enableRedisson;

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
    @ConditionalOnProperty(name="spring.redis.enable-redisson", havingValue="true")
    public RedissonClient redissonClientBean() {

        Config config = new Config();
        config.setTransportMode(TransportMode.EPOLL);

        switch (this.mode) {
            case STAND_ALONE -> {
                config.useSingleServer().setAddress(new StringBuilder("redis://").append(this.getHost()).append(":").append(this.getPort()).toString());
                config.useSingleServer().setDatabase(this.getDatabase());
                config.useSingleServer().setUsername(this.getUsername());
                config.useSingleServer().setPassword(this.getPassword());
                config.useSingleServer().setConnectionPoolSize(this.getPool().getMaxActive());
                config.useSingleServer().setConnectionMinimumIdleSize(this.getPool().getMinIdle());
                config.useSingleServer().setSubscriptionConnectionMinimumIdleSize(this.getPool().getMinIdle());
                config.useSingleServer().setSubscriptionConnectionPoolSize(this.getPool().getMaxIdle());
                config.useSingleServer().setIdleConnectionTimeout((int)this.getTimeout().toMillis());
                config.useSingleServer().setTimeout((int)this.getTimeout().toMillis());
                config.useSingleServer().setConnectTimeout((int)this.getTimeout().toMillis());
            }
            case CLUSTER -> {

                for(String node : this.cluster.getNodes()) {

                    config.useClusterServers().addNodeAddress(new StringBuilder("redis://").append(node).toString());
                }
                config.useClusterServers().setUsername(this.getUsername());
                config.useClusterServers().setPassword(this.getPassword());
                config.useClusterServers().setSlaveConnectionMinimumIdleSize(this.getPool().getMinIdle());
                config.useClusterServers().setSubscriptionConnectionMinimumIdleSize(this.getPool().getMinIdle());
                config.useClusterServers().setMasterConnectionMinimumIdleSize(this.getPool().getMinIdle());
                config.useClusterServers().setTimeout((int)this.getTimeout().toMillis());
                config.useClusterServers().setConnectTimeout((int)this.getTimeout().toMillis());
                config.useClusterServers().setIdleConnectionTimeout((int)this.getTimeout().toMillis());
                config.useClusterServers().setMasterConnectionPoolSize(this.getPool().getMaxActive());
                config.useClusterServers().setSubscriptionConnectionPoolSize(this.getPool().getMaxActive());
                config.useClusterServers().setSlaveConnectionPoolSize(this.getPool().getMaxActive());
            }
            case SENTINEL -> {

                for(String node : this.sentinel.getNodes()) {

                    config.useSentinelServers().addSentinelAddress(new StringBuilder("redis://").append(node).toString());
                }
                config.useSentinelServers().setDatabase(this.getDatabase());
                config.useSentinelServers().setMasterName(this.getSentinel().getMaster());
                config.useSentinelServers().setUsername(this.getUsername());
                config.useSentinelServers().setPassword(this.getPassword());
                config.useSentinelServers().setSentinelPassword(this.sentinel.getPassword());
                config.useSentinelServers().setMasterConnectionMinimumIdleSize(this.getPool().getMinIdle());
                config.useSentinelServers().setSlaveConnectionMinimumIdleSize(this.getPool().getMinIdle());
                config.useSentinelServers().setSubscriptionConnectionMinimumIdleSize(this.getPool().getMinIdle());
                config.useSentinelServers().setMasterConnectionPoolSize(this.getPool().getMaxActive());
                config.useSentinelServers().setSlaveConnectionPoolSize(this.getPool().getMaxActive());
                config.useSentinelServers().setSubscriptionConnectionPoolSize(this.getPool().getMaxActive());
                config.useSentinelServers().setTimeout((int)this.getTimeout().toMillis());
                config.useSentinelServers().setConnectTimeout((int)this.getTimeout().toMillis());
                config.useSentinelServers().setIdleConnectionTimeout((int)this.getTimeout().toMillis());
            }
        }

        return Redisson.create(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateBean(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(factory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<?> valueSerializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.registerModule(new JavaTimeModule());
        valueSerializer.setObjectMapper(mapper);

        template.setKeySerializer(keySerializer);
//        template.setValueSerializer(valueSerializer);     we keep the value as binary
        template.setHashKeySerializer(keySerializer);
        template.setEnableTransactionSupport(true);
        template.setHashValueSerializer(valueSerializer);
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
    public RedisKeyValueTemplate redisKeyValueTemplate(ConfigurableApplicationContext context, RedisTemplate<String, Object> template, RedisKeyValueExtensionAdapter adapter) {
        RedisKeyValueExtensionTemplate operations =  new RedisKeyValueExtensionTemplate(template, adapter, redisMappingContextBean());
        operations.setApplicationEventPublisher(context);


        return operations;
    }

    @Bean
    public RedisConverter redisConverter(ObjectMapper mapper, ReferenceResolver resolver, CustomConversions conversions) {

        MappingJsonRedisConverter converter = new MappingJsonRedisConverter(mapper, redisMappingContextBean(), null, resolver, customTypeMapper(), conversions);

        return converter;
    }

    @Bean
    public RedisKeyValueExtensionAdapter redisKeyValueAdapter(RedisTemplate<String, Object> template, RedisConverter conversions) {

        RedisKeyValueExtensionAdapter adapter = new RedisKeyValueExtensionAdapter(template, conversions);
        adapter.setEnableKeyspaceEvents(RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP);
        adapter.setShadowCopy(RedisKeyValueAdapter.ShadowCopy.OFF);
        return adapter;
    }

    @Bean
    public RedisCustomConversions redisCustomConversions(BytesToLocaleConverter bytesToLocaleConverter, LocaleToBytesConverter localeToBytesConverter) {
        return new RedisCustomConversions(Arrays.asList(
                bytesToLocaleConverter, localeToBytesConverter));
    }

    @Bean
    public RedisCustomTypeMapper customTypeMapper() {
        return new RedisCustomTypeMapper();
    }

    @Bean
    public RedisMappingContext redisMappingContextBean() {

        return new RedisMappingContext();
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

    public boolean isEnableRedisson() {
        return enableRedisson;
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

    public void setEnableRedisson(boolean enableRedisson) {
        this.enableRedisson = enableRedisson;
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
