package com.cobnet.spring.boot.core;

import com.cobnet.exception.ServiceDownException;
import com.cobnet.spring.boot.configuration.GoogleConsoleConfiguration;
import com.google.api.client.http.HttpResponseException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.testing.RemoteTranslateHelper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.*;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

public class GoogleTranslatorBundleMessageSource extends ResourceBundleMessageSource {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleTranslatorBundleMessageSource.class);
    public static final String DEFAULT_BASENAME = "locale/messages";

    private Translate translate;

    GoogleTranslatorBundleMessageSource(@Autowired GoogleConsoleConfiguration configuration) {

        super();

        try {

            RemoteTranslateHelper helper = RemoteTranslateHelper.create();

            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(configuration.getCredentials())).createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

            this.translate = helper.getOptions().toBuilder().setCredentials(credentials).build().getService();

        } catch (IOException ex) {

            LOG.error("Check application.yml of google.console.credentials to resolve %s", ex.getMessage());
        }

        this.setBasename(DEFAULT_BASENAME);
    }

    public boolean hasKey(String key, Locale locale) {

        return this.getBasenameSet().stream().anyMatch(basename -> {

            ResourceBundle bundle = this.getResourceBundle(basename, locale);

            return bundle != null && bundle.getLocale().equals(locale) && bundle.containsKey(key);
        });
    }

    public String getMessage(String key, Locale locale, Object... args) throws IOException, ServiceDownException {

        return this.getMessage(key, null, locale, args);
    }

    public String getMessage(String key, String defaultValue, Locale locale, Object... args) throws IOException, ServiceDownException {

        if(hasKey(key, locale)) {

            return super.getMessage(key, args, locale);
        }

        String message = super.resolveCodeWithoutArguments(key, Objects.requireNonNull(this.getDefaultLocale()));

        if (message == null) {

            message = defaultValue;
        }

        if (message == null) {

            message = this.getDefaultMessage(key);
        }

        if(this.translate == null) {

            throw new ServiceDownException(this.getClass(), this.translate);
        }

        Translation translation = this.translate.translate(message, Translate.TranslateOption.targetLanguage(locale.getLanguage()));

        this.add(DEFAULT_BASENAME, key, translation.getTranslatedText(), locale);

        return String.format(translation.getTranslatedText(), args);
    }

    public void add(String basename, String key, String value, Locale locale) throws IOException {

        Properties properties = new Properties();

        URL url = this.getClass().getResource( "/" + basename + "_" + locale.getLanguage() + "_" + locale.getCountry() + ".properties");

        assert url != null;

        File file = new File(url.getFile());

        if(file.exists() || file.createNewFile()) {

            try(InputStream stream = new FileInputStream(file)) {

                properties.load(stream);
            }

            properties.setProperty(key, value);

            DefaultPropertiesPersister persister = new DefaultPropertiesPersister();

            try(OutputStream stream = new FileOutputStream(file)) {

                persister.store(properties, stream, "Google Translated.");
            }
        }
    }

}
