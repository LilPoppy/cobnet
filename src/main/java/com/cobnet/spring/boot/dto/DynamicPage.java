package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.Page;
import com.cobnet.interfaces.connection.web.PageField;

import java.util.Properties;

public class DynamicPage implements Page {

    private Properties properties;

    private PageField[] fields;

    public DynamicPage(Properties properties, PageField... fields) {

        this.properties = properties;
        this.fields = fields;
    }

    @Override
    public PageField[] getFields() {
        return this.fields;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }
}
