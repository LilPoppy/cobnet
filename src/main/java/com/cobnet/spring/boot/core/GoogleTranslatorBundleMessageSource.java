package com.cobnet.spring.boot.core;

import com.cobnet.exception.ServiceDownException;
import com.cobnet.interfaces.FileSource;
import com.cobnet.interfaces.spring.repository.FileInfoRepository;
import com.cobnet.spring.boot.configuration.GoogleConsoleConfiguration;
import com.cobnet.spring.boot.entity.FileInfo;
import com.cobnet.spring.boot.service.CacheService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateException;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.testing.RemoteTranslateHelper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.util.DefaultPropertiesPersister;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.*;

public class GoogleTranslatorBundleMessageSource extends ResourceBundleMessageSource {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleTranslatorBundleMessageSource.class);
    private static final String DEFAULT_BASENAME = "locale/messages";

    private static final String DEFAULT_MEME_TYPE = ".properties";

    private static final String DEFAULT_COMMENT = """
            ======={0}=======
            Google Translated.""";

    public static final String CACHE_NAMESPACE = GoogleTranslatorBundleMessageSource.class.getSimpleName();

    private final FileInfoRepository repository;

    private final FileSource source;

    private final CacheService service;

    private final Translate translate;

    GoogleTranslatorBundleMessageSource(FileInfoRepository repository, FileSource source, CacheService service, GoogleConsoleConfiguration configuration) throws IOException {

        super();

        this.repository = repository;
        this.source = source;
        this.service = service;

        RemoteTranslateHelper helper = RemoteTranslateHelper.create();

        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(configuration.getCredentials())).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        this.translate = helper.getOptions().toBuilder().setCredentials(credentials).build().getService();
        this.setBasename(DEFAULT_BASENAME);
        this.setUseCodeAsDefaultMessage(true);
        this.setDefaultLocale(new Locale("en", "US"));
        this.setAlwaysUseMessageFormat(true);
        for(Locale locale : Locale.getAvailableLocales()) {

            String name = this.getBaseName(locale);

            Optional<FileInfo> info = repository.findFileInfoByName(name);

            if(info.isPresent()) {

                Properties properties = this.readFromCache(locale);

                if(properties != null) {

                    Properties temp = this.readFromFile(locale);

                    if(temp != null) {

                        properties.putAll(temp);
                    }

                } else {

                    properties = this.readFromFile(locale);
                }

                if(properties != null) {

                    this.writeToCache(locale, properties);
                }
            }
        }
    }

    private boolean hasKeyInBundle(String key, Locale locale) {

        return this.getBasenameSet().stream().anyMatch(basename -> {

            ResourceBundle bundle = this.getResourceBundle(basename, locale);

            return bundle != null && bundle.getLocale().equals(locale) && bundle.containsKey(key);

        });
    }

    private boolean hasKeyInCache(String key, Locale locale) {

        Properties properties = this.readFromCache(locale);

        if(properties == null) {

            return false;
        }

        return properties.containsKey(key);
    }

    public boolean hasKey(String key, Locale locale) {

        return hasKeyInBundle(key, locale) || hasKeyInCache(key, locale);
    }

    public String getMessage(String message, Object... args) throws IOException, ServiceDownException {

        return this.getMessage(message, message, null, args);
    }

    public String getMessage(String message, Locale locale, Object... args) throws IOException, ServiceDownException {

        return this.getMessage(message, message, locale, args);
    }

    @Nullable
    protected String getMessageInternal(@Nullable String code, @Nullable Object[] args, @Nullable Locale locale) {

        if(hasKeyInBundle(code, locale)) {

            return super.getMessageInternal(code, args, locale);
        }

        Properties properties = this.readFromCache(locale);

        if(properties != null) {

            String result = properties.getProperty(code);

            if(result != null) {

                return new MessageFormat(result).format(args);
            }

            try {

                Properties temp = this.readFromFile(locale);

                if(temp != null) {

                    properties.putAll(temp);
                }

                result = properties.getProperty(code);

                if(result != null) {

                    this.writeToCache(locale, properties);
                    this.writeToFile(locale, properties);

                    return new MessageFormat(result).format(args);
                }

            } catch (IOException e) {

                throw new RuntimeException(e);
            }

        } else {

            try {

                properties = this.readFromFile(locale);

                if(properties != null) {

                    String result = properties.getProperty(code);

                    if (result != null) {

                        this.writeToCache(locale, properties);
                    }

                    return new MessageFormat(result).format(args);
                }

            } catch (IOException e) {

                throw new RuntimeException(e);
            }

        }

        if(locale != this.getDefaultLocale()) {

            return this.getMessageInternal(code, args, this.getDefaultLocale());
        }

        return super.getMessageInternal(code, args, locale);
    }

    public String getMessage(String key, String defaultValue, Locale locale, Object... args) throws IOException, ServiceDownException {

        if(locale == null) {

            locale = this.getDefaultLocale();
        }

        if(hasKeyInBundle(key, locale)) {

            return super.getMessage(key, args, locale);
        }

        String message = super.resolveCodeWithoutArguments(key, locale);

        if (message == null) {

            message = defaultValue;
        }

        if(message == null) {

            message = super.getMessageInternal(key, args, this.getDefaultLocale());
        }

        if (message == null) {

            message = this.getDefaultMessage(key);
        }

        try {

            Translation translation = this.translate.translate(message, Translate.TranslateOption.targetLanguage(locale.getLanguage()));

            Properties properties = this.readFromCache(locale);

            if(properties == null) {

                properties = new Properties();
            }

            properties.put(key, translation.getTranslatedText());

            this.writeToCache(locale, properties);

            return new MessageFormat(translation.getTranslatedText()).format(args);

        } catch (TranslateException exception) {

            if(LOG.isTraceEnabled() || LOG.isDebugEnabled()) {

                exception.addSuppressed(new ServiceDownException(this.getClass()));
                exception.printStackTrace();
            }

            return super.getMessage(key, args, defaultValue, this.getDefaultLocale());
        }
    }

    protected boolean writeToFile(@NotNull Locale locale, @NotNull Properties properties) throws IOException {

        if(locale == null || properties == null) {

            if(LOG.isDebugEnabled()) {

                LOG.error("Locale or properties cannot be null!", new NullPointerException());
            }

            return false;
        }

        try(ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            properties.store(output, GoogleTranslatorBundleMessageSource.DEFAULT_COMMENT);

            byte[] bs = output.toByteArray();

            try(ByteArrayInputStream input = new ByteArrayInputStream(bs)) {

                source.write(input, getFileInfo(locale, bs.length));
            }
        }

        return true;
    }

    public boolean writeToFile(@NotNull Locale locale) throws IOException {

        if(locale == null) {

            if(LOG.isDebugEnabled()) {

                LOG.error("Locale cannot be null!", new NullPointerException());
            }

            return false;
        }

        return this.writeToFile(locale, this.readFromCache(locale));
    }

    public Properties readFromFile(@NotNull Locale locale) throws IOException {

        if(locale == null) {

            return null;
        }

        Properties properties = new Properties();

        try(InputStream stream = source.read(getFileInfo(locale, -1))) {

            if(stream == null) {

                return null;
            }

            properties.load(stream);
        }

        return properties;
    }

    protected boolean writeToCache(@NotNull Locale locale, @NotNull Properties properties) {

        if(locale == null || properties == null) {

            return false;
        }

        return service.set(GoogleTranslatorBundleMessageSource.CACHE_NAMESPACE, locale, properties, this.getCacheMillis() > -1 ? Duration.ofMillis(this.getCacheMillis()) : Duration.ofDays(360));
    }

    protected Properties readFromCache(@NotNull Locale locale) {

        if(locale == null) {

            return null;
        }

        return service.get(GoogleTranslatorBundleMessageSource.CACHE_NAMESPACE, locale, Properties.class);
    }

    private FileInfo getFileInfo(Locale locale, long size) {

        Optional<FileInfo> optional = repository.findFileInfoByName(this.getBaseName(locale));

        FileInfo info = optional.orElseGet(() -> {

            FileInfo temp = new FileInfo();
            temp.setName(this.getBaseName(locale));
            temp.setMemeType(GoogleTranslatorBundleMessageSource.DEFAULT_MEME_TYPE);
            return temp;
        });

        if(size > -1) {

            info.setSize(size);
        }

        repository.save(info);

        return info;
    }

    private String getBaseName(Locale locale) {

        return GoogleTranslatorBundleMessageSource.DEFAULT_BASENAME + "_" + locale.getLanguage() + "_" + locale.getCountry() + GoogleTranslatorBundleMessageSource.DEFAULT_MEME_TYPE;
    }

}
