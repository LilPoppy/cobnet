package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.PageField;
import com.cobnet.spring.boot.dto.support.PageFieldType;

import java.util.Properties;

public class DynamicPageField implements PageField {

    private PageFieldType type;

    private Properties properties;

    private PageField[] children;

    public DynamicPageField(PageFieldType type, Properties properties, PageField... children) {

        this.type = type;
        this.properties = properties;
        this.children = children;
    }

    public PageFieldType getType() {
        return type;
    }

    public Properties getProperties() {
        return properties;
    }

    public PageField[] getChildren() {
        return children;
    }

    public void setType(PageFieldType type) {
        this.type = type;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setChildren(PageField[] children) {
        this.children = children;
    }
}
