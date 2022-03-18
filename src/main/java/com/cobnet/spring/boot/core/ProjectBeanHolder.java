package com.cobnet.spring.boot.core;

import com.cobnet.spring.boot.configuration.CacheConfiguration;
import com.cobnet.spring.boot.configuration.DatasourceConfiguration;
import com.cobnet.spring.boot.configuration.ProjectConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

public class ProjectBeanHolder {

    private static ConfigurableApplicationContext SPRING_CONTEXT;

    private static ProjectConfiguration PROJECT_CONFIGURATION;

    private static DatasourceConfiguration DATASOURCE_CONFIGURATION;

    private static CacheConfiguration CACHE_CONFIGURATION;

    private static CacheConfiguration.RedisConfiguration REDIS_CONFIGURATION;

    private static CacheConfiguration.SessionConfiguration SESSION_CONFIGURATION;

    private static ApplicationEventPublisher APPLICATION_EVENT_PUBLISHER;

    private static ObjectMapper OBJECT_MAPPER;

    private static Jackson2HashMapper HASH_MAPPER;

    private static ScriptEngineManager SCRIPT_ENGINE_MANAGER;

    private static TaskProvider TASK_PROVIDER;

    private static RedisTemplate<String, Object> REDIS_TEMPLATE;

    private static RedisCacheManager REDIS_CACHE_MANAGER;

    private static MessageListenerAdapter MESSAGE_LISTENER_ADAPTER;

    private static RedisMessageListenerContainer REDIS_MESSAGE_LISTENER_CONTAINER;

    private static HashOperations<String, String, Object> REDIS_HASH_OPERATIONS;

    private static ValueOperations<String, Object> REDIS_VALUE_OPERATIONS;

    private static ListOperations<String, Object> REDIS_LIST_OPERATIONS;

    private static SetOperations<String, Object> REDIS_SET_OPERATIONS;

    private static ZSetOperations<String, Object> REDIS_Z_SET_OPERATIONS;

    private static PlatformTransactionManager PLATFORM_TRANSACTION_MANAGER;

    public static ConfigurableApplicationContext getSpringContext() {

        return ProjectBeanHolder.SPRING_CONTEXT;
    }

    public static ProjectConfiguration getProjectConfiguration() {

        return ProjectBeanHolder.PROJECT_CONFIGURATION;
    }

    public static DatasourceConfiguration getDatasourceConfiguration() {

        return  ProjectBeanHolder.DATASOURCE_CONFIGURATION;
    }

    public static CacheConfiguration getCacheConfiguration() {

        return  ProjectBeanHolder.CACHE_CONFIGURATION;
    }

    public static CacheConfiguration.RedisConfiguration getRedisConfiguration() {

        return ProjectBeanHolder.REDIS_CONFIGURATION;
    }

    public static CacheConfiguration.SessionConfiguration getSessionConfiguration() {

        return ProjectBeanHolder.SESSION_CONFIGURATION;
    }

    public static ApplicationEventPublisher getApplicationEventPublisher() {

        return ProjectBeanHolder.APPLICATION_EVENT_PUBLISHER;
    }

    public static ObjectMapper getObjectMapper() {

        return ProjectBeanHolder.OBJECT_MAPPER;
    }

    public static Jackson2HashMapper getHashMapper() {

        return ProjectBeanHolder.HASH_MAPPER;
    }

    public static ScriptEngineManager getScriptEngineManager() {

        return ProjectBeanHolder.SCRIPT_ENGINE_MANAGER;
    }

    public static TaskProvider getTaskProvider() {

        return ProjectBeanHolder.TASK_PROVIDER;
    }

    public static RedisTemplate<String, Object> getRedisTemplate() {

        return ProjectBeanHolder.REDIS_TEMPLATE;
    }

    public static RedisCacheManager getRedisCacheManager() {

        return ProjectBeanHolder.REDIS_CACHE_MANAGER;
    }

    public static MessageListenerAdapter getMessageListenerAdapter() {

        return  ProjectBeanHolder.MESSAGE_LISTENER_ADAPTER;
    }

    public static RedisMessageListenerContainer getRedisMessageListenerContainer() {

        return ProjectBeanHolder.REDIS_MESSAGE_LISTENER_CONTAINER;
    }

    public static HashOperations<String, String, Object> getRedisHashOperations() {

        return ProjectBeanHolder.REDIS_HASH_OPERATIONS;
    }

    public static ValueOperations<String, Object> getRedisValueOperations() {

        return ProjectBeanHolder.REDIS_VALUE_OPERATIONS;
    }

    public static ListOperations<String, Object> getRedisListOperations() {

        return ProjectBeanHolder.REDIS_LIST_OPERATIONS;
    }

    public static SetOperations<String, Object> getRedisSetOperations() {

        return ProjectBeanHolder.REDIS_SET_OPERATIONS;
    }

    public static ZSetOperations<String, Object> getRedisZSetOperations() {

        return  ProjectBeanHolder.REDIS_Z_SET_OPERATIONS;
    }

    public static PlatformTransactionManager getPlatformTransactionManager() {

        return ProjectBeanHolder.PLATFORM_TRANSACTION_MANAGER;
    }

    @Component
    final static class AutowireLoader {

        @Autowired
        public void setConfigurableApplicationContext(ConfigurableApplicationContext context) {

            ProjectBeanHolder.SPRING_CONTEXT = context;
        }

        @Autowired
        public void setProjectConfiguration(ProjectConfiguration config) {

            ProjectBeanHolder.PROJECT_CONFIGURATION = config;
        }

        @Autowired
        public void setDatasourceConfiguration(DatasourceConfiguration config) {

            ProjectBeanHolder.DATASOURCE_CONFIGURATION = config;
        }

        @Autowired
        public void setCacheConfiguration(CacheConfiguration config) {

            ProjectBeanHolder.CACHE_CONFIGURATION = config;
        }

        @Autowired
        public void setRedisConfiguration(CacheConfiguration.RedisConfiguration config) {

            ProjectBeanHolder.REDIS_CONFIGURATION = config;
        }

        @Autowired
        public void setSessionConfiguration(CacheConfiguration.SessionConfiguration config) {

            ProjectBeanHolder.SESSION_CONFIGURATION = config;
        }

        @Autowired
        public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {

            ProjectBeanHolder.APPLICATION_EVENT_PUBLISHER = publisher;
        }

        @Autowired
        public void setObjectMapper(ObjectMapper mapper) {

            ProjectBeanHolder.OBJECT_MAPPER = mapper;
        }

        @Autowired
        public void setHashMapper(Jackson2HashMapper mapper) {

            ProjectBeanHolder.HASH_MAPPER = mapper;
        }

        @Autowired
        public void setScriptEngineManager(ScriptEngineManager manager) {

            ProjectBeanHolder.SCRIPT_ENGINE_MANAGER = manager;
        }

        @Autowired
        public void setTaskProvider(TaskProvider provider) {

            ProjectBeanHolder.TASK_PROVIDER = provider;
        }

        @Autowired
        public void setRedisTemplate(RedisTemplate<String, Object> template) {

            ProjectBeanHolder.REDIS_TEMPLATE = template;
        }

        @Autowired
        public void setRedisCacheManager(RedisCacheManager manager) {

            ProjectBeanHolder.REDIS_CACHE_MANAGER = manager;
        }

        @Autowired
        public void setMessageListenerAdapter(MessageListenerAdapter adapter) {

            ProjectBeanHolder.MESSAGE_LISTENER_ADAPTER = adapter;
        }

        @Autowired
        public void setRedisMessageListenerContainer(RedisMessageListenerContainer container) {

            ProjectBeanHolder.REDIS_MESSAGE_LISTENER_CONTAINER = container;
        }

        @Autowired
        public void setRedisHashOperations(HashOperations<String, String, Object> operations) {

            ProjectBeanHolder.REDIS_HASH_OPERATIONS = operations;
        }

        @Autowired
        public void setRedisValueOperations(ValueOperations<String, Object> operations) {

            ProjectBeanHolder.REDIS_VALUE_OPERATIONS = operations;
        }

        @Autowired
        public void setRedisListOperations(ListOperations<String, Object> operations) {

            ProjectBeanHolder.REDIS_LIST_OPERATIONS = operations;
        }

        @Autowired
        public void setRedisSetOperations(SetOperations<String, Object> operations) {

            ProjectBeanHolder.REDIS_SET_OPERATIONS = operations;
        }

        @Autowired
        public void setRedisZSetOperations(ZSetOperations<String, Object> operations) {

            ProjectBeanHolder.REDIS_Z_SET_OPERATIONS = operations;
        }

        public void setPlatformTransactionManager(PlatformTransactionManager manager) {

            ProjectBeanHolder.PLATFORM_TRANSACTION_MANAGER = manager;
        }
    }

    @Component
    final static class BeanRegister {

        @Bean
        public ObjectMapper objectMapperBean() {

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            return mapper;
        }

        @Bean
        public Jackson2HashMapper hashMapperBean(@Autowired ObjectMapper mapper) {

            return new Jackson2HashMapper(mapper,false);
        }

        @Bean
        public ScriptEngineManager scriptEngineManagerBean() {

            return new ScriptEngineManager();
        }

        @Bean
        public TaskProvider taskProviderBean(@Autowired ProjectConfiguration config) {

            return new TaskProvider(config.getTaskProvider());
        }
    }
}
