package com.cobnet.interfaces.connection.web;

import com.cobnet.spring.boot.dto.support.PageFieldType;

import java.util.Properties;

public interface PageField extends ApplicationJson {

    public PageFieldType getType();

    public PageField[] getChildren();

    public Properties getProperties();
}
