package com.cobnet.spring.boot.core;

import com.cobnet.interfaces.FileSource;
import com.cobnet.interfaces.security.Account;
import com.cobnet.interfaces.spring.repository.*;
import com.cobnet.spring.boot.configuration.*;
import com.cobnet.spring.boot.dto.support.DTOModule;
import com.cobnet.spring.boot.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

public class ProjectBeanHolder {

    private static FileSource FILE_SOURCE;

    private static FileInfoRepository FILE_INFO_REPOSITORY;

    private static FileSourceConfiguration FILE_SOURCE_CONFIGURATION;
    private static MessageSource MESSAGE_SOURCE;

    private static StoreService STORE_SERVICE;

    private static AddressRepository ADDRESS_REPOSITORY;

    private static PhoneNumberSmsVerifyService PHONE_NUMBER_SMS_VERIFY_SERVICE;

    private static GoogleMapService GOOGLE_MAP_SERVICE;

    private static GoogleMap GOOGLE_MAP;

    private static GoogleConsoleConfiguration GOOGLE_CONSOLE_CONFIGURATION;

    private static RememberMeServices REMEMBER_ME_SERVICE;

    private static AccountService ACCOUNT_SERVICE;

    private static CacheService CACHE_SERVICE;

    private static HumanValidatorService HUMAN_VALIDATOR;

    private static RandomImageProvider RANDOM_IMAGE_PROVIDER;

    private static EntityManager ENTITY_MANAGER;

    private static ConfigurableApplicationContext SPRING_CONTEXT;

    private static ProjectConfiguration PROJECT_CONFIGURATION;

    private static DatasourceConfiguration DATASOURCE_CONFIGURATION;

    private static CacheConfiguration CACHE_CONFIGURATION;

    private static RedisConfiguration REDIS_CONFIGURATION;

    private static SessionConfiguration SESSION_CONFIGURATION;

    private static SecurityConfiguration SECURITY_CONFIGURATION;

    private static TwilioConfiguration TWILIO_CONFIGURATION;

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

    private static RedisIndexedSessionRepository REDIS_INDEXED_SESSION_REPOSITORY;

    private static ClientRegistrationRepository CLIENT_REGISTRATION_REPOSITORY;

    private static OAuth2AuthorizedClientService OAUTH2_AUTHORIZED_CLIENT_SERVICE;

    private static SessionRegistry SESSION_REGISTRY;

    private static PasswordEncoder PASSWORD_ENCODER;

    private static AuthenticationManager AUTHENTICATION_MANAGER;

    private static StoreRepository STORE_REPOSITORY;

    private static StoreOrderRepository STORE_ORDER_REPOSITORY;

    private static WorkRepository WORK_REPOSITORY;

    private static UserRepository USER_REPOSITORY;

    private static UserRoleRepository USER_ROLE_REPOSITORY;

    private static ExternalUserRepository EXTERNAL_USER_REPOSITORY;

    private static PersistentLoginsRepository PERSISTENT_LOGINS_REPOSITORY;

    private static RedirectStrategy REDIRECT_STRATEGY;

    private static OidcUserService OIDC_USER_SERVICE;

    private static Messager MESSAGER;

    private static JavaMailSender JAVA_MAIL_SENDER;

    private static QRCodeProvider QR_CODE_PROVIDER;

    private static DataSource DATA_SOURCE;

    private static RedisCacheKeyGenerator REDIS_CACHE_KEY_GENERATOR;

    private static ModelMapper MODEL_MAPPER;

    public static FileInfoRepository getFileInfoRepository() {

        return ProjectBeanHolder.FILE_INFO_REPOSITORY;
    }

    public static FileSource getFileSource() {

        return ProjectBeanHolder.FILE_SOURCE;
    }

    public static FileSourceConfiguration getFileSourceConfiguration() {

        return ProjectBeanHolder.FILE_SOURCE_CONFIGURATION;
    }

    public static RandomImageProvider getRandomImageProvider() {

        return ProjectBeanHolder.RANDOM_IMAGE_PROVIDER;
    }

    public static AccountService getAccountService() {

        return ProjectBeanHolder.ACCOUNT_SERVICE;
    }

    public static CacheService getCacheService() {

        return ProjectBeanHolder.CACHE_SERVICE;
    }

    public static HumanValidatorService getHumanValidator() {

        return ProjectBeanHolder.HUMAN_VALIDATOR;
    }

    public static Account getCurrentAccount() {

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Account account) {

            return account;
        }

        return null;
    }

    public static HttpServletRequest getCurrentHttpRequest() {

        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {

            return attributes.getRequest();
        }

        return null;
    }

    public static HttpServletResponse getCurrentHttpResponse() {

        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {

            return attributes.getResponse();
        }

        return null;
    }

    public static ConfigurableApplicationContext getSpringContext() {

        return ProjectBeanHolder.SPRING_CONTEXT;
    }

    public static ProjectConfiguration getProjectConfiguration() {

        return ProjectBeanHolder.PROJECT_CONFIGURATION;
    }

    public static DatasourceConfiguration getDatasourceConfiguration() {

        return  ProjectBeanHolder.DATASOURCE_CONFIGURATION;
    }

    public static RememberMeServices getRememberMeService() {

        return ProjectBeanHolder.REMEMBER_ME_SERVICE;
    }

    public static CacheConfiguration getCacheConfiguration() {

        return  ProjectBeanHolder.CACHE_CONFIGURATION;
    }

    public static RedisConfiguration getRedisConfiguration() {

        return ProjectBeanHolder.REDIS_CONFIGURATION;
    }

    public static SessionConfiguration getSessionConfiguration() {

        return ProjectBeanHolder.SESSION_CONFIGURATION;
    }

    public static SecurityConfiguration getSecurityConfiguration() {

        return ProjectBeanHolder.SECURITY_CONFIGURATION;
    }

    public static GoogleMap getGoogleMap() {

        return ProjectBeanHolder.GOOGLE_MAP;
    }

    public static GoogleConsoleConfiguration getGoogleConsoleConfiguration() {

        return ProjectBeanHolder.GOOGLE_CONSOLE_CONFIGURATION;
    }

    public static TwilioConfiguration getTwilioConfiguration() {

        return ProjectBeanHolder.TWILIO_CONFIGURATION;
    }

    public static ApplicationEventPublisher getApplicationEventPublisher() {

        return ProjectBeanHolder.APPLICATION_EVENT_PUBLISHER;
    }

    public static MessageSource getMessageSource() {

        return ProjectBeanHolder.MESSAGE_SOURCE;
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

    public static PhoneNumberSmsVerifyService getPhoneNumberSmsVerifyService() {

        return ProjectBeanHolder.PHONE_NUMBER_SMS_VERIFY_SERVICE;
    }

    public static GoogleMapService getGoogleMapService() {

        return ProjectBeanHolder.GOOGLE_MAP_SERVICE;
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

    public static RedisIndexedSessionRepository getRedisIndexedSessionRepository() {

        return ProjectBeanHolder.REDIS_INDEXED_SESSION_REPOSITORY;
    }

    public static EntityManager getEntityManager() {

        return ProjectBeanHolder.ENTITY_MANAGER;
    }

    public static ClientRegistrationRepository getClientRegistrationRepository() {

        return ProjectBeanHolder.CLIENT_REGISTRATION_REPOSITORY;
    }

    public static OAuth2AuthorizedClientService getOauth2AuthorizedClientService() {

        return ProjectBeanHolder.OAUTH2_AUTHORIZED_CLIENT_SERVICE;
    }

    public static StoreService getStoreService() {

        return ProjectBeanHolder.STORE_SERVICE;
    }

    public static SessionRegistry getSessionRegistry() {

        return ProjectBeanHolder.SESSION_REGISTRY;
    }

    public static PasswordEncoder getPasswordEncoder() {

        return ProjectBeanHolder.PASSWORD_ENCODER;
    }

    public static AuthenticationManager getAuthenticationManager() {

        return ProjectBeanHolder.AUTHENTICATION_MANAGER;
    }

    public static UserRepository getUserRepository() {

        return ProjectBeanHolder.USER_REPOSITORY;
    }

    public static ExternalUserRepository getExternalUserRepository() {

        return ProjectBeanHolder.EXTERNAL_USER_REPOSITORY;
    }

    public static UserRoleRepository getUserRoleRepository() {

        return ProjectBeanHolder.USER_ROLE_REPOSITORY;
    }

    public static PersistentLoginsRepository getPersistentLoginsRepository() {

        return ProjectBeanHolder.PERSISTENT_LOGINS_REPOSITORY;
    }

    public static RedirectStrategy getRedirectStrategy() {

        return ProjectBeanHolder.REDIRECT_STRATEGY;
    }

    public static OidcUserService getOidcUserService() {

        return ProjectBeanHolder.OIDC_USER_SERVICE;
    }

    public static Messager getMessager() {

        return ProjectBeanHolder.MESSAGER;
    }

    public static JavaMailSender getJavaMailSender() {

        return ProjectBeanHolder.JAVA_MAIL_SENDER;
    }

    public static QRCodeProvider getQRCodeProvider() {

        return ProjectBeanHolder.QR_CODE_PROVIDER;
    }

    public static DataSource getDataSource() {

        return ProjectBeanHolder.DATA_SOURCE;
    }

    public static RedisCacheKeyGenerator getRedisCacheKeyGenerator() {

        return ProjectBeanHolder.REDIS_CACHE_KEY_GENERATOR;
    }

    public static ModelMapper getModelMapper() {

        return ProjectBeanHolder.MODEL_MAPPER;
    }

    public static AddressRepository getAddressRepository() {

        return ProjectBeanHolder.ADDRESS_REPOSITORY;
    }

    public static GoogleTranslatorBundleMessageSource getTranslatorMessageSource() {

        return (GoogleTranslatorBundleMessageSource) ProjectBeanHolder.MESSAGE_SOURCE;
    }

    @Component("autowireLoader")
    public static class AutowireLoader {

        @Autowired
        public void setAccountService(AccountService service) {

            ProjectBeanHolder.ACCOUNT_SERVICE = service;
        }

        @Autowired
        public void setCacheService(CacheService service) {

            ProjectBeanHolder.CACHE_SERVICE = service;
        }

        @Autowired
        public void setConfigurableApplicationContext(ConfigurableApplicationContext context) {

            ProjectBeanHolder.SPRING_CONTEXT = context;
        }

        @Autowired
        public void setProjectConfiguration(ProjectConfiguration config) {

            ProjectBeanHolder.PROJECT_CONFIGURATION = config;
        }

        @Autowired
        public void setRandomImageProvider(RandomImageProvider provider) {

            ProjectBeanHolder.RANDOM_IMAGE_PROVIDER = provider;
        }

        @Autowired
        public void setDatasourceConfiguration(DatasourceConfiguration config) {

            ProjectBeanHolder.DATASOURCE_CONFIGURATION = config;
        }

        @Autowired
        public void setStoreService(StoreService service) {

            ProjectBeanHolder.STORE_SERVICE = service;
        }

        @Autowired
        public void setCacheConfiguration(CacheConfiguration config) {

            ProjectBeanHolder.CACHE_CONFIGURATION = config;
        }

        @Autowired
        public void setRedisConfiguration(RedisConfiguration config) {

            ProjectBeanHolder.REDIS_CONFIGURATION = config;
        }

        @Autowired
        public void setEntityManager(EntityManager manager) {

            ProjectBeanHolder.ENTITY_MANAGER = manager;
        }

        @Autowired
        public void setSessionConfiguration(SessionConfiguration config) {

            ProjectBeanHolder.SESSION_CONFIGURATION = config;
        }

        @Autowired
        public void setSecurityConfiguration(SecurityConfiguration config) {

            ProjectBeanHolder.SECURITY_CONFIGURATION = config;
        }

        @Autowired
        public void setTwilioConfiguration(TwilioConfiguration config) {

            ProjectBeanHolder.TWILIO_CONFIGURATION = config;
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
        public void setAddressRepository(AddressRepository repository) {

            ProjectBeanHolder.ADDRESS_REPOSITORY = repository;
        }

        @Autowired
        public void setScriptEngineManager(ScriptEngineManager manager) {

            ProjectBeanHolder.SCRIPT_ENGINE_MANAGER = manager;
        }

        @Autowired
        public void setHumanValidator(HumanValidatorService validator) {

            ProjectBeanHolder.HUMAN_VALIDATOR = validator;
        }

        @Autowired
        public void setTaskProvider(TaskProvider provider) {

            ProjectBeanHolder.TASK_PROVIDER = provider;
        }

        @Autowired
        public void setRememberMeServices(RememberMeServices services) {

            ProjectBeanHolder.REMEMBER_ME_SERVICE = services;
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
        public void setStoreRepository(StoreRepository repository) {

            ProjectBeanHolder.STORE_REPOSITORY = repository;
        }

        @Autowired
        public void setStoreOrderRepository(StoreOrderRepository repository) {

            ProjectBeanHolder.STORE_ORDER_REPOSITORY = repository;
        }

        @Autowired
        public void setWorkRepository(WorkRepository repository) {

            ProjectBeanHolder.WORK_REPOSITORY = repository;
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

        @Autowired
        public void setPlatformTransactionManager(PlatformTransactionManager manager) {

            ProjectBeanHolder.PLATFORM_TRANSACTION_MANAGER = manager;
        }


        @Deprecated(since = "Until fix @EnableRedisHttpSession")
        @Autowired
        public void setRedisIndexedSessionRepository(RedisIndexedSessionRepository repository) {

            ProjectBeanHolder.REDIS_INDEXED_SESSION_REPOSITORY = repository;
        }

        @Autowired
        public void setFileSourceConfiguration(FileSourceConfiguration configuration) {

            ProjectBeanHolder.FILE_SOURCE_CONFIGURATION = configuration;
        }

        @Autowired
        public void setFileSource(FileSource source) {

            ProjectBeanHolder.FILE_SOURCE = source;
        }

        @Autowired
        public void setFileInfoRepository(FileInfoRepository repository) {

            ProjectBeanHolder.FILE_INFO_REPOSITORY = repository;
        }

        @Autowired
        public void setClientRegistrationRepository(ClientRegistrationRepository repository) {

            ProjectBeanHolder.CLIENT_REGISTRATION_REPOSITORY = repository;
        }

        @Autowired
        public void setOAuth2AuthorizedClientService(OAuth2AuthorizedClientService service) {

            ProjectBeanHolder.OAUTH2_AUTHORIZED_CLIENT_SERVICE = service;
        }

        @Autowired
        public void setSessionRegistry(SessionRegistry registry) {

            ProjectBeanHolder.SESSION_REGISTRY = registry;
        }

        @Autowired
        public void setPasswordEncoder(PasswordEncoder encoder) {

            ProjectBeanHolder.PASSWORD_ENCODER = encoder;
        }

        @Autowired
        public void setAuthenticationManager(AuthenticationManager manager) {

            ProjectBeanHolder.AUTHENTICATION_MANAGER = manager;
        }

        @Autowired
        public void setUserRepository(UserRepository repository) {

            ProjectBeanHolder.USER_REPOSITORY = repository;
        }

        @Autowired
        public void setExternalUserRepository(ExternalUserRepository repository) {

            ProjectBeanHolder.EXTERNAL_USER_REPOSITORY = repository;
        }

        @Autowired
        public void setUserRoleRepository(UserRoleRepository repository) {

            ProjectBeanHolder.USER_ROLE_REPOSITORY = repository;
        }

        @Autowired
        public void setPersistentLoginsRepository(PersistentLoginsRepository repository) {

            ProjectBeanHolder.PERSISTENT_LOGINS_REPOSITORY = repository;
        }

        @Autowired
        public void setRedirectStrategy(RedirectStrategy strategy) {

            ProjectBeanHolder.REDIRECT_STRATEGY = strategy;
        }

        @Autowired
        public void setPhoneSmsVerifyService(PhoneNumberSmsVerifyService service) {

            ProjectBeanHolder.PHONE_NUMBER_SMS_VERIFY_SERVICE = service;
        }

        @Autowired
        public void setGoogleMapService(GoogleMapService service) {

            ProjectBeanHolder.GOOGLE_MAP_SERVICE = service;
        }

        @Autowired
        public void setOidcUserService(OidcUserService service) {

            ProjectBeanHolder.OIDC_USER_SERVICE = service;
        }

        @Autowired
        public void setMessager(Messager messager) {

            ProjectBeanHolder.MESSAGER = messager;
        }

        @Autowired
        public void setJavaMailSender(JavaMailSender sender) {

            ProjectBeanHolder.JAVA_MAIL_SENDER = sender;
        }

        @Autowired
        public void setQRCodeProvider(QRCodeProvider provider) {

            ProjectBeanHolder.QR_CODE_PROVIDER = provider;
        }

        @Autowired
        public void setGoogleMap(GoogleMap map) {

            ProjectBeanHolder.GOOGLE_MAP = map;
        }

        @Autowired
        public void setGoogleMapConfiguration(GoogleConsoleConfiguration configuration) {

            ProjectBeanHolder.GOOGLE_CONSOLE_CONFIGURATION = configuration;
        }

        @Autowired
        public void setDataSource(DataSource source) {

            ProjectBeanHolder.DATA_SOURCE = source;
        }

        @Autowired
        public void setRedisCacheKeyGenerator(RedisCacheKeyGenerator generator) {

            ProjectBeanHolder.REDIS_CACHE_KEY_GENERATOR = generator;
        }

        @Autowired
        public void setModelMapper(ModelMapper mapper) {

            ProjectBeanHolder.MODEL_MAPPER = mapper;
        }

        @Autowired
        public void setMessageSource(MessageSource source) {

            ProjectBeanHolder.MESSAGE_SOURCE = source;
        }
    }

    @Component
    final static class BeanRegister {

        @Bean
        public ModelMapper modelMapperBean() {

            ModelMapper mapper = new ModelMapper();

            return mapper;
        }

        @Bean
        public ObjectMapper objectMapperBean() {

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.registerModule(new DTOModule());
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


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

        @Bean
        public RedirectStrategy redirectStrategyBean() {

            return new DefaultRedirectStrategy();
        }

        @Bean
        public OidcUserService oidcUserServiceBean() {

            return new OidcUserService();
        }

        @Bean
        public Messager messagerBean(TwilioConfiguration config) {

            return new Messager(config);
        }

        @Bean
        public JavaMailSender javaMailSenderBean() {

            return new JavaMailSenderImpl();
        }

        @Bean
        public  QRCodeProvider qrCodeProviderBean() {

            return new QRCodeProvider();
        }

        @Bean
        public RandomImageProvider randomImageProviderBean() {

            return new RandomImageProvider();
        }

        @Bean
        public GoogleMap googleMapBean(GoogleConsoleConfiguration configuration) {

            return new GoogleMap(configuration);
        }

        @Bean
        @Primary
        public MessageSource messageSourceBean(@Autowired FileInfoRepository repository, @Autowired FileSource source, @Autowired CacheService service, @Autowired GoogleConsoleConfiguration configuration) throws IOException {

            return new GoogleTranslatorBundleMessageSource(repository, source, service, configuration);
        }

        @Bean
        public FileSource fileSourceBean(FileInfoRepository repository, FileSourceConfiguration configuration) {

            return new LocalFileSource(repository, configuration);
        }

    }
}
