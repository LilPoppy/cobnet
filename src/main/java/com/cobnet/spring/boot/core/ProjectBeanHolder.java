package com.cobnet.spring.boot.core;

import com.cobnet.spring.boot.configuration.ProjectConfiguration;
import com.cobnet.spring.boot.configuration.RedisConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Component;

public class ProjectBeanHolder {

    private static ConfigurableApplicationContext SPRING_CONTEXT;

    private static ProjectConfiguration PROJECT_CONFIGURATION;

    private static RedisConfiguration REDIS_CONFIGURATION;

    private static ApplicationEventPublisher APPLICATION_EVENT_PUBLISHER;

    private static ObjectMapper OBJECT_MAPPER;

    private static Jackson2HashMapper HASH_MAPPER;

    private static ScriptEngineManager SCRIPT_ENGINE_MANAGER;

    private static TaskProvider TASK_PROVIDER;

    private static RedisTemplate<String, Object> REDIS_TEMPLATE;

    private static HashOperations<String, String, Object> REDIS_HASH_OPERATIONS;

    private static ValueOperations<String, Object> REDIS_VALUE_OPERATIONS;

    private static ListOperations<String, Object> REDIS_LIST_OPERATIONS;

    private static SetOperations<String, Object> REDIS_SET_OPERATIONS;

    private static ZSetOperations<String, Object> REDIS_Z_SET_OPERATIONS;

    public static ConfigurableApplicationContext getSpringContext() {

        return ProjectBeanHolder.SPRING_CONTEXT;
    }

    public static ProjectConfiguration getProjectConfiguration() {

        return ProjectBeanHolder.PROJECT_CONFIGURATION;
    }

    public static RedisConfiguration getRedisConfiguration() {

        return ProjectBeanHolder.REDIS_CONFIGURATION;
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
        public void setRedisConfiguration(RedisConfiguration config) {

            ProjectBeanHolder.REDIS_CONFIGURATION = config;
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

            return new Jackson2HashMapper(mapper,true);
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
